package kr.or.kmi.mis.api.seal.service.impl;

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

@Service
@RequiredArgsConstructor
public class SealRegisterServiceImpl implements SealRegisterService {

    private final SealRegisterDetailRepository sealRegisterDetailRepository;
    private final SealRegisterHistoryService sealRegisterHistoryService;

    @Transactional
    public void registerSeal(SealRegisterRequestDTO sealRegisterRequestDTO, MultipartFile sealImage) throws IOException {

        String base64EncodedImage = null;
        if (sealImage != null && !sealImage.isEmpty()) {
            base64EncodedImage = ImageUtil.encodeImageToBase64(sealImage);
        }

        sealRegisterRequestDTO.setSealImageBase64(base64EncodedImage);

        SealRegisterDetail sealRegisterDetail = sealRegisterRequestDTO.toDetailEntity();
        sealRegisterDetail.setRgstrId(sealRegisterRequestDTO.getDrafterId());
        sealRegisterDetail.setRgstDt(new Timestamp(System.currentTimeMillis()));
        sealRegisterDetailRepository.save(sealRegisterDetail);

        sealRegisterHistoryService.createSealRegisterHistory(sealRegisterDetail);
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

        sealRegisterDetail.deleteSeal(new Timestamp(System.currentTimeMillis()));
        sealRegisterDetailRepository.save(sealRegisterDetail);
    }

    @Override
    public SealDetailResponseDTO getSealDetail(String draftId) {
        SealRegisterDetail sealRegisterDetail = sealRegisterDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        return SealDetailResponseDTO.of(sealRegisterDetail);
    }
}
