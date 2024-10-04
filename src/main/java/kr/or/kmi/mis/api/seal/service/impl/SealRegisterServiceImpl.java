package kr.or.kmi.mis.api.seal.service.impl;

import kr.or.kmi.mis.api.corpdoc.model.entity.CorpDocMaster;
import kr.or.kmi.mis.api.seal.repository.SealMasterRepository;
import kr.or.kmi.mis.api.seal.util.ImageUtil;
import kr.or.kmi.mis.api.seal.model.entity.SealRegisterDetail;
import kr.or.kmi.mis.api.seal.model.request.SealRegisterRequestDTO;
import kr.or.kmi.mis.api.seal.model.request.SealUpdateRequestDTO;
import kr.or.kmi.mis.api.seal.model.response.SealDetailResponseDTO;
import kr.or.kmi.mis.api.seal.repository.SealRegisterDetailRepository;
import kr.or.kmi.mis.api.seal.service.SealRegisterHistoryService;
import kr.or.kmi.mis.api.seal.service.SealRegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SealRegisterServiceImpl implements SealRegisterService {

    private final SealRegisterDetailRepository sealRegisterDetailRepository;
    private final SealRegisterHistoryService sealRegisterHistoryService;
    private final SealMasterRepository sealMasterRepository;

    @Transactional
    public void registerSeal(SealRegisterRequestDTO sealRegisterRequestDTO, MultipartFile sealImage) throws IOException {

        String base64EncodedImage = null;
        if (sealImage != null && !sealImage.isEmpty()) {
            base64EncodedImage = ImageUtil.encodeImageToBase64(sealImage);
        }

        sealRegisterRequestDTO.setSealImageBase64(base64EncodedImage);

        String draftId = generateDraftId();

        SealRegisterDetail sealRegisterDetail = sealRegisterRequestDTO.toDetailEntity(draftId);
        sealRegisterDetail.setRgstrId(sealRegisterRequestDTO.getDrafterId());
        sealRegisterDetail.setRgstDt(LocalDateTime.now());
        sealRegisterDetailRepository.save(sealRegisterDetail);
    }

    private String generateDraftId() {
        Optional<SealRegisterDetail> lastSealRegisterDetailOpt = sealRegisterDetailRepository.findTopByOrderByDraftIdDesc();

        if (lastSealRegisterDetailOpt.isPresent()) {
            String lastDraftId = lastSealRegisterDetailOpt.get().getDraftId();
            int lastIdNum = Integer.parseInt(lastDraftId.substring(2));
            return "sr" + String.format("%010d", lastIdNum + 1);
        } else {
            // TODO: draftId 관련 기준자료 추가 후 수정!!!
            return "sr0000000001";
        }
    }

    @Override
    @Transactional
    public void updateSeal(String draftId, SealUpdateRequestDTO sealUpdateRequestDTO, MultipartFile sealImage, boolean isFileDeleted) throws IOException {
        SealRegisterDetail sealRegisterDetail = sealRegisterDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        sealRegisterHistoryService.createSealRegisterHistory(sealRegisterDetail);

        String base64EncodedImage;
        if (sealImage != null && !sealImage.isEmpty()) {
            base64EncodedImage = ImageUtil.encodeImageToBase64(sealImage);
        } else if (isFileDeleted) {
            base64EncodedImage = null;
        } else {
            base64EncodedImage = sealRegisterDetail.getSealImage();
        }

        sealRegisterDetail.updateFile(sealUpdateRequestDTO, base64EncodedImage);

        sealRegisterDetailRepository.save(sealRegisterDetail);
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
