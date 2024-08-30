package kr.or.kmi.mis.api.corpdoc.service;

import kr.or.kmi.mis.api.corpdoc.model.request.CorpDocRequestDTO;
import kr.or.kmi.mis.api.corpdoc.model.request.CorpDocUpdateRequestDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocDetailResponseDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocMasterResponseDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocMyResponseDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocPendingResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

public interface CorpDocService {

    /** 법인서류 신청 */
    void createCorpDocApply(CorpDocRequestDTO corpDocRequestDTO, MultipartFile file) throws IOException;
    /** 법인서류 신청내역 조회 */
    CorpDocDetailResponseDTO getCorpDocApply(Long draftId);
    /** 법인서류 신청내역 수정 */
    void updateCorpDocApply(Long draftId, CorpDocUpdateRequestDTO corpDocUpdateRequestDTO,
                            MultipartFile file, boolean isFileDeleted) throws IOException;
    /** 법인서류 신청내역 취소 */
    void cancelCorpDocApply(Long draftId);
    /** 법인서류 나의 승인대기내역 조회 */
    List<CorpDocPendingResponseDTO> getMyPendingList(String userId);
    /** 법인서류 전체 승인대기내역, 센터별 조회 */
    List<CorpDocPendingResponseDTO> getPendingList(Timestamp startDate, Timestamp endDate);
    /** 법인서류 나의 신청내역, 신청일자로 조회 */
    List<CorpDocMyResponseDTO> getMyCorpDocApplyByDateRange(Timestamp startDate, Timestamp endDate, String userId);
    /** 법인서류 전체 신청내역, 신청일자로 조회 */
    List<CorpDocMasterResponseDTO> getCorpDocApplyByDateRange(Timestamp startDate, Timestamp endDate);
}
