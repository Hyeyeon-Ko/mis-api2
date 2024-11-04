package kr.or.kmi.mis.api.seal.service.impl;

import kr.or.kmi.mis.api.seal.model.entity.SealRegisterDetail;
import kr.or.kmi.mis.api.seal.model.request.SealRegisterRequestDTO;
import kr.or.kmi.mis.api.seal.model.response.SealDetailResponseDTO;
import kr.or.kmi.mis.api.seal.repository.SealRegisterDetailRepository;
import kr.or.kmi.mis.api.seal.service.SealRegisterHistoryService;
import kr.or.kmi.mis.api.seal.service.SealRegisterService;
import kr.or.kmi.mis.api.seal.util.ImageUtil;
import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import kr.or.kmi.mis.api.std.repository.StdDetailRepository;
import kr.or.kmi.mis.api.std.repository.StdGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SealRegisterServiceImpl implements SealRegisterService {

    private final SealRegisterDetailRepository sealRegisterDetailRepository;
    private final SealRegisterHistoryService sealRegisterHistoryService;
    private final StdGroupRepository stdGroupRepository;
    private final StdDetailRepository stdDetailRepository;

    @Transactional
    public void registerSeal(SealRegisterRequestDTO sealRegisterRequestDTO, MultipartFile sealImage) throws IOException {

        String base64EncodedImage = null;
        String sealImageNm = null;
        if (sealImage != null && !sealImage.isEmpty()) {
            base64EncodedImage = ImageUtil.encodeImageToBase64(sealImage);
            sealImageNm = sealImage.getOriginalFilename();
        }

        sealRegisterRequestDTO.setSealImageBase64(base64EncodedImage);
        sealRegisterRequestDTO.setSealImageNm(sealImageNm);

        String draftId = generateDraftId();

        SealRegisterDetail sealRegisterDetail = sealRegisterRequestDTO.toDetailEntity(draftId);
        sealRegisterDetail.setRgstrId(sealRegisterRequestDTO.getDrafterId());
        sealRegisterDetail.setRgstDt(LocalDateTime.now());
        sealRegisterDetailRepository.save(sealRegisterDetail);
    }

    private String generateDraftId() {
        Optional<SealRegisterDetail> lastSealRegisterDetailOpt = sealRegisterDetailRepository.findTopByOrderByDraftIdDesc();

        StdGroup stdGroup = stdGroupRepository.findByGroupCd("A007")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        StdDetail stdDetail = stdDetailRepository.findByGroupCdAndDetailCd(stdGroup, "G")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        if (lastSealRegisterDetailOpt.isPresent()) {
            String lastDraftId = lastSealRegisterDetailOpt.get().getDraftId();
            int lastIdNum = Integer.parseInt(lastDraftId.substring(2));
            return stdDetail.getEtcItem1() + String.format("%010d", lastIdNum + 1);
        } else {
            return stdDetail.getEtcItem1() + "0000000001";
        }
    }

    @Override
    @Transactional
    public void deleteSeal(String draftId) {
        SealRegisterDetail sealRegisterDetail = sealRegisterDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        sealRegisterHistoryService.createSealRegisterHistory(sealRegisterDetail);

        sealRegisterDetail.deleteSeal(LocalDateTime.now());
        sealRegisterDetailRepository.save(sealRegisterDetail);
    }

    @Override
    public SealDetailResponseDTO getSealDetail(String draftId) {
        SealRegisterDetail sealRegisterDetail = sealRegisterDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        return SealDetailResponseDTO.of(sealRegisterDetail);
    }
}
