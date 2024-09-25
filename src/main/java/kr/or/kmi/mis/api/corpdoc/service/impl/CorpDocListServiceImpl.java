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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CorpDocListServiceImpl implements CorpDocListService {

    private final CorpDocMasterRepository corpDocMasterRepository;
    private final CorpDocDetailRepository corpDocDetailRepository;
    private final StdBcdService stdBcdService;

    @Override
    @Transactional(readOnly = true)
    public CorpDocIssueListResponseDTO getCorpDocIssueList(String searchType, String keyword) {

        // 1. 발급완료+입고된 법인서류, 발급대기 중인 법인서류 모두 호출
        List<CorpDocMaster> corpDocMasters = corpDocMasterRepository.findAllByStatusOrderByDraftDateAsc("G");
        corpDocMasters.addAll(corpDocMasterRepository.findAllByStatusOrderByDraftDateAsc("X"));
        List<CorpDocMaster> corpDocPendingMasters = corpDocMasterRepository.findAllByStatusOrderByDraftDateAsc("B");

        // 2. searchType과 keyword를 통한 필터링
        corpDocMasters = corpDocMasters.stream()
                .filter(corpDocMaster -> {
                    boolean matchesSearchType = true;

                    CorpDocDetail corpDocDetail = corpDocDetailRepository.findById(corpDocMaster.getDraftId())
                            .orElseThrow(() -> new IllegalArgumentException("Not Found"));

                    if (searchType != null && keyword != null && !keyword.isEmpty()) {
                        matchesSearchType = switch (searchType) {
                            case "전체" -> corpDocDetail.getIssueDate() != null && corpDocDetail.getIssueDate().toString().contains(keyword) ||
                                    corpDocMaster.getDrafter() != null && corpDocMaster.getDrafter().contains(keyword) ||
                                    corpDocDetail.getSubmission() != null && corpDocDetail.getSubmission().contains(keyword) ||
                                    corpDocDetail.getPurpose() != null && corpDocDetail.getPurpose().contains(keyword);
                            case "발급/입고일자" -> corpDocDetail.getIssueDate() != null && corpDocDetail.getIssueDate().toString().contains(keyword);
                            case "이름" -> corpDocMaster.getDrafter() != null && corpDocMaster.getDrafter().contains(keyword);
                            case "제출처" -> corpDocDetail.getSubmission() != null && corpDocDetail.getSubmission().contains(keyword);
                            case "사용목적" -> corpDocDetail.getPurpose() != null && corpDocDetail.getPurpose().contains(keyword);
                            default -> true;
                        };
                    }

                    return matchesSearchType;
                })
                .collect(Collectors.toList());

        // 3. 각각 responseDTO 형태로 반환
        List<CorpDocIssueResponseDTO> sortedIssueList = this.intoDTO(corpDocMasters).stream()
                .sorted(Comparator.comparing(CorpDocIssueResponseDTO::getIssueDate))
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
    @Transactional(readOnly = true)
    public int getCorpDocIssuePendingList() {
        List<CorpDocMaster> corpDocPendingMasters = corpDocMasterRepository.findAllByStatusOrderByDraftDateAsc("B");
        return corpDocPendingMasters.size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CorpDocRnpResponseDTO> getCorpDocRnpList(String searchType, String keyword, String instCd) {
        // 1. 해당 기관 코드(instCd)로 "E" 상태인 법인서류 호출
        List<CorpDocMaster> corpDocMasters = corpDocMasterRepository.findAllByStatusAndInstCdOrderByEndDateAsc("E", instCd);

        // 2. searchType과 keyword를 통한 필터링
        corpDocMasters = corpDocMasters.stream()
                .filter(corpDocMaster -> {
                    boolean matchesSearchType = true;

                    CorpDocDetail corpDocDetail = corpDocDetailRepository.findById(corpDocMaster.getDraftId())
                            .orElseThrow(() -> new IllegalArgumentException("Not found corp doc detail"));

                    if (searchType != null && keyword != null && !keyword.isEmpty()) {
                        matchesSearchType = switch (searchType) {
                            case "전체" -> corpDocMaster.getDraftDate() != null && corpDocMaster.getDraftDate().toString().contains(keyword) ||
                                    corpDocMaster.getDrafter() != null && corpDocMaster.getDrafter().contains(keyword) ||
                                    corpDocDetail.getSubmission() != null && corpDocDetail.getSubmission().contains(keyword) ||
                                    corpDocDetail.getPurpose() != null && corpDocDetail.getPurpose().contains(keyword);
                            case "수령일자" -> corpDocMaster.getDraftDate() != null && corpDocMaster.getDraftDate().toString().contains(keyword);
                            case "신청자" -> corpDocMaster.getDrafter() != null && corpDocMaster.getDrafter().contains(keyword);
                            case "제출처" -> corpDocDetail.getSubmission() != null && corpDocDetail.getSubmission().contains(keyword);
                            case "사용목적" -> corpDocDetail.getPurpose() != null && corpDocDetail.getPurpose().contains(keyword);
                            default -> true;
                        };
                    }

                    return matchesSearchType;
                })
                .toList();

        // 3. 각각 responseDTO 형태로 변환 후 반환
        return corpDocMasters.stream()
                .map(corpDocMaster -> {
                    CorpDocDetail corpDocDetail = corpDocDetailRepository.findById(corpDocMaster.getDraftId())
                            .orElseThrow(() -> new IllegalArgumentException("Not found corp doc detail"));

                    return CorpDocRnpResponseDTO.of(corpDocMaster, corpDocDetail);
                })
                .toList();
    }

    @Override
    @Transactional
    public void issueCorpDoc(Long draftId, CorpDocLeftRequestDTO corpDocLeftRequestDTO) {
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

    @Override
    @Transactional
    public void completeCorpDoc(Long draftId) {
        CorpDocMaster corpDocMaster = corpDocMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not found corp doc master: " + draftId));

        corpDocMaster.end(draftId);
        corpDocMasterRepository.save(corpDocMaster);
    }
}
