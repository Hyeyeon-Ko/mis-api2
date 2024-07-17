package kr.or.kmi.mis.api.bcd.service;

import kr.or.kmi.mis.api.bcd.model.request.BcdRequestDTO;
import kr.or.kmi.mis.api.bcd.model.request.BcdUpdateRequestDTO;
import kr.or.kmi.mis.api.bcd.model.response.BcdDetailResponseDTO;
import kr.or.kmi.mis.api.bcd.model.response.BcdMasterResponseDTO;
import kr.or.kmi.mis.api.bcd.model.response.BcdPendingResponseDTO;
import kr.or.kmi.mis.api.bcd.model.response.BcdSampleResponseDTO;

import java.sql.Timestamp;
import java.util.List;

public interface BcdService {

    public void applyBcd(BcdRequestDTO bcdRequestDTO);
    public void updateBcd(Long draftId, BcdUpdateRequestDTO updateBcdRequestDTO);
    public void cancelBcdApply(Long draftId);
    public List<BcdMasterResponseDTO> getBcdApplyByDateRange(Timestamp startDate, Timestamp endDate);
    public List<BcdMasterResponseDTO> getMyBcdApplyByDateRange(Timestamp startDate, Timestamp endDate);
    public BcdDetailResponseDTO getBcd(Long draftId);
    public List<BcdPendingResponseDTO> getPendingList();
    public List<BcdPendingResponseDTO> getMyPendingList();
    public void completeBcdApply(Long draftId);

    BcdSampleResponseDTO getDetailNm(String groupCd, String detailCd);
}
