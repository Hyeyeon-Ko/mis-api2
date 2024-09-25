package kr.or.kmi.mis.api.corpdoc.service;

import kr.or.kmi.mis.api.corpdoc.model.request.CorpDocLeftRequestDTO;
import kr.or.kmi.mis.api.corpdoc.model.request.CorpDocStoreRequestDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocIssueListResponseDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocRnpResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface CorpDocListService {
    /** 법인서류 발급대장 내역 조회 */
    CorpDocIssueListResponseDTO getCorpDocIssueList(LocalDate startDate, LocalDate endDate, String searchType, String keyword);

    int getCorpDocIssuePendingList();

    /** 법인서류 수불대장 내역 조회 */
    List<CorpDocRnpResponseDTO> getCorpDocRnpList(String searchType, String keyword, String instCd);

    void issueCorpDoc(Long draftId, CorpDocLeftRequestDTO corpDocLeftRequestDTO);

    void storeCorpDoc(CorpDocStoreRequestDTO corpDocStoreRequestDTO);

    void completeCorpDoc(Long draftId);
}
