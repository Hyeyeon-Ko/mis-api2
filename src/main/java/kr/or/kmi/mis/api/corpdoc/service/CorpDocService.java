package kr.or.kmi.mis.api.corpdoc.service;

import kr.or.kmi.mis.api.corpdoc.model.request.CorpDocRequestDTO;
import kr.or.kmi.mis.api.corpdoc.model.request.CorpDocUpdateRequestDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocDetailResponseDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocMyResponseDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocPendingResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

public interface CorpDocService {
    void createCorpDocApply(CorpDocRequestDTO corpDocRequestDTO, MultipartFile file) throws IOException;
    CorpDocDetailResponseDTO getCorpDocApply(Long draftId);
    void updateCorpDocApply(Long draftId, CorpDocUpdateRequestDTO corpDocUpdateRequestDTO,
                            MultipartFile file, boolean isFileDeleted) throws IOException;
    void cancelCorpDocApply(Long draftId);
    List<CorpDocPendingResponseDTO> getMyPendingList(String userId);
    List<CorpDocPendingResponseDTO> getPendingList(String instCd);
    List<CorpDocMyResponseDTO> getMyCorpDocApplyByDateRange(Timestamp startDate, Timestamp endDate, String userId);
}
