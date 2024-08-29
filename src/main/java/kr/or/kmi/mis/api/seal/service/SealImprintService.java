package kr.or.kmi.mis.api.seal.service;

import kr.or.kmi.mis.api.seal.model.request.ImprintRequestDTO;
import kr.or.kmi.mis.api.seal.model.request.ImprintUpdateRequestDTO;
import kr.or.kmi.mis.api.seal.model.response.SealImprintDetailResponseDTO;

public interface SealImprintService {

    /* 날인신청 */
    void applyImprint(ImprintRequestDTO imprintRequestDTO);

    /* 날인수정 */
    void updateImprint(Long draftId, ImprintUpdateRequestDTO imprintUpdateRequestDTO);

    /* 날인취소 */
    void cancelImprint(Long draftId);

    /* 날인신청 상세조회 */
    SealImprintDetailResponseDTO getSealImprintDetail(Long draftId);
}
