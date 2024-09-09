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
    List<BcdMasterResponseDTO> getBcdApplyByInstCd(String instCd);
    List<BcdMyResponseDTO> getMyBcdApply(String userId);
//    BcdDetailResponseDTO getBcd(Long draftId);
    List<BcdPendingResponseDTO> getPendingList(String instCd);
    List<BcdPendingResponseDTO> getMyPendingList(String userId);
    void completeBcdApply(Long draftId);

//    BcdSampleResponseDTO getDetailNm(String groupCd, String detailCd);
}
