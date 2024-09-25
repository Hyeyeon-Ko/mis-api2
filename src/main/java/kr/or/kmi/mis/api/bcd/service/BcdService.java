package kr.or.kmi.mis.api.bcd.service;

import kr.or.kmi.mis.api.bcd.model.request.BcdRequestDTO;
import kr.or.kmi.mis.api.bcd.model.request.BcdUpdateRequestDTO;
import kr.or.kmi.mis.api.bcd.model.response.*;

import java.sql.Timestamp;
import java.util.List;

public interface BcdService {

    void applyBcd(BcdRequestDTO bcdRequestDTO);
    void applyBcdByLeader(BcdRequestDTO bcdRequestDTO);
    void updateBcd(Long draftId, BcdUpdateRequestDTO updateBcdRequestDTO);
    void cancelBcdApply(Long draftId);
    List<BcdMasterResponseDTO> getBcdApply(Timestamp startDate, Timestamp endDate, String searchType, String keyword, String instCd, String userId);
    List<BcdMyResponseDTO> getMyBcdApply(Timestamp startDate, Timestamp endDate, String userId);
//    BcdDetailResponseDTO getBcd(Long draftId);
    List<BcdPendingResponseDTO> getPendingList(Timestamp startDate, Timestamp endDate, String instCd, String userId);
    List<BcdPendingResponseDTO> getMyPendingList(String userId);
    void completeBcdApply(Long draftId);

//    BcdSampleResponseDTO getDetailNm(String groupCd, String detailCd);
}
