package kr.or.kmi.mis.api.corpdoc.service;

import kr.or.kmi.mis.api.corpdoc.model.request.CorpDocLeftRequestDTO;
import kr.or.kmi.mis.api.corpdoc.model.request.CorpDocStoreRequestDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocIssueListResponseDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocRnpResponseDTO;
import kr.or.kmi.mis.cmm.model.request.PostSearchRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CorpDocListService {
    /** 법인서류 발급대장 내역 조회 */
    CorpDocIssueListResponseDTO getCorpDocIssueList(PostSearchRequestDTO postSearchRequestDTO, Pageable page);

    int getCorpDocIssuePendingList();

    /** 법인서류 수불대장 내역 조회 */
    Page<CorpDocRnpResponseDTO> getCorpDocRnpList(String instCd, PostSearchRequestDTO postSearchRequestDTO, Pageable page);


    void issueCorpDoc(String draftId, CorpDocLeftRequestDTO corpDocLeftRequestDTO);

    void storeCorpDoc(CorpDocStoreRequestDTO corpDocStoreRequestDTO);

    void completeCorpDoc(String draftId);
}
