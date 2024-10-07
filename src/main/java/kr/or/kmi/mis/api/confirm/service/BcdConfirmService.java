package kr.or.kmi.mis.api.confirm.service;

import kr.or.kmi.mis.api.apply.model.request.ConfirmRequestDTO;
import kr.or.kmi.mis.api.bcd.model.response.BcdDetailResponseDTO;
import kr.or.kmi.mis.api.confirm.model.response.BcdHistoryResponseDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface BcdConfirmService {

    /* 명함신청 상세 정보 불러오기 */
    BcdDetailResponseDTO getBcdDetailInfo(String draftId);

    /* 승인 */
    void approve(String draftId, ConfirmRequestDTO confirmRequestDTO);

    /* 반려 */
    void disapprove(String draftId, ConfirmRequestDTO confirmRequestDTO);

    /* 명함 신청이력 조회 */
    List<BcdHistoryResponseDTO> getBcdApplicationHistory(LocalDateTime startDate, LocalDateTime endDate, String draftId);
}
