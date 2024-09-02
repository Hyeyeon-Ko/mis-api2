package kr.or.kmi.mis.api.corpdoc.service.impl;

import kr.or.kmi.mis.api.corpdoc.model.entity.CorpDocDetail;
import kr.or.kmi.mis.api.corpdoc.model.entity.CorpDocMaster;
import kr.or.kmi.mis.api.corpdoc.model.request.CorpDocLeftRequestDTO;
import kr.or.kmi.mis.api.corpdoc.model.request.CorpDocStoreRequestDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocIssueListResponseDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocIssueResponseDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocRnpResponseDTO;
import kr.or.kmi.mis.api.corpdoc.repository.CorpDocDetailRepository;
import kr.or.kmi.mis.api.corpdoc.repository.CorpDocMasterRepository;
import kr.or.kmi.mis.api.corpdoc.service.CorpDocListService;
import kr.or.kmi.mis.api.std.service.StdBcdService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CorpDocListServiceImpl implements CorpDocListService {

    private final CorpDocMasterRepository corpDocMasterRepository;
    private final CorpDocDetailRepository corpDocDetailRepository;
    private final StdBcdService stdBcdService;
    private final DataSourceTransactionManagerAutoConfiguration dataSourceTransactionManagerAutoConfiguration;

    @Override
    @Transactional(readOnly = true)
    public CorpDocIssueListResponseDTO getCorpDocIssueList() {
        List<CorpDocMaster> corpDocMasters = corpDocMasterRepository.findAllByStatusOrderByDraftDateAsc("G");
        corpDocMasters.addAll(corpDocMasterRepository.findAllByStatusOrderByDraftDateAsc("X"));
        List<CorpDocMaster> corpDocPendingMasters = corpDocMasterRepository.findAllByStatusOrderByDraftDateAsc("B");

        List<CorpDocIssueResponseDTO> sortedIssueList = this.intoDTO(corpDocMasters).stream()
                .sorted(Comparator.comparing(CorpDocIssueResponseDTO::getIssueDate)) // issueDate 기준으로 정렬
                .toList();
        return CorpDocIssueListResponseDTO.of(sortedIssueList, this.intoDTO(corpDocPendingMasters));
    }

    private List<CorpDocIssueResponseDTO> intoDTO(List<CorpDocMaster> corpDocMasters) {

        return corpDocMasters.stream()
                .map(corpDocMaster -> {
                    CorpDocDetail corpDocDetail = corpDocDetailRepository.findById(corpDocMaster.getDraftId())
                            .orElseThrow(() -> new IllegalArgumentException("Not found corp doc detail"));

                    CorpDocIssueResponseDTO corpDocIssueResponseDTO = CorpDocIssueResponseDTO.of(corpDocMaster, corpDocDetail);
                    if(!"X".equals(corpDocMaster.getStatus())){
                        corpDocIssueResponseDTO.setInstNm(stdBcdService.getInstNm(corpDocMaster.getInstCd()));
                    }

                    return corpDocIssueResponseDTO;
                }).toList();
    }

    @Override
    public List<CorpDocRnpResponseDTO> getCorpDocRnPList() {
        return List.of();
    }

    @Override
    @Transactional
    public void issueCorpDoc(Long draftId, CorpDocLeftRequestDTO corpDocLeftRequestDTO) {
        CorpDocMaster corpDocMaster = corpDocMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not found corp doc master: " + draftId));
        CorpDocDetail corpDocDetail = corpDocDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not found corp doc detail: " + draftId));

        // 법인서류 잔고 계산
        int totalCorpseal = corpDocLeftRequestDTO.getTotalCorpseal();
        totalCorpseal -= corpDocDetail.getCertCorpseal();
        int totalCoregister = corpDocLeftRequestDTO.getTotalCoregister();
        totalCoregister -= corpDocDetail.getCertCoregister();

        if (totalCorpseal < 0 || totalCoregister < 0) {
            throw new IllegalArgumentException("서류 잔고 부족");
        }

        // 법인서류 detail 발급 처리
        corpDocDetail.updateDateAndTotal(totalCorpseal, totalCoregister);
        corpDocDetailRepository.save(corpDocDetail);

        // 법인서류 master 상태 "발급완료"로 변경
        corpDocMaster.updateStatus("G");
        corpDocMasterRepository.save(corpDocMaster);
    }

    @Override
    @Transactional
    public void storeCorpDoc(CorpDocStoreRequestDTO corpDocStoreRequestDTO) {

        CorpDocMaster corpDocMaster = CorpDocMaster.builder()
                .drafterId(corpDocStoreRequestDTO.getUserId())
                .drafter(corpDocStoreRequestDTO.getUserNm())
                .draftDate(new Timestamp(System.currentTimeMillis()))
                .status("X")
                .instCd(corpDocStoreRequestDTO.getInstCd())
                .build();
        corpDocMasterRepository.save(corpDocMaster);

        CorpDocDetail corpDocDetail = corpDocStoreRequestDTO.toEntity(corpDocMaster.getDraftId());

        int totalCorpseal = corpDocStoreRequestDTO.getTotalCorpseal();
        totalCorpseal += corpDocDetail.getCertCorpseal();
        int totalCoregister = corpDocStoreRequestDTO.getTotalCoregister();
        totalCoregister += corpDocDetail.getCertCoregister();
        corpDocDetail.updateDateAndTotal(totalCorpseal, totalCoregister);

        corpDocDetail.setRgstrId(corpDocStoreRequestDTO.getUserId());
        corpDocDetail.setRgstDt(new Timestamp(System.currentTimeMillis()));
        corpDocDetailRepository.save(corpDocDetail);
    }
}
