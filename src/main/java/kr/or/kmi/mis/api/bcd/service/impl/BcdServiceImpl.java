package kr.or.kmi.mis.api.bcd.service.impl;

import kr.or.kmi.mis.api.apply.model.request.ApplyRequestDTO;
import kr.or.kmi.mis.api.authority.model.request.AuthorityRequestDTO;
import kr.or.kmi.mis.api.authority.repository.AuthorityRepository;
import kr.or.kmi.mis.api.authority.service.AuthorityService;
import kr.or.kmi.mis.api.bcd.model.entity.BcdDetail;
import kr.or.kmi.mis.api.bcd.model.entity.BcdMaster;
import kr.or.kmi.mis.api.bcd.model.request.BcdRequestDTO;
import kr.or.kmi.mis.api.bcd.model.request.BcdUpdateRequestDTO;
import kr.or.kmi.mis.api.bcd.model.response.BcdMasterResponseDTO;
import kr.or.kmi.mis.api.bcd.model.response.BcdMyResponseDTO;
import kr.or.kmi.mis.api.bcd.model.response.BcdPendingResponseDTO;
import kr.or.kmi.mis.api.bcd.repository.BcdApplyQueryRepository;
import kr.or.kmi.mis.api.bcd.repository.BcdDetailRepository;
import kr.or.kmi.mis.api.bcd.repository.BcdMasterRepository;
import kr.or.kmi.mis.api.bcd.repository.BcdPendingQueryRepository;
import kr.or.kmi.mis.api.bcd.service.BcdHistoryService;
import kr.or.kmi.mis.api.bcd.service.BcdService;
import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import kr.or.kmi.mis.api.std.model.request.StdDetailRequestDTO;
import kr.or.kmi.mis.api.std.repository.StdDetailRepository;
import kr.or.kmi.mis.api.std.repository.StdGroupRepository;
import kr.or.kmi.mis.api.std.service.StdBcdService;
import kr.or.kmi.mis.api.std.service.StdDetailService;
import kr.or.kmi.mis.api.user.model.response.InfoDetailResponseDTO;
import kr.or.kmi.mis.api.user.service.InfoService;
import kr.or.kmi.mis.cmm.model.request.PostSearchRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BcdServiceImpl implements BcdService {

    private final BcdMasterRepository bcdMasterRepository;
    private final BcdDetailRepository bcdDetailRepository;
    private final AuthorityRepository authorityRepository;
    private final BcdHistoryService bcdHistoryService;
    private final InfoService infoService;
    private final StdBcdService stdBcdService;
    private final AuthorityService authorityService;
    private final StdDetailService stdDetailService;
    private final StdGroupRepository stdGroupRepository;
    private final StdDetailRepository stdDetailRepository;

    private final BcdApplyQueryRepository bcdApplyQueryRepository;
    private final BcdPendingQueryRepository bcdPendingQueryRepository;

    @Override
    @Transactional
    public void applyBcd(BcdRequestDTO bcdRequestDTO) {

        // 명함 신청 로직
        BcdMaster bcdMaster = saveBcdMaster(bcdRequestDTO);
        saveBcdDetail(bcdRequestDTO, bcdMaster.getDraftId());

        String firstApproverId = bcdRequestDTO.getApproverIds().getFirst();
        InfoDetailResponseDTO infoDetail = infoService.getUserInfoDetail(firstApproverId);

        // ADMIN 권한 부여 및 사이드바 권한 부여
        grantAdminAuthorityIfAbsent(firstApproverId, infoDetail);

        // 권한 업데이트 필요 여부 체크 및 업데이트
        updateSidebarPermissionsIfNeeded(firstApproverId);
    }

    private void grantAdminAuthorityIfAbsent(String firstApproverId, InfoDetailResponseDTO infoDetail) {
        boolean authorityExists = authorityRepository.findAll()
                .stream()
                .anyMatch(authority -> authority.getUserId().equals(firstApproverId));

        if (!authorityExists) {
            AuthorityRequestDTO authorityRequest = AuthorityRequestDTO.builder()
                    .userId(firstApproverId)
                    .userNm(infoDetail.getUserName())
                    .userRole("ADMIN")
                    .build();

            authorityService.addAdmin(authorityRequest);

            // 사이드바 권한 추가
            stdDetailService.addInfo(
                    StdDetailRequestDTO.builder()
                            .detailCd(firstApproverId)
                            .groupCd("B002")
                            .detailNm(infoDetail.getInstNm() + " " + infoDetail.getDeptNm())
                            .etcItem1(infoDetail.getUserName())
                            .etcItem2("A-2")
                            .build()
            );
        }
    }

    private void updateSidebarPermissionsIfNeeded(String firstApproverId) {
        StdGroup groupB002 = findStdGroup("B002");
        StdDetail detailB002 = findStdDetail(groupB002, firstApproverId);

        boolean needsUpdate = false;

        List<String> allowedValues = Arrays.asList("A", "A-1", "A-2");

        if (!allowedValues.contains(detailB002.getEtcItem2()) && !allowedValues.contains(detailB002.getEtcItem3())) {
            detailB002.updateEtcItem3("A-2");
            needsUpdate = true;
        }

        StdGroup stdGroupC002 = findStdGroup("C002");
        List<StdDetail> detailsC002 = findAllActiveDetails(stdGroupC002);

        if (detailsC002.stream().anyMatch(detail -> firstApproverId.equals(detail.getEtcItem2()) || firstApproverId.equals(detail.getEtcItem3()))) {
            needsUpdate = false;
        }

        if (needsUpdate) {
            stdDetailRepository.save(detailB002);
        }
    }

    private String generateDraftId() {
        Optional<BcdMaster> lastBcdMasterOpt = bcdMasterRepository.findTopByOrderByDraftIdDesc();

        StdGroup stdGroup = stdGroupRepository.findByGroupCd("A007")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        StdDetail stdDetail = stdDetailRepository.findByGroupCdAndDetailCd(stdGroup, "A")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        if (lastBcdMasterOpt.isPresent()) {
            String lastDraftId = lastBcdMasterOpt.get().getDraftId();
            int lastIdNum = Integer.parseInt(lastDraftId.substring(2));
            return stdDetail.getEtcItem1() + String.format("%010d", lastIdNum + 1);
        } else {
            return stdDetail.getEtcItem1() + "0000000001";
        }
    }

    private BcdMaster saveBcdMaster(BcdRequestDTO bcdRequestDTO) {
        String draftId = generateDraftId();

        BcdMaster bcdMaster = bcdRequestDTO.toMasterEntity(draftId, "A");
        return bcdMasterRepository.save(bcdMaster);
    }

    private void saveBcdDetail(BcdRequestDTO bcdRequestDTO, String draftId) {
        BcdDetail bcdDetail = bcdRequestDTO.toDetailEntity(draftId);
        bcdDetailRepository.save(bcdDetail);
    }

    private StdGroup findStdGroup(String groupCd) {
        return stdGroupRepository.findByGroupCd(groupCd)
                .orElseThrow(() -> new IllegalArgumentException("Group not found: " + groupCd));
    }

    private StdDetail findStdDetail(StdGroup group, String detailCd) {
        return stdDetailRepository.findByGroupCdAndDetailCd(group, detailCd)
                .orElseThrow(() -> new IllegalArgumentException("Detail not found for group: " + group.getGroupCd()));
    }

    private List<StdDetail> findAllActiveDetails(StdGroup group) {
        return stdDetailRepository.findAllByUseAtAndGroupCd("Y", group)
                .orElseThrow(() -> new IllegalArgumentException("No active details found for group: " + group.getGroupCd()));
    }

    @Override
    @Transactional
    public void applyBcdByLeader(BcdRequestDTO bcdRequestDTO) {
        String draftId = generateDraftId();

        BcdMaster bcdMaster = bcdRequestDTO.toMasterEntity(draftId, "B");
        bcdMaster.updateRespondDate(LocalDateTime.now());
        bcdMasterRepository.save(bcdMaster);

        saveBcdDetail(bcdRequestDTO, bcdMaster.getDraftId());
    }

    @Override
    @Transactional
    public void updateBcd(String draftId, BcdUpdateRequestDTO updateBcdRequestDTO) {
        BcdDetail existingDetail = getBcdDetail(draftId);
        BcdMaster existingMaster = getBcdMaster(draftId);

        bcdHistoryService.createBcdHistory(existingDetail);

        String updtr = infoService.getUserInfoDetail(updateBcdRequestDTO.getUserId()).getUserName();
        existingDetail.update(updateBcdRequestDTO, updtr);
        existingMaster.updateTitle(updateBcdRequestDTO);

        bcdDetailRepository.save(existingDetail);
    }

    private BcdDetail getBcdDetail(String draftId) {
        return bcdDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("BcdDetail not found: " + draftId));
    }

    private BcdMaster getBcdMaster(String draftId) {
        return bcdMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("BcdMaster not found: " + draftId));
    }

    @Override
    @Transactional
    public void cancelBcdApply(String draftId) {
        BcdMaster bcdMaster = getBcdMaster(draftId);
        bcdMaster.updateStatus("F");
    }

    @Override
    @Transactional(readOnly = true)
    public List<BcdMasterResponseDTO> getBcdApply(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO) {

        LocalDateTime startDate = convertStringToLocalDateTime(postSearchRequestDTO.getStartDate(), false);
        LocalDateTime endDate = convertStringToLocalDateTime(postSearchRequestDTO.getEndDate(), true);

        String searchType = postSearchRequestDTO.getSearchType();
        String keyword = postSearchRequestDTO.getKeyword();

        String instCd = applyRequestDTO.getInstCd();

        List<BcdMaster> bcdMasters = bcdMasterRepository.findAllByStatusNotAndDraftDateBetweenOrderByDraftDateDesc("F", startDate, endDate)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        return bcdMasters.stream()
                .filter(bcdMaster -> isValidForSearch(bcdMaster, instCd, searchType, keyword))
                .map(bcdMaster -> BcdMasterResponseDTO.of(bcdMaster, instCd, stdBcdService.getInstNm(instCd)))
                .collect(Collectors.toList());
    }

    public LocalDateTime convertStringToLocalDateTime(String dateString, boolean isEndDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            LocalDate date = LocalDate.parse(dateString, formatter);
            if (isEndDate) {
                return date.atTime(23, 59, 59);
            }
            return date.atStartOfDay();
        } catch (DateTimeParseException e) {
            System.out.println("Date parsing error: " + e.getMessage());
            return null;
        }
    }

    private boolean isValidForSearch(BcdMaster bcdMaster, String instCd, String searchType, String keyword) {
        BcdDetail bcdDetail = bcdDetailRepository.findById(bcdMaster.getDraftId())
                .orElseThrow(() -> new IllegalArgumentException("BcdDetail not found: " + bcdMaster.getDraftId()));

        if (!bcdDetail.getInstCd().equals(instCd)) {
            return false;
        }

        if (searchType != null && keyword != null) {
            return switch (searchType) {
                case "전체" -> bcdMaster.getTitle().contains(keyword) || bcdMaster.getDrafter().contains(keyword);
                case "제목" -> bcdMaster.getTitle().contains(keyword);
                case "신청자" -> bcdMaster.getDrafter().contains(keyword);
                default -> true;
            };
        }
        return true;
    }

    @Override
    public Page<BcdMasterResponseDTO> getBcdApply2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page) {
        return bcdApplyQueryRepository.getBcdApply2(applyRequestDTO, postSearchRequestDTO, page);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BcdMyResponseDTO> getMyBcdApply(LocalDateTime startDate, LocalDateTime endDate, String userId) {
        List<BcdMyResponseDTO> results = new ArrayList<>();

        results.addAll(this.getMyMasterList(startDate, endDate, userId));
        results.addAll(this.getAnotherMasterList(startDate, endDate, userId));
        return results;
    }

    @Override
    public Page<BcdMyResponseDTO> getMyBcdApply2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page) {
        return bcdApplyQueryRepository.getMyBcdApply2(applyRequestDTO, postSearchRequestDTO, page);
    }

    @Override
    public List<BcdMyResponseDTO> getMyBcdApply(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO) {
        List<BcdMyResponseDTO> results = new ArrayList<>();
        results.addAll(this.getMyMasterList2(applyRequestDTO, postSearchRequestDTO));
        results.addAll(this.getAnotherMasterList2(applyRequestDTO, postSearchRequestDTO));
        return results;
    }

    /**
     * 2-1. 내가 신청한 명함신청 내역
     * @param userId
     * @return List<BcdMyResponseDTO>
     */
    public List<BcdMyResponseDTO> getMyMasterList(LocalDateTime startDate, LocalDateTime endDate, String userId) {
        List<BcdMaster> bcdMasters = bcdMasterRepository.findByDrafterIdAndDraftDateBetween(userId, startDate, endDate)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        return bcdMasters.stream()
                .map(bcdMaster -> BcdMyResponseDTO.of(bcdMaster, infoService))
                .toList();
    }

    public List<BcdMyResponseDTO> getMyMasterList2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO) {
        return bcdApplyQueryRepository.getMyBcdList(applyRequestDTO, postSearchRequestDTO);
    }

    /**
     * 2-2. 타인이 신청한 나의 명함신청 내역
     * @param userId
     * @return List<BcdMyResponseDTO>
     */
    public List<BcdMyResponseDTO> getAnotherMasterList(LocalDateTime startDate, LocalDateTime endDate, String userId) {
        // 2-2. 타인이 신청해준 나의 명함신청 내역
        List<BcdDetail> bcdDetails = bcdDetailRepository.findAllByUserId(userId);

        return bcdDetails.stream()
            .flatMap(bcdDetail -> {
                // startDate, endDate 사이에 있는 BcdMaster 조회
                List<BcdMaster> newBcdMasters = bcdMasterRepository
                        .findByDraftIdAndDraftDateBetweenAndDrafterIdNot(bcdDetail.getDraftId(), startDate, endDate, userId)
                        .orElse(new ArrayList<>());

                return newBcdMasters.stream()
                        .map(bcdMaster -> BcdMyResponseDTO.of(bcdMaster, infoService));
            }).toList();
    }

    public List<BcdMyResponseDTO> getAnotherMasterList2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO) {
        return bcdApplyQueryRepository.getAnotherMasterList(applyRequestDTO, postSearchRequestDTO);
    }

    @Override
    public List<BcdPendingResponseDTO> getPendingList(LocalDateTime startDate, LocalDateTime endDate, String instCd, String userId) {

        // 1. 승인대기 상태인 신청목록 모두 호출
        //    - 기안일자 기준 내림차순 정렬
        List<BcdMaster> bcdMasters = bcdMasterRepository.findAllByStatusAndDraftDateBetweenOrderByDraftDateDesc("A", startDate, endDate);

        // 2. Detail 테이블 정보와 매핑해, ResponseDto로 반환
        return bcdMasters.stream()
                .filter(bcdMaster -> {
                    String[] approverChainArray = bcdMaster.getApproverChain().split(", ");
                    int currentIndex = bcdMaster.getCurrentApproverIndex();

                    return currentIndex < approverChainArray.length && approverChainArray[currentIndex].equals(userId);
                })
                .map(bcdMaster -> {
                    BcdDetail bcdDetail = bcdDetailRepository.findById(bcdMaster.getDraftId())
                            .orElseThrow(() -> new IllegalArgumentException("Not Found : " + bcdMaster.getDraftId()));

                    // 3. instCd가 파라미터 instCd와 같은 경우만 처리
                    if (bcdDetail.getInstCd().equals(instCd)) {
                        BcdPendingResponseDTO result = BcdPendingResponseDTO.of(bcdMaster, bcdDetail);
                        result.setInstNm(stdBcdService.getInstNm(result.getInstCd()));
                        return result;
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public Page<BcdPendingResponseDTO> getPendingList2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page) {
        return bcdPendingQueryRepository.getBcdPending2(applyRequestDTO, postSearchRequestDTO, page);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BcdPendingResponseDTO> getMyPendingList(ApplyRequestDTO applyRequestDTO) {

        List<BcdPendingResponseDTO> results = new ArrayList<>();

        // 나의 모든 명함신청 승인대기 내역을 호출한다.
        results.addAll(this.getMyPndMasterList(applyRequestDTO.getUserId()));
        results.addAll(this.getAnotherPndMasterList(applyRequestDTO.getUserId()));
        return results;
    }

    /**
     * 2-1. 내가 신청한 나의 명함신청 승인대기 내역
     *    - DrafterId(기안자 사번)로 나의 명함신청 승인대기 내역을 불러온다.
     *    - 기안 일자를 기준으로 내림차순 정렬한다.
     * @param userId
     * @return List<BcdPendingResponseDTO>
     */
    public List<BcdPendingResponseDTO> getMyPndMasterList(String userId) {
        List<BcdMaster> myBcdMasters = bcdMasterRepository.findByDrafterIdAndStatusAndCurrentApproverIndex(userId, "A", 0)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        return myBcdMasters.stream()
                .map(bcdMaster -> {
                    BcdDetail bcdDetail = bcdDetailRepository.findById(bcdMaster.getDraftId())
                            .orElseThrow(() -> new  IllegalArgumentException("Not Found : " + bcdMaster.getDraftId()));
                    return BcdPendingResponseDTO.of(bcdMaster, bcdDetail);
                }).toList();
    }

    /**
     * 2-2. 타인이 신청해준 나의 명함신청 승인대기 내역
     *    - userId(기안자 사번)로 타인이 신청해준 나의 명함신청 승인대기 내역을 불러온다.
     *    - 기안 일자를 기준으로 내림차순 정렬한다.
     * @param userId
     * @return List<BcdPendingResponseDTO>
     */
    public List<BcdPendingResponseDTO> getAnotherPndMasterList(String userId) {

        // 1. 명함신청 대상자 사번이 나의 사번과 일치하는 명함상세를 조회한다.
        List<BcdDetail> bcdDetails = bcdDetailRepository.findAllByUserId(userId);

        // 2. 명함상세의 draftId로 BcdMaster와 매핑해 PendingResponseDTO로 반환한다.
        return bcdDetails.stream()
                .map(bcdDetail -> {
                    return bcdMasterRepository.findByDraftIdAndStatusAndCurrentApproverIndexAndDrafterIdNot(bcdDetail.getDraftId(), "A", 0, userId)
                            .map(newBcdMaster -> BcdPendingResponseDTO.of(newBcdMaster, bcdDetail))
                            .orElse(null);
                }).filter(Objects::nonNull).toList();
    }

    @Override
    @Transactional
    public void completeBcdApply(String draftId){

        // 1. 명함신청 Entity 호출
        BcdMaster bcdMaster = bcdMasterRepository.findById(draftId)
                .orElseThrow(() -> new  IllegalArgumentException("Not Found"));

        // 2. 명함신청 상태 "완료, End"로 변경
        bcdMaster.updateStatus("E");
        bcdMaster.updateEndDate(LocalDateTime.now());
    }

}
