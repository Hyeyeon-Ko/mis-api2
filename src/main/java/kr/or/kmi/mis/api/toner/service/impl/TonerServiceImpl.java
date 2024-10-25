package kr.or.kmi.mis.api.toner.service.impl;

import kr.or.kmi.mis.api.apply.model.request.ApplyRequestDTO;
import kr.or.kmi.mis.api.apply.model.response.ApplyResponseDTO;
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
import kr.or.kmi.mis.api.toner.model.request.TonerUpdateRequestDTO;
import kr.or.kmi.mis.api.toner.model.response.*;
import kr.or.kmi.mis.api.toner.repository.*;
import kr.or.kmi.mis.api.toner.service.TonerService;
import kr.or.kmi.mis.api.user.service.InfoService;
import kr.or.kmi.mis.cmm.model.request.PostSearchRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TonerServiceImpl implements TonerService {

    private final TonerInfoRepository tonerInfoRepository;
    private final TonerPriceRepository tonerPriceRepository;
    private final TonerMasterRepository tonerMasterRepository;
    private final TonerDetailRepository tonerDetailRepository;

    private final StdGroupRepository stdGroupRepository;
    private final StdDetailRepository stdDetailRepository;

    private final TonerApplyQueryRepository tonerApplyQueryRepository;

    private final InfoService infoService;

    /**
     * 센터별 관리번호 항목들을 TonerMngResponseDTO로 반환.
     * @param instCd 센터코드
     * @return TonerMngResponseDTO
     */
    @Override
    @Transactional(readOnly = true)
    public TonerMngResponseDTO getMngInfo(String instCd) {
        List<TonerInfo> tonerInfoList = tonerInfoRepository.findAllByInstCd(instCd);
        List<String> mngNums = tonerInfoList.stream().map(TonerInfo::getMngNum).toList();

        return new TonerMngResponseDTO(mngNums);
    }

    /**
     * 나의 토너 신청내역 TonerMyResponseDTO 리스트로 반환.
     * @param applyRequestDTO
     * @param postSearchRequestDTO
     * @return TonerMyResponseDTO 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<TonerMyResponseDTO> getMyTonerApply(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO) {
        return tonerApplyQueryRepository.getMyTonerApply(applyRequestDTO, postSearchRequestDTO);
    }

    /**
     * 나의 토너 신청내역 TonerMyResponseDTO 페이지로 반환.
     * @param applyRequestDTO
     * @param postSearchRequestDTO
     * @param pageable
     * @return TonerMyResponseDTO 페이지
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TonerMyResponseDTO> getMyTonerApply2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable pageable) {
        return tonerApplyQueryRepository.getMyTonerApply2(applyRequestDTO, postSearchRequestDTO, pageable);
    }

    /**
     * 나의 토너 승인대기내역 TonerPendingListResponseDTO 리스트로 반환.
     * @param applyRequestDTO
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public List<TonerPendingListResponseDTO> getMyTonerPendingList(ApplyRequestDTO applyRequestDTO) {
        return new ArrayList<>(this.getMyTonerPendingMasterList(applyRequestDTO.getUserId()));
    }

    public LocalDateTime convertStringToLocalDateTime(String dateString) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            return LocalDateTime.parse(dateString, formatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public List<TonerPendingListResponseDTO> getMyTonerPendingMasterList(String userId) {
        List<TonerMaster> tonerMasterList = tonerMasterRepository.findByDrafterIdAndStatus(userId, "A")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        return tonerMasterList.stream()
                .map(tonerMaster -> {
                    String updaterId = tonerMaster.getUpdtrId();
                    String updaterNm = updaterId != null ? infoService.getUserInfoDetail(updaterId).getUserName() : null;
                    return TonerPendingListResponseDTO.of(tonerMaster, updaterNm);
                }).toList();
    }

    /**
     * 토너 전체 신청내역 TonerMasterResponseDTO 페이지로 반환.
     * @param applyRequestDTO
     * @param postSearchRequestDTO
     * @param page
     * @return TonerMasterResponseDTO 페이지
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TonerMasterResponseDTO> getTonerApply2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page) {
        return tonerApplyQueryRepository.getTonerApply2(applyRequestDTO, postSearchRequestDTO, page);
    }

    /**
     * 토너 상세정보 TonerInfo2ResponseDTO 리스트로 반환.
     * @param mngNum 조회할 관리번호
     * @return TonerInfo2ResponseDTO
     */
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

    /**
     * 상세 정보 조회 또는 수정을 위해 신청 항목 반환.
     * @param draftId 조회할 신청건의 기안번호
     * @return TonerInfo2ResponseDTO 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<TonerApplyResponseDTO> getTonerApply(String draftId) {

        // 1. tonerDetailList 조회
        List<TonerDetail> tonerDetailList = tonerDetailRepository.findAllByDraftId(draftId);

        // 2. TonerApplyResponstDTO 반환
        return tonerDetailList.stream()
                .map(TonerApplyResponseDTO::of)
                .collect(Collectors.toList());
    }

    /**
     * 토너 신청.
     * @param tonerApplyRequestDTO 토너 신청 정보
     */
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
                    TonerDetail.TonerDetailBuilder detailBuilder = TonerDetail.builder()
                            .itemId(itemId.getAndIncrement())
                            .draftId(draftId)
                            .mngNum(tonerDetailDTO.getMngNum())
                            .teamNm(tonerDetailDTO.getTeamNm())
                            .location(tonerDetailDTO.getLocation())
                            .printNm(tonerDetailDTO.getPrintNm())
                            .tonerNm(tonerDetailDTO.getTonerNm())
                            .price(tonerDetailDTO.getPrice())
                            .quantity(tonerDetailDTO.getQuantity())
                            .totalPrice(tonerDetailDTO.getTotalPrice());

                    if (itemId.get() > 1) {
                        detailBuilder.holding("T");
                    }

                    TonerDetail tonerDetail = detailBuilder.build();

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

    /**
     * 토너 신청 수정.
     * @param draftId 수정할 신청건의 기안번호
     * @param tonerUpdateRequestDTO 토너 신청 수정 정보
     */
    @Override
    @Transactional
    public void updateTonerApply(String draftId, TonerUpdateRequestDTO tonerUpdateRequestDTO) {

        // 1. tonerMaster 조회
        TonerMaster tonerMaster = tonerMasterRepository.findById(draftId)
                .orElseThrow(() -> new EntityNotFoundException("Toner Master Not Found: " + draftId));

        // 2. 기존의 tonerDetail 리스트 조회
        List<TonerDetail> existingTonerDetails = tonerDetailRepository.findAllByDraftId(draftId);

        Map<Long, TonerDetail> existingDetailMap = existingTonerDetails.stream()
                .collect(Collectors.toMap(TonerDetail::getItemId, detail -> detail));

        // 3. if) 신규 항목 추가 시 사용할 itemId 값 계산
        AtomicLong maxItemId = new AtomicLong(existingTonerDetails.stream()
                .mapToLong(TonerDetail::getItemId)
                .max()
                .orElse(1L));

        // 4. 항목 수정 또는 새 항목 추가
        Set<Long> incomingItemIds = tonerUpdateRequestDTO.getTonerDetailDTOs().stream()
                .map(tonerDetailDTO -> {
                    if (tonerDetailDTO.getItemId() != null) {
                        TonerDetail existingDetail = existingDetailMap.get(tonerDetailDTO.getItemId());
                        if (existingDetail != null) {
                            existingDetail.tonerDetailUpdate(tonerDetailDTO);
                            tonerDetailRepository.save(existingDetail);
                        } else {
                            throw new EntityNotFoundException("Toner Detail Not Found: " + tonerDetailDTO.getItemId());
                        }
                    } else {
                        TonerDetail.TonerDetailBuilder newTonerDetail = TonerDetail.builder()
                                .itemId(maxItemId.incrementAndGet())
                                .draftId(draftId)
                                .mngNum(tonerDetailDTO.getMngNum())
                                .teamNm(tonerDetailDTO.getTeamNm())
                                .location(tonerDetailDTO.getLocation())
                                .printNm(tonerDetailDTO.getPrintNm())
                                .tonerNm(tonerDetailDTO.getTonerNm())
                                .price(tonerDetailDTO.getPrice())
                                .quantity(tonerDetailDTO.getQuantity())
                                .totalPrice(tonerDetailDTO.getTotalPrice());

                        if (maxItemId.incrementAndGet() > 1) {
                            newTonerDetail.holding("T");
                        }

                        TonerDetail tonerDetail = newTonerDetail.build();

                        tonerDetailRepository.save(tonerDetail);
                    }
                    return tonerDetailDTO.getItemId();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 5. 기존의 tonerDetail 항목 중에서 들어온 요청에 없는 itemId 항목 삭제
        existingTonerDetails.stream()
                .filter(detail -> !incomingItemIds.contains(detail.getItemId()))
                .forEach(tonerDetailRepository::delete);

        // 6. tonerMaster 업데이트
        tonerMaster.setUpdtDt(LocalDateTime.now());
        tonerMaster.setUpdtrId(tonerUpdateRequestDTO.getDrafterId());
        tonerMasterRepository.save(tonerMaster);
    }

    /**
     * 토너 신청 취소
     * @param draftId 취소할 신청건의 기안번호
     */
    @Override
    @Transactional
    public void cancelTonerApply(String draftId) {
        TonerMaster tonerMaster = tonerMasterRepository.findById(draftId)
                .orElseThrow(() -> new EntityNotFoundException("Not found: " + draftId));
        tonerMaster.updateStatus("F");
    }
}
