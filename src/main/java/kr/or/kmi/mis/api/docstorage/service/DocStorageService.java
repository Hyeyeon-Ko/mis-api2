package kr.or.kmi.mis.api.docstorage.service;

import kr.or.kmi.mis.api.docstorage.domain.entity.DocStorageDetail;
import kr.or.kmi.mis.api.docstorage.domain.request.DocStorageApplyRequestDTO;
import kr.or.kmi.mis.api.docstorage.domain.request.DocStorageRequestDTO;
import kr.or.kmi.mis.api.docstorage.domain.request.DocStorageUpdateRequestDTO;
import kr.or.kmi.mis.api.docstorage.domain.response.DocStorageDetailResponseDTO;

import java.util.List;

public interface DocStorageService {
    void addStorageInfo(DocStorageRequestDTO docStorageRequestDTO);
    void updateStorageInfo(Long detailId, DocStorageUpdateRequestDTO docStorageUpdateDTO);
    void deleteStorageInfo(Long detailId);
    DocStorageDetailResponseDTO getStorageInfo(Long detailId);
    void applyStorage(DocStorageApplyRequestDTO docStorageApplyRequestDTO);
    void saveFileData(List<DocStorageDetail> documents);
    void approveStorage(List<Long> draftIds);
}
