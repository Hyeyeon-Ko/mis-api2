package kr.or.kmi.mis.api.bcd.repository;

import kr.or.kmi.mis.api.apply.model.request.ApplyRequestDTO;
import kr.or.kmi.mis.api.bcd.model.response.BcdMasterResponseDTO;
import kr.or.kmi.mis.api.bcd.model.response.BcdMyResponseDTO;
import kr.or.kmi.mis.api.confirm.model.response.BcdHistoryResponseDTO;
import kr.or.kmi.mis.cmm.model.request.PostSearchRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * packageName    : kr.or.kmi.mis.api.bcd.repository
 * fileName       : BcdApplyQueryRepository
 * author         : KMI_DI
 * date           : 2024-10-01
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-10-01        KMI_DI       the first create
 */
public interface BcdApplyQueryRepository {
    Page<BcdMasterResponseDTO> getBcdApply2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page);
    Page<BcdMyResponseDTO> getMyBcdApply2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page);
    List<BcdMyResponseDTO> getMyBcdList(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO);
    List<BcdMyResponseDTO> getAnotherMasterList(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO);
    Page<BcdHistoryResponseDTO> getBcdApplicationHistory(PostSearchRequestDTO postSearchRequestDTO, Pageable page, String draftId);
}
