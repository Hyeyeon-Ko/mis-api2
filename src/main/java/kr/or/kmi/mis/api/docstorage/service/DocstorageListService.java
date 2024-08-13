package kr.or.kmi.mis.api.docstorage.service;

import kr.or.kmi.mis.api.docstorage.domain.response.DocstorageCenterListResponseDTO;
import kr.or.kmi.mis.api.docstorage.domain.response.DocstorageResponseDTO;
import kr.or.kmi.mis.api.docstorage.domain.response.DocstorageTotalListResponseDTO;

import java.util.List;

public interface DocstorageListService {

    /* 부서별 문서보관 내역 */
    List<DocstorageResponseDTO> getDocstorageDeptList(String deptCd);

    /* 센터별 문서보관 내역 */
    DocstorageCenterListResponseDTO getDocstorageCenterList(String instCd);

    /* 전국센터 문서보관 내역 */
    DocstorageTotalListResponseDTO getTotalDocstorageList();
}
