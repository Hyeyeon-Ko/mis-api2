package kr.or.kmi.mis.api.docstorage.service.impl;

import kr.or.kmi.mis.api.docstorage.domain.entity.DocStorageDetail;
import kr.or.kmi.mis.api.docstorage.domain.entity.DocStorageMaster;
import kr.or.kmi.mis.api.docstorage.domain.request.DocStorageApplyRequestDTO;
import kr.or.kmi.mis.api.docstorage.domain.request.DocStorageRequestDTO;
import kr.or.kmi.mis.api.docstorage.domain.request.DocStorageUpdateDTO;
import kr.or.kmi.mis.api.docstorage.domain.response.DocStorageDetailResponseDTO;
import kr.or.kmi.mis.api.docstorage.repository.DocStorageDetailRepository;
import kr.or.kmi.mis.api.docstorage.repository.DocStorageMasterRepository;
import kr.or.kmi.mis.api.docstorage.service.DocStorageService;
import kr.or.kmi.mis.api.user.service.InfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        docStorageDetailRepository.save(docStorageDetail);
    }

    @Override
    @Transactional
    public void updateStorageInfo(Long draftId, DocStorageUpdateDTO docStorageUpdateDTO) {

    }

    @Override
    @Transactional
    public void deleteStorageInfo(Long draftId) {

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

    }

    @Override
    public void saveAll(List<DocStorageDetail> documents) {
        for (DocStorageDetail document : documents) {
            if (document.getDraftId() == null) {
                throw new IllegalArgumentException("Draft ID must be provided");
            }
        }
        docStorageDetailRepository.saveAll(documents);
    }
}
