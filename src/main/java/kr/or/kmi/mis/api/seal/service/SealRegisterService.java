package kr.or.kmi.mis.api.seal.service;

import kr.or.kmi.mis.api.seal.model.request.SealRegisterRequestDTO;
import kr.or.kmi.mis.api.seal.model.request.SealUpdateRequestDTO;
import kr.or.kmi.mis.api.seal.model.response.SealDetailResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface SealRegisterService {

    /* 인장 등록 */
    void registerSeal(SealRegisterRequestDTO sealRegisterRequestDTO, MultipartFile sealImage) throws IOException;

    /* 인장 수정 */
    void updateSeal(String draftId, SealUpdateRequestDTO sealUpdateRequestDTO, MultipartFile sealImage, boolean isFileDeleted) throws IOException;

    /* 인장 삭제*/
    void deleteSeal(String draftId);

    /* 인장 상세조회 */
    SealDetailResponseDTO getSealDetail(String draftId);
}
