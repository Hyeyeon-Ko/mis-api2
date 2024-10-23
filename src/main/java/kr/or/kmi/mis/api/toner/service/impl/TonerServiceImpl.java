package kr.or.kmi.mis.api.toner.service.impl;

import kr.or.kmi.mis.api.exception.EntityNotFoundException;
import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import kr.or.kmi.mis.api.std.repository.StdDetailRepository;
import kr.or.kmi.mis.api.std.repository.StdGroupRepository;
import kr.or.kmi.mis.api.toner.model.entity.TonerDetail;
import kr.or.kmi.mis.api.toner.model.entity.TonerInfo;
import kr.or.kmi.mis.api.toner.model.entity.TonerMaster;
import kr.or.kmi.mis.api.toner.model.request.TonerApplyRequestDTO;
import kr.or.kmi.mis.api.toner.model.request.TonerPriceDTO;
import kr.or.kmi.mis.api.toner.model.response.TonerApplyResponseDTO;
import kr.or.kmi.mis.api.toner.model.response.TonerInfo2ResponseDTO;
import kr.or.kmi.mis.api.toner.model.response.TonerMngResponseDTO;
import kr.or.kmi.mis.api.toner.repository.TonerDetailRepository;
import kr.or.kmi.mis.api.toner.repository.TonerInfoRepository;
import kr.or.kmi.mis.api.toner.repository.TonerMasterRepository;
import kr.or.kmi.mis.api.toner.repository.TonerPriceRepository;
import kr.or.kmi.mis.api.toner.service.TonerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class TonerServiceImpl implements TonerService {

    private final TonerInfoRepository tonerInfoRepository;
    private final TonerPriceRepository tonerPriceRepository;
    private final TonerMasterRepository tonerMasterRepository;
    private final TonerDetailRepository tonerDetailRepository;

    private final StdGroupRepository stdGroupRepository;
    private final StdDetailRepository stdDetailRepository;

    @Override
    @Transactional(readOnly = true)
    public TonerMngResponseDTO getMngInfo(String instCd) {
        List<TonerInfo> tonerInfoList = tonerInfoRepository.findAllByInstCd(instCd);
        List<String> mngNums = tonerInfoList.stream().map(TonerInfo::getMngNum).toList();

        return new TonerMngResponseDTO(mngNums);
    }

    @Override
    @Transactional(readOnly = true)
    public TonerInfo2ResponseDTO getTonerInfo(String mngNum) {
        TonerInfo tonerInfo = tonerInfoRepository.findById(mngNum)
                .orElseThrow(() -> new EntityNotFoundException("Not found: " + mngNum));

        List<TonerPriceDTO> tonerPriceList = Arrays.stream(tonerInfo.getTonerNm().split(","))
                .map(String::trim)
                .map(tonerName -> tonerPriceRepository.findByTonerNm(tonerName)
                        .orElseThrow(() -> new EntityNotFoundException("Not found: " + tonerName)))
                .map(tonerPrice -> TonerPriceDTO.of(tonerPrice.getTonerNm(), tonerPrice.getPrice()))
                .toList();

        return TonerInfo2ResponseDTO.of(tonerInfo, tonerPriceList);
    }

    @Override
    @Transactional(readOnly = true)
    public TonerApplyResponseDTO getTonerApply(String draftId) {
        return null;
    }

    @Override
    @Transactional
    public void applyToner(TonerApplyRequestDTO tonerApplyRequestDTO) {
        String draftId = generateDraftId();

        // 1. tonerMaster 저장
        TonerMaster tonerMaster = tonerApplyRequestDTO.toMasterEntity(draftId);
        tonerMaster.setRgstrId(tonerApplyRequestDTO.getDrafterId());
        tonerMaster.setRgstDt(LocalDateTime.now());
        tonerMasterRepository.save(tonerMaster);

        // 2. tonerDetail 저장
        AtomicLong itemId = new AtomicLong(1L);

        tonerApplyRequestDTO.getTonerDetailDTOs()
                .forEach(tonerDetailDTO -> {

                    // 2-1) tonerDetail 객체 생성
                    TonerDetail tonerDetail = TonerDetail.builder()
                            .itemId(itemId.getAndIncrement())
                            .draftId(draftId)
                            .mngNum(tonerDetailDTO.getMngNum())
                            .teamNm(tonerDetailDTO.getTeamNm())
                            .location(tonerDetailDTO.getLocation())
                            .printNm(tonerDetailDTO.getPrintNm())
                            .tonerNm(tonerDetailDTO.getTonerNm())
                            .price(tonerDetailDTO.getPrice())
                            .quantity(tonerDetailDTO.getQuantity())
                            .totalPrice(tonerDetailDTO.getTotalPrice())
                            .build();

                    // 2-2) 상세정보 저장
                    tonerDetailRepository.save(tonerDetail);
        });

    }

    private String generateDraftId() {
        Optional<TonerMaster> lastTonerMasterOpt = tonerMasterRepository.findTopByOrderByDraftIdDesc();

        StdGroup stdGroup = stdGroupRepository.findByGroupCd("A007")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        StdDetail stdDetail = stdDetailRepository.findByGroupCdAndDetailCd(stdGroup, "H")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        if (lastTonerMasterOpt.isPresent()) {
            String lastDraftId = lastTonerMasterOpt.get().getDraftId();
            int lastIdNum = Integer.parseInt(lastDraftId.substring(2));
            return stdDetail.getEtcItem1() + String.format("%010d", lastIdNum + 1);
        } else {
            return stdDetail.getEtcItem1() + "0000000001";
        }
    }

    @Override
    @Transactional
    public void updateTonerApply(TonerApplyRequestDTO tonerRequestDTO) {

    }

    @Override
    @Transactional
    public void cancelTonerApply(String draftId) {
        TonerMaster tonerMaster = tonerMasterRepository.findById(draftId)
                .orElseThrow(() -> new EntityNotFoundException("Not found: " + draftId));
        tonerMaster.updateStatus("F");
    }
}
