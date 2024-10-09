package kr.or.kmi.mis.api.corpdoc.service.impl;

import kr.or.kmi.mis.api.corpdoc.model.entity.CorpDocDetail;
import kr.or.kmi.mis.api.corpdoc.model.entity.CorpDocMaster;
import kr.or.kmi.mis.api.corpdoc.model.request.CorpDocLeftRequestDTO;
import kr.or.kmi.mis.api.corpdoc.model.request.CorpDocStoreRequestDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocIssueResponseDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocRnpResponseDTO;
import kr.or.kmi.mis.api.corpdoc.repository.CorpDocDetailRepository;
import kr.or.kmi.mis.api.corpdoc.repository.CorpDocMasterRepository;
import kr.or.kmi.mis.api.corpdoc.repository.CorpDocQueryRepository;
import kr.or.kmi.mis.api.corpdoc.service.CorpDocListService;
import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import kr.or.kmi.mis.api.std.repository.StdDetailRepository;
import kr.or.kmi.mis.api.std.repository.StdGroupRepository;
import kr.or.kmi.mis.cmm.model.request.PostSearchRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CorpDocListServiceImpl implements CorpDocListService {

    private final CorpDocMasterRepository corpDocMasterRepository;
    private final CorpDocDetailRepository corpDocDetailRepository;
    private final CorpDocQueryRepository corpDocQueryRepository;
    private final StdGroupRepository stdGroupRepository;
    private final StdDetailRepository stdDetailRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<CorpDocIssueResponseDTO> getCorpDocIssueList(PostSearchRequestDTO postSearchRequestDTO, Pageable page) {
        return corpDocQueryRepository.getCorpDocIssueList2(postSearchRequestDTO, page);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CorpDocIssueResponseDTO> getCorpDocIssuePendingList(PostSearchRequestDTO postSearchRequestDTO, Pageable page){
        return corpDocQueryRepository.getCorpDocIssuePendingList(postSearchRequestDTO, page);
    }

    @Override
    @Transactional(readOnly = true)
    public int getCorpDocIssuePendingListCount() {
        List<CorpDocMaster> corpDocPendingMasters = corpDocMasterRepository.findAllByStatusOrderByDraftDateAsc("B");
        return corpDocPendingMasters.size();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CorpDocRnpResponseDTO> getCorpDocRnpList(String instCd, PostSearchRequestDTO postSearchRequestDTO, Pageable page) {
        return corpDocQueryRepository.getCorpDocRnpList(instCd, postSearchRequestDTO, page);
    }

    @Override
    @Transactional
    public void issueCorpDoc(String draftId, CorpDocLeftRequestDTO corpDocLeftRequestDTO) {
        CorpDocMaster corpDocMaster = corpDocMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not found corp doc master: " + draftId));
        CorpDocDetail corpDocDetail = corpDocDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not found corp doc detail: " + draftId));

        // 1. 법인서류 잔고 계산
        //    - pdf 요청 서류일 경우, 잔고 계산 x
        int totalCorpseal = corpDocLeftRequestDTO.getTotalCorpseal();
        int totalCoregister = corpDocLeftRequestDTO.getTotalCoregister();

        if(!Objects.equals(corpDocDetail.getType(), "P")) {
            totalCorpseal -= corpDocDetail.getCertCorpseal();
            totalCoregister -= corpDocDetail.getCertCoregister();

            if (totalCorpseal < 0 || totalCoregister < 0) {
                throw new IllegalArgumentException("서류 잔고 부족");
            }
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

        String draftId = generateDraftId();

        CorpDocMaster corpDocMaster = CorpDocMaster.builder()
                .draftId(draftId)
                .drafterId(corpDocStoreRequestDTO.getUserId())
                .drafter(corpDocStoreRequestDTO.getUserNm())
                .draftDate(LocalDateTime.now())
                .status("X")
                .instCd(corpDocStoreRequestDTO.getInstCd())
                .build();
        corpDocMasterRepository.save(corpDocMaster);

        CorpDocDetail corpDocDetail = corpDocStoreRequestDTO.toEntity(draftId);

        int totalCorpseal = corpDocStoreRequestDTO.getTotalCorpseal();
        totalCorpseal += corpDocDetail.getCertCorpseal();
        int totalCoregister = corpDocStoreRequestDTO.getTotalCoregister();
        totalCoregister += corpDocDetail.getCertCoregister();
        corpDocDetail.updateDateAndTotal(totalCorpseal, totalCoregister);

        corpDocDetail.setRgstrId(corpDocStoreRequestDTO.getUserId());
        corpDocDetail.setRgstDt(LocalDateTime.now());
        corpDocDetailRepository.save(corpDocDetail);
    }

    private String generateDraftId() {
        Optional<CorpDocMaster> lastCorpdocMasterOpt = corpDocMasterRepository.findTopByOrderByDraftIdDesc();

        StdGroup stdGroup = stdGroupRepository.findByGroupCd("A007")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        StdDetail stdDetail = stdDetailRepository.findByGroupCdAndDetailCd(stdGroup, "C")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        if (lastCorpdocMasterOpt.isPresent()) {
            String lastDraftId = lastCorpdocMasterOpt.get().getDraftId();
            int lastIdNum = Integer.parseInt(lastDraftId.substring(2));
            return stdDetail.getEtcItem1() + String.format("%010d", lastIdNum + 1);
        } else {
            return stdDetail.getEtcItem1() + "0000000001";
        }
    }

    @Override
    @Transactional
    public void completeCorpDoc(String draftId) {
        CorpDocMaster corpDocMaster = corpDocMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not found corp doc master: " + draftId));

        corpDocMaster.end(draftId);
        corpDocMasterRepository.save(corpDocMaster);
    }
}
