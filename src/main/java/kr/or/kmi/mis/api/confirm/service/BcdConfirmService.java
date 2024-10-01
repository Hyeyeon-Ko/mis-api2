package kr.or.kmi.mis.api.confirm.service;

import kr.or.kmi.mis.api.bcd.model.response.BcdDetailResponseDTO;
import kr.or.kmi.mis.api.confirm.model.response.BcdHistoryResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface BcdConfirmService {

    /* 명함신청 상세 정보 불러오기 */
    BcdDetailResponseDTO getBcdDetailInfo(String draftId);

    /* 승인 */
    void approve(String draftId, String userId);

    /* 반려 */
    void disapprove(String draftId, String rejectReason, String userId);

    /* 명함 신청이력 조회 */
    List<BcdHistoryResponseDTO> getBcdApplicationHistory(LocalDate startDate, LocalDate endDate, String draftId);
}
