package kr.or.kmi.mis.api.corpdoc.service.impl;

import kr.or.kmi.mis.api.corpdoc.model.entity.CorpDocDetail;
import kr.or.kmi.mis.api.corpdoc.model.entity.CorpDocMaster;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocIssueListResponseDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocIssueResponseDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocRnpResponseDTO;
import kr.or.kmi.mis.api.corpdoc.repository.CorpDocDetailRepository;
import kr.or.kmi.mis.api.corpdoc.repository.CorpDocMasterRepository;
import kr.or.kmi.mis.api.corpdoc.service.CorpDocListService;
import kr.or.kmi.mis.api.std.service.StdBcdService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CorpDocListServiceImpl implements CorpDocListService {

    private final CorpDocMasterRepository corpDocMasterRepository;
    private final CorpDocDetailRepository corpDocDetailRepository;
    private final StdBcdService stdBcdService;

    @Override
    public CorpDocIssueListResponseDTO getCorpDocIssueList() {
        List<CorpDocMaster> corpDocMasters = corpDocMasterRepository.findAllByStatusOrderByDraftDateAsc("G");
        List<CorpDocMaster> corpDocPendingMasters = corpDocMasterRepository.findAllByStatusOrderByDraftDateAsc("B");

        return CorpDocIssueListResponseDTO.of(this.intoDTO(corpDocMasters), this.intoDTO(corpDocPendingMasters));
    }

    private List<CorpDocIssueResponseDTO> intoDTO(List<CorpDocMaster> corpDocMasters) {

        return corpDocMasters.stream()
                .map(corpDocMaster -> {
                    CorpDocDetail corpDocDetail = corpDocDetailRepository.findById(corpDocMaster.getDraftId())
                            .orElseThrow(() -> new IllegalArgumentException("Not found corp doc detail"));

                    CorpDocIssueResponseDTO corpDocIssueResponseDTO = CorpDocIssueResponseDTO.of(corpDocMaster, corpDocDetail);
                    corpDocIssueResponseDTO.setInstNm(stdBcdService.getInstNm(corpDocMaster.getInstCd()));

                    return corpDocIssueResponseDTO;
                }).toList();
    }

    @Override
    public List<CorpDocRnpResponseDTO> getCorpDocRnPList() {
        return List.of();
    }
}
