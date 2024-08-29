package kr.or.kmi.mis.api.confirm.service;

import kr.or.kmi.mis.api.seal.model.response.ExportDetailResponseDTO;
import kr.or.kmi.mis.api.seal.model.response.ImprintDetailResponseDTO;
import kr.or.kmi.mis.api.seal.model.response.SealHistoryResponseDTO;

import java.util.List;

public interface SealConfirmService {

    /* 날인신청 신청 상세 정보 불러오기 */
    ImprintDetailResponseDTO getImprintDetailInfo(Long draftId);

    /* 반출신청 신청 상세 정보 불러오기 */
    ExportDetailResponseDTO getExportDetailInfo(Long draftId);

    /* 승인 */
    void approve(Long draftId);

    /* 반려 */
    void disapprove(Long draftId, String rejectReason);

    /* 인장 신청이력 조회 */
    List<SealHistoryResponseDTO> getSealApplicationHistory(String userId);
}
