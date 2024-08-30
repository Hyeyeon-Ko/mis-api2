package kr.or.kmi.mis.api.corpdoc.service;

import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocIssueListResponseDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocRnpResponseDTO;

import java.util.List;

public interface CorpDocListService {
    /** 법인서류 발급대장 내역 조회 */
    CorpDocIssueListResponseDTO getCorpDocIssueList();

    /** 법인서류 수불대장 내역 조회 */
    List<CorpDocRnpResponseDTO> getCorpDocRnPList();
}
