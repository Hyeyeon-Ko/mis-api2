package kr.or.kmi.mis.api.corpdoc.service;

import kr.or.kmi.mis.api.corpdoc.model.request.CorpDocRequestDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocDetailResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CorpDocService {
    void createCorpDocApply(CorpDocRequestDTO corpDocRequestDTO, MultipartFile file) throws IOException;
    CorpDocDetailResponseDTO getCorpDocApply(Long draftId);
    void updateCorpDocApply(Long draftId, CorpDocRequestDTO corpDocRequestDTO, MultipartFile file, boolean isFileDeleted);
    void cancelCorpDocApply(Long draftId);
}
