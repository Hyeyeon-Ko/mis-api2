package kr.or.kmi.mis.api.docstorage.service;

import kr.or.kmi.mis.api.docstorage.domain.response.DocstorageResponseDTO;
import kr.or.kmi.mis.api.docstorage.domain.response.DocstorageTotalListResponseDTO;

import java.util.List;

public interface DocstorageListService {

    /* 센터별 문서보관 내역 */
    List<DocstorageResponseDTO> getDocstorageCenterList(String userId);

    /* 전국센터 문서보관 내역 */
    DocstorageTotalListResponseDTO getTotalDocstorageList();
}
