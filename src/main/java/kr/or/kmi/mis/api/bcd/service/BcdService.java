package kr.or.kmi.mis.api.bcd.service;

import kr.or.kmi.mis.api.apply.model.request.ApplyRequestDTO;
import kr.or.kmi.mis.api.bcd.model.request.BcdRequestDTO;
import kr.or.kmi.mis.api.bcd.model.request.BcdUpdateRequestDTO;
import kr.or.kmi.mis.api.bcd.model.response.BcdMasterResponseDTO;
import kr.or.kmi.mis.api.bcd.model.response.BcdMyResponseDTO;
import kr.or.kmi.mis.api.bcd.model.response.BcdPendingResponseDTO;
import kr.or.kmi.mis.cmm.model.request.PostSearchRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface BcdService {

    void applyBcd(BcdRequestDTO bcdRequestDTO);
    void applyBcdByLeader(BcdRequestDTO bcdRequestDTO);
    void updateBcd(String draftId, BcdUpdateRequestDTO updateBcdRequestDTO);
    void cancelBcdApply(String draftId);
    List<BcdMasterResponseDTO> getBcdApply(LocalDateTime startDate, LocalDateTime endDate, String searchType, String keyword, String instCd, String userId);
    Page<BcdMasterResponseDTO> getBcdApply2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page);
    List<BcdMyResponseDTO> getMyBcdApply(LocalDateTime startDate, LocalDateTime endDate, String userId);
    /**
     * 전체 조회
     * @param applyRequestDTO
     * @param postSearchRequestDTO
     * @param page
     * @return
     */
    Page<BcdMyResponseDTO> getMyBcdApply2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page);
    /**
     * 전체 조회
     * @param applyRequestDTO
     * @param postSearchRequestDTO
     * @return
     */
    List<BcdMyResponseDTO> getMyBcdApply(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO);
    List<BcdPendingResponseDTO> getPendingList(LocalDateTime startDate, LocalDateTime endDate, String instCd, String userId);
    Page<BcdPendingResponseDTO> getPendingList2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page);
    List<BcdPendingResponseDTO> getMyPendingList(ApplyRequestDTO applyRequestDTO);
    void completeBcdApply(String draftId);
}
