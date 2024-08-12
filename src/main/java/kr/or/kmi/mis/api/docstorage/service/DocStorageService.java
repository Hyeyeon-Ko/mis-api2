package kr.or.kmi.mis.api.docstorage.service;

import kr.or.kmi.mis.api.docstorage.domain.request.DocStorageApplyRequestDTO;
import kr.or.kmi.mis.api.docstorage.domain.request.DocStorageRequestDTO;
import kr.or.kmi.mis.api.docstorage.domain.response.DocStorageDetailResponseDTO;

public interface DocStorageService {
    void addStorageInfo(DocStorageRequestDTO docStorageRequestDTO);
    void updateStorageInfo(Long detailId, DocStorageRequestDTO docStorageUpdateDTO);
    void deleteStorageInfo(Long detailId);
    DocStorageDetailResponseDTO getStorageInfo(Long detailId);
    void applyStorage(DocStorageApplyRequestDTO docStorageApplyRequestDTO);
}
