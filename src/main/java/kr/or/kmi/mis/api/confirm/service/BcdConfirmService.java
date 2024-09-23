package kr.or.kmi.mis.api.confirm.service;

import kr.or.kmi.mis.api.bcd.model.response.BcdDetailResponseDTO;
import kr.or.kmi.mis.api.confirm.model.response.BcdHistoryResponseDTO;

import java.util.List;

public interface BcdConfirmService {

    /* 명함신청 상세 정보 불러오기 */
    BcdDetailResponseDTO getBcdDetailInfo(Long id);

    /* 승인 */
    void approve(Long id, String userId);

    /* 반려 */
    void disapprove(Long id, String rejectReason, String userId);

    /* 명함 신청이력 조회 */
    List<BcdHistoryResponseDTO> getBcdApplicationHistory(Long draftId);
}
