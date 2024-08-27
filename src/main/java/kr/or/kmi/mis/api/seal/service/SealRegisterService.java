package kr.or.kmi.mis.api.seal.service;

import kr.or.kmi.mis.api.seal.model.request.SealRegisterRequestDTO;
import kr.or.kmi.mis.api.seal.model.request.SealUpdateRequestDTO;

public interface SealRegisterService {

    /* 인장 등록 */
    void registerSeal(SealRegisterRequestDTO sealRegisterRequestDTO);

    /* 인장 수정 */
    void updateSeal(Long draftId, SealUpdateRequestDTO sealUpdateRequestDTO);

    /* 인장 삭제*/
    void deleteSeal(Long draftId);
}
