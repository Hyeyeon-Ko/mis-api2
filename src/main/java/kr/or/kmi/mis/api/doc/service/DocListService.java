package kr.or.kmi.mis.api.doc.service;

import kr.or.kmi.mis.api.doc.model.request.DocRequestDTO;
import kr.or.kmi.mis.api.doc.model.response.DocResponseDTO;
import kr.or.kmi.mis.api.docstorage.domain.response.DeptResponseDTO;
import kr.or.kmi.mis.cmm.model.request.PostPageRequest;
import kr.or.kmi.mis.cmm.model.request.PostSearchRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface DocListService {
    List<DocResponseDTO> getReceiveApplyList(LocalDateTime startDate, LocalDateTime endDate, String searchType, String keyword, String instCd);
    Page<DocResponseDTO> getDocList(DocRequestDTO docRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page);
    List<DocResponseDTO> getSendApplyList(LocalDateTime startDate, LocalDateTime endDate, String searchType, String keyword, String instCd);
}