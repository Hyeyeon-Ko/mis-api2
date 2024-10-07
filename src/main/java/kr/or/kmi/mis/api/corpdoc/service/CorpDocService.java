package kr.or.kmi.mis.api.corpdoc.service;

import kr.or.kmi.mis.api.apply.model.request.ApplyRequestDTO;
import kr.or.kmi.mis.api.corpdoc.model.request.CorpDocRequestDTO;
import kr.or.kmi.mis.api.corpdoc.model.request.CorpDocUpdateRequestDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocDetailResponseDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocMasterResponseDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocMyResponseDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocPendingResponseDTO;
import kr.or.kmi.mis.cmm.model.request.PostSearchRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public interface CorpDocService {

    /** 법인서류 신청 */
    void createCorpDocApply(CorpDocRequestDTO corpDocRequestDTO, MultipartFile file) throws Exception;
    /** 법인서류 신청내역 조회 */
    CorpDocDetailResponseDTO getCorpDocApply(String draftId);
    /** 법인서류 신청내역 수정 */
    void updateCorpDocApply(String draftId, CorpDocUpdateRequestDTO corpDocUpdateRequestDTO,
                            MultipartFile file, boolean isFileDeleted) throws Exception;
    /** 법인서류 신청내역 취소 */
    void cancelCorpDocApply(String draftId);
    /** 법인서류 나의 승인대기내역 조회 */
    List<CorpDocPendingResponseDTO> getMyPendingList(String userId);
    Page<CorpDocPendingResponseDTO> getMyPendingList2(ApplyRequestDTO applyRequestDTO, Pageable page);
    /** 법인서류 전체 승인대기내역, 센터별 조회 */
    List<CorpDocPendingResponseDTO> getPendingList(LocalDateTime startDate, LocalDateTime endDate);
    Page<CorpDocPendingResponseDTO> getPendingList2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page);
    /** 법인서류 나의 신청내역, 신청일자로 조회 */
    List<CorpDocMyResponseDTO> getMyCorpDocApply(LocalDateTime startDate, LocalDateTime endDate, String userId);
    Page<CorpDocMyResponseDTO> getMyCorpDocApply2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page);
    List<CorpDocMyResponseDTO> getMyCorpDocApply(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO);
    /** 법인서류 전체 신청내역, 신청일자로 조회 */
    List<CorpDocMasterResponseDTO> getCorpDocApply(LocalDateTime startDate, LocalDateTime endDate, String searchType, String keyword);
    Page<CorpDocMasterResponseDTO> getCorpDocApply2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable pageable);
}
