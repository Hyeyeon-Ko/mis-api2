package kr.or.kmi.mis.api.docstorage.service.impl;

import kr.or.kmi.mis.api.docstorage.domain.entity.DocStorageDetail;
import kr.or.kmi.mis.api.docstorage.domain.entity.DocStorageMaster;
import kr.or.kmi.mis.api.docstorage.domain.request.DocStorageApplyRequestDTO;
import kr.or.kmi.mis.api.docstorage.domain.request.DocStorageRequestDTO;
import kr.or.kmi.mis.api.docstorage.domain.request.DocStorageUpdateRequestDTO;
import kr.or.kmi.mis.api.docstorage.domain.response.DocStorageDetailResponseDTO;
import kr.or.kmi.mis.api.docstorage.repository.DocStorageDetailRepository;
import kr.or.kmi.mis.api.docstorage.repository.DocStorageMasterRepository;
import kr.or.kmi.mis.api.docstorage.service.DocStorageService;
import kr.or.kmi.mis.api.user.service.InfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class DocStorageServiceImpl implements DocStorageService {

    private final DocStorageMasterRepository docStorageMasterRepository;
    private final DocStorageDetailRepository docStorageDetailRepository;
    private final InfoService infoService;

    @Override
    @Transactional
    public void addStorageInfo(DocStorageRequestDTO docStorageRequestDTO) {
        DocStorageDetail docStorageDetail = docStorageRequestDTO.toDetailEntity();

        docStorageDetail.setRgstrId(infoService.getUserInfo().getUserName());
        docStorageDetail.setRgstDt(new Timestamp(System.currentTimeMillis()));

        docStorageDetailRepository.save(docStorageDetail);
    }

    @Override
    @Transactional
    public void updateStorageInfo(Long detailId, DocStorageUpdateRequestDTO docStorageUpdateDTO) {
        DocStorageDetail docStorageDetail = docStorageDetailRepository.findById(detailId)
                .orElseThrow(() -> new IllegalArgumentException("DocStorageDetail not found By DetailId : " + detailId));

        docStorageDetail.update(docStorageUpdateDTO);
        docStorageDetail.setUpdtDt(new Timestamp(System.currentTimeMillis()));
        docStorageDetail.setUpdtrId(infoService.getUserInfo().getUserName());
        docStorageDetailRepository.save(docStorageDetail);
    }

    @Override
    @Transactional
    public void deleteStorageInfo(Long detailId) {
        DocStorageDetail docStorageDetail = docStorageDetailRepository.findById(detailId)
                .orElseThrow(() -> new IllegalArgumentException("DocStorageDetail not found By DetailId : " + detailId));

        docStorageDetailRepository.delete(docStorageDetail);
    }

    @Override
    @Transactional(readOnly = true)
    public DocStorageDetailResponseDTO getStorageInfo(Long detailId) {
        DocStorageDetail docStorageDetail = docStorageDetailRepository.findById(detailId)
                .orElseThrow(() -> new IllegalArgumentException("DocStorageDetail not found By DetailId : " + detailId));

        if (docStorageDetail.getDraftId() != null) {
            DocStorageMaster docStorageMaster = docStorageMasterRepository.findById(docStorageDetail.getDraftId())
                    .orElseThrow(() -> new IllegalArgumentException("DocStorageMaster not found By DetailId : " + detailId));
            return DocStorageDetailResponseDTO.of(docStorageDetail, docStorageMaster.getType(), docStorageMaster.getStatus());
        } else {
            return DocStorageDetailResponseDTO.of(docStorageDetail, "", "미신청");
        }
    }

    @Override
    @Transactional
    public void applyStorage(DocStorageApplyRequestDTO docStorageApplyRequestDTO) {

        String drafter = infoService.getUserInfo().getUserName();
        String drafterId = infoService.getUserInfo().getUserId();

        DocStorageMaster docStorageMaster = docStorageMasterRepository.save(docStorageApplyRequestDTO.toMasterEntity(drafter, drafterId));

        docStorageApplyRequestDTO.getDetailIds().forEach(detailId -> {
            docStorageDetailRepository.findById(detailId).ifPresent(docStorageDetail -> {
                docStorageDetail.updateStatus("A");
                docStorageDetail.updateDraftId(docStorageMaster.getDraftId());
                docStorageDetailRepository.save(docStorageDetail);
            });
        });
    }

    @Override
    @Transactional
    public void approveStorage(List<Long> draftIds) {
        draftIds.forEach(draftId -> {
            docStorageMasterRepository.findById(draftId).ifPresent(docStorageMaster -> {
                docStorageMaster.updateStatus("B");
                docStorageMasterRepository.save(docStorageMaster);
            });

            List<DocStorageDetail> docStorageDetails = docStorageDetailRepository.findAllByDraftId(draftId)
                    .orElseThrow(() -> new IllegalArgumentException("Not Found"));
            docStorageDetails.forEach(docStorageDetail -> {
                docStorageDetail.updateStatus("B");
                docStorageDetailRepository.save(docStorageDetail);
            });
        });
    }

    @Override
    public void finishStorage(List<Long> detailIds) {
        detailIds.forEach(detailId -> {
            docStorageDetailRepository.findById(detailId).ifPresent(docStorageDetail -> {
                docStorageDetail.updateStatus("E");
                docStorageDetailRepository.save(docStorageDetail);
            });
        });
    }
}
