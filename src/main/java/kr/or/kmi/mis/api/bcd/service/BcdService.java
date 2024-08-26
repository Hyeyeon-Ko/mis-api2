package kr.or.kmi.mis.api.bcd.service;

import kr.or.kmi.mis.api.bcd.model.request.BcdRequestDTO;
import kr.or.kmi.mis.api.bcd.model.request.BcdUpdateRequestDTO;
import kr.or.kmi.mis.api.bcd.model.response.*;

import java.sql.Timestamp;
import java.util.List;

public interface BcdService {

    void applyBcd(BcdRequestDTO bcdRequestDTO);
    void updateBcd(Long draftId, BcdUpdateRequestDTO updateBcdRequestDTO);
    void cancelBcdApply(Long draftId);
    List<BcdMasterResponseDTO> getBcdApplyByDateRangeAndInstCd(Timestamp startDate, Timestamp endDate, String instCd);
    List<BcdMyResponseDTO> getMyBcdApplyByDateRange(Timestamp startDate, Timestamp endDate);
//    BcdDetailResponseDTO getBcd(Long draftId);
    List<BcdPendingResponseDTO> getPendingList(String instCd);
    List<BcdPendingResponseDTO> getMyPendingList();
    void completeBcdApply(Long draftId);

//    BcdSampleResponseDTO getDetailNm(String groupCd, String detailCd);
}
