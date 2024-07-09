package kr.or.kmi.mis.api.bcd.service;

import kr.or.kmi.mis.api.bcd.model.request.BcdRequestDTO;
import kr.or.kmi.mis.api.bcd.model.response.BcdDetailResponseDTO;
import kr.or.kmi.mis.api.bcd.model.response.BcdMasterResponseDTO;
import kr.or.kmi.mis.api.bcd.model.response.BcdPendingResponseDTO;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

public interface BcdService {

    public void applyBcd(BcdRequestDTO bcdRequestDTO);
    public void updateBcd(Long draftId, BcdRequestDTO bcdUpdateRequest);
    public void cancelBcdApply(Long draftId);
    public List<BcdMasterResponseDTO> getBcdApplyByDateRange(Timestamp startDate, Timestamp endDate);
    public List<BcdMasterResponseDTO> getMyBcdApplyByDateRange(Timestamp startDate, Timestamp endDate);
    public BcdDetailResponseDTO getBcd(Long draftId, Long seqId);
    public List<BcdPendingResponseDTO> getMyPendingList();
    public void completeBcdApply(Long draftId);
}
