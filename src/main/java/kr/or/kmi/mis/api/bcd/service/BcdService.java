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

import java.util.List;

public interface BcdService {

    void applyBcd(BcdRequestDTO bcdRequestDTO);
    void updateBcd(String draftId, BcdUpdateRequestDTO updateBcdRequestDTO);
    void cancelBcdApply(String draftId);
    List<BcdMasterResponseDTO> getBcdApply(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO);
    Page<BcdMasterResponseDTO> getBcdApply2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page);
    /**
     * 전체 조회
     * @param applyRequestDTO applyRequestDTO
     * @param postSearchRequestDTO postSearchRequestDTO
     * @param page page
     * @return Page<BcdMyResponseDTO>
     */
    Page<BcdMyResponseDTO> getMyBcdApply2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page);
    /**
     * 전체 조회
     * @param applyRequestDTO applyRequestDTO
     * @param postSearchRequestDTO postSearchRequestDTO
     * @return Page<BcdMyResponseDTO>
     */
    List<BcdMyResponseDTO> getMyBcdApply(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO);
    Page<BcdPendingResponseDTO> getPendingList2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page);
    List<BcdPendingResponseDTO> getMyPendingList(ApplyRequestDTO applyRequestDTO);
    void completeBcdApply(String draftId);
    void sendReceiptBcd(List<String> draftIds);
}
