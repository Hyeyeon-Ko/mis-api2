package kr.or.kmi.mis.api.bcd.service.impl;

import kr.or.kmi.mis.api.authority.model.entity.Authority;
import kr.or.kmi.mis.api.authority.model.request.AuthorityRequestDTO;
import kr.or.kmi.mis.api.authority.repository.AuthorityRepository;
import kr.or.kmi.mis.api.authority.service.AuthorityService;
import kr.or.kmi.mis.api.bcd.model.entity.BcdDetail;
import kr.or.kmi.mis.api.bcd.model.entity.BcdMaster;
import kr.or.kmi.mis.api.bcd.model.request.BcdRequestDTO;
import kr.or.kmi.mis.api.bcd.model.request.BcdUpdateRequestDTO;
import kr.or.kmi.mis.api.bcd.model.response.*;
import kr.or.kmi.mis.api.bcd.repository.BcdDetailRepository;
import kr.or.kmi.mis.api.bcd.repository.BcdMasterRepository;
import kr.or.kmi.mis.api.bcd.repository.impl.BcdSampleQueryRepositoryImpl;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
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

    @Override
    @Transactional
    public void applyBcd(BcdRequestDTO bcdRequestDTO) {

        // 명함 신청 로직
        BcdMaster bcdMaster = bcdRequestDTO.toMasterEntity("A");
        bcdMaster = bcdMasterRepository.save(bcdMaster);

        Long draftId = bcdMaster.getDraftId();
        BcdDetail bcdDetail = bcdRequestDTO.toDetailEntity(draftId);
        bcdDetailRepository.save(bcdDetail);

        // ADMIN 권한 부여
        List<Authority> authorityList = authorityRepository.findAllByDeletedtIsNull();

        String firstApproverId = bcdRequestDTO.getApproverIds().getFirst();
        InfoDetailResponseDTO infoDetailResponseDTO = infoService.getUserInfoDetail(firstApproverId);
        String userNm = infoDetailResponseDTO.getUserName();
        String instNm = infoDetailResponseDTO.getInstNm();
        String deptNm = infoDetailResponseDTO.getDeptNm();

        boolean authorityExists = authorityList.stream()
                .anyMatch(authority -> authority.getUserId().equals(firstApproverId));

        if (!authorityExists) {
            AuthorityRequestDTO requestDTO = AuthorityRequestDTO.builder()
                    .userId(firstApproverId)
                    .userNm(userNm)
                    .userRole("ADMIN")
                    .detailRole(null)
                    .build();

            authorityService.addAdmin(requestDTO);

            // 사이드바 권한 부여
            StdDetailRequestDTO stdDetailRequestDTO = StdDetailRequestDTO.builder()
                    .detailCd(firstApproverId)
                    .groupCd("B002")
                    .detailNm(instNm + " " + deptNm)
                    .etcItem1(userNm)
                    .etcItem2("A-2")
                    .build();

            stdDetailService.addInfo(stdDetailRequestDTO);
        }

        StdGroup stdGroup = stdGroupRepository.findByGroupCd("B002")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        StdDetail stdDetail = stdDetailRepository.findByGroupCdAndDetailCd(stdGroup, firstApproverId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        boolean needsUpdate = false;

        if (!"A-2".equals(stdDetail.getEtcItem2()) && !"A-2".equals(stdDetail.getEtcItem3())) {
            stdDetail.updateEtcItem3("A-2");
            needsUpdate = true;
        }

        if (needsUpdate) {
            stdDetailRepository.save(stdDetail);
        }
    }

    @Override
    public void applyBcdByLeader(BcdRequestDTO bcdRequestDTO) {
        // 명함 신청 로직
        BcdMaster bcdMaster = bcdRequestDTO.toMasterEntity("B");
        bcdMaster.updateRespondDate(new Timestamp(System.currentTimeMillis()));
        bcdMaster = bcdMasterRepository.save(bcdMaster);

        Long draftId = bcdMaster.getDraftId();
        BcdDetail bcdDetail = bcdRequestDTO.toDetailEntity(draftId);
        bcdDetailRepository.save(bcdDetail);
    }

    @Override
    @Transactional
    public void updateBcd(Long draftId, BcdUpdateRequestDTO updateBcdRequestDTO) {

        // 1. 명함상세 조회
        BcdDetail existingDetailOpt = bcdDetailRepository.findById(draftId)
                .orElseThrow(()-> new IllegalArgumentException("명함 신청 이력이 없습니다."));
        BcdMaster existingMasterOpt = bcdMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("명함 신청 이력이 없습니다."));

        // 2. 현재 명함상세 정보, 상세이력 테이블에 저장
        bcdHistoryService.createBcdHistory(existingDetailOpt);

        // 3. 수정된 명함상세 정보로 명함상세 update
        //   1) 수정자 조회
        //   2) 정보 업데이트
        String updtr = infoService.getUserInfo().getUserName();
        existingDetailOpt.update(updateBcdRequestDTO, updtr);
        existingMasterOpt.updateTitle(updateBcdRequestDTO);
        bcdDetailRepository.save(existingDetailOpt);
    }

    @Override
    @Transactional
    public void cancelBcdApply(Long draftId) {

        BcdMaster bcdMaster = bcdMasterRepository.findById(draftId)
                .orElseThrow(() -> new  IllegalArgumentException("Not Found : " + draftId));

        bcdMaster.updateStatus("F");
    }

    @Override
    @Transactional(readOnly = true)
    public List<BcdMasterResponseDTO> getBcdApply(Timestamp startDate, Timestamp endDate, String searchType, String keyword, String instCd, String userId) {

        List<BcdMaster> bcdMasters = bcdMasterRepository.findAllByStatusNotAndDraftDateBetweenOrderByDraftDateDesc("F", startDate, endDate)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        return bcdMasters.stream()
                .filter(bcdMaster -> {
                    if (bcdMaster.getStatus().equals("A")) {
                        String[] approverChainArray = bcdMaster.getApproverChain().split(", ");
                        int currentIndex = bcdMaster.getCurrentApproverIndex();

                        if (currentIndex >= approverChainArray.length || !approverChainArray[currentIndex].equals(userId)) {
                            return false;
                        }
                    }

                    BcdDetail bcdDetail = bcdDetailRepository.findById(bcdMaster.getDraftId())
                            .orElseThrow(() -> new IllegalArgumentException("Not Found : " + bcdMaster.getDraftId()));

                    if (!bcdDetail.getInstCd().equals(instCd)) return false;

                    if (searchType != null && keyword != null) {
                        return switch (searchType) {
                            case "전체" ->
                                    bcdMaster.getTitle().contains(keyword) || bcdMaster.getDrafter().contains(keyword);
                            case "제목" -> bcdMaster.getTitle().contains(keyword);
                            case "신청자" -> bcdMaster.getDrafter().contains(keyword);
                            default -> true;
                        };
                    }
                    return true;
                })
                .map(bcdMaster -> {
                    BcdMasterResponseDTO result = BcdMasterResponseDTO.of(bcdMaster, instCd);
                    result.setInstNm(stdBcdService.getInstNm(result.getInstCd()));
                    return result;
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BcdMyResponseDTO> getMyBcdApply(Timestamp startDate, Timestamp endDate, String userId) {
        List<BcdMyResponseDTO> results = new ArrayList<>();

        results.addAll(this.getMyMasterList(startDate, endDate, userId));
        results.addAll(this.getAnotherMasterList(startDate, endDate, userId));
        return results;
    }

    /**
     * 2-1. 내가 신청한 나의 명함신청 내역
     * @param userId
     * @return List<BcdMyResponseDTO>
     */
    public List<BcdMyResponseDTO> getMyMasterList(Timestamp startDate, Timestamp endDate, String userId) {
        List<BcdMaster> bcdMasters = bcdMasterRepository.findByDrafterIdAndDraftDateBetween(userId, startDate, endDate)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        return bcdMasters.stream()
                .map(bcdMaster -> BcdMyResponseDTO.of(bcdMaster, infoService))
                .toList();
    }

    /**
     * 2-2. 타인이 신청한 나의 명함신청 내역
     * @param userId
     * @return List<BcdMyResponseDTO>
     */
    public List<BcdMyResponseDTO> getAnotherMasterList(Timestamp startDate, Timestamp endDate, String userId) {
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

    @Override
    public List<BcdPendingResponseDTO> getPendingList(Timestamp startDate, Timestamp endDate, String instCd, String userId) {

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
    @Transactional(readOnly = true)
    public List<BcdPendingResponseDTO> getMyPendingList(String userId) {

        List<BcdPendingResponseDTO> results = new ArrayList<>();

        // 나의 모든 명함신청 승인대기 내역을 호출한다.
        results.addAll(this.getMyPndMasterList(userId));
        results.addAll(this.getAnotherPndMasterList(userId));
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
    public void completeBcdApply(Long draftId){

        // 1. 명함신청 Entity 호출
        BcdMaster bcdMaster = bcdMasterRepository.findById(draftId)
                .orElseThrow(() -> new  IllegalArgumentException("Not Found"));

        // 2. 명함신청 상태 "완료, End"로 변경
        bcdMaster.updateStatus("E");
        bcdMaster.updateEndDate(new Timestamp(System.currentTimeMillis()));
    }

}
