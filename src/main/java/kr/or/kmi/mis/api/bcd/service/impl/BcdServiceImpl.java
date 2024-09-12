package kr.or.kmi.mis.api.bcd.service.impl;

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
import kr.or.kmi.mis.api.std.service.StdBcdService;
import kr.or.kmi.mis.api.user.service.InfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BcdServiceImpl implements BcdService {

    private final BcdMasterRepository bcdMasterRepository;
    private final BcdDetailRepository bcdDetailRepository;
    private final BcdHistoryService bcdHistoryService;
    private final InfoService infoService;
    private final StdBcdService stdBcdService;

    private final BcdSampleQueryRepositoryImpl bcdSampleQueryRepositoryImpl;

    @Override
    @Transactional
    public void applyBcd(BcdRequestDTO bcdRequestDTO) {

        // 명함신청
        BcdMaster bcdMaster = bcdRequestDTO.toMasterEntity();
        bcdMaster = bcdMasterRepository.save(bcdMaster);

        // 명함상세
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

        bcdMaster.updateStatus("F");   // F(신청 취소)
    }

    @Override
    @Transactional(readOnly = true)
    public List<BcdMasterResponseDTO> getBcdApplyByInstCd(String instCd, String userId) {

        List<BcdMaster> bcdMasters = bcdMasterRepository.findAllByStatusNotOrderByDraftDateDesc("F")
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
    public List<BcdMyResponseDTO> getMyBcdApply(String userId) {

        List<BcdMyResponseDTO> results = new ArrayList<>();

        // 나의 모든 명함신청 내역을 호출한다.
        results.addAll(this.getMyMasterList(userId));
        results.addAll(this.getAnotherMasterList(userId));
        return results;
    }

    /**
     * 2-1. 내가 신청한 나의 명함신청 내역
     * @param userId
     * @return List<BcdMyResponseDTO>
     */
    public List<BcdMyResponseDTO> getMyMasterList(String userId) {
        List<BcdMaster> bcdMasters = bcdMasterRepository.findByDrafterId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        return bcdMasters.stream()
                .map(BcdMyResponseDTO::of).toList();
    }

    /**
     * 2-2. 타인이 신청한 나의 명함신청 내역
     * @param userId
     * @return List<BcdMyResponseDTO>
     */
    public List<BcdMyResponseDTO> getAnotherMasterList(String userId) {
        // 2-2. 타인이 신청해준 나의 명함신청 내역
        List<BcdDetail> bcdDetails = bcdDetailRepository.findAllByUserId(userId);

        return bcdDetails.stream()
                .flatMap(bcdDetail -> {
                    // startDate, endDate 사이에 있는 BcdMaster 조회
                    List<BcdMaster> newBcdMasters = bcdMasterRepository
                            .findByDraftIdAndDrafterIdNot(bcdDetail.getDraftId(), userId)
                            .orElse(new ArrayList<>());

                    return newBcdMasters.stream()
                            .map(BcdMyResponseDTO::of);
                }).toList();
    }

    @Override
    public List<BcdPendingResponseDTO> getPendingList(String instCd, String userId) {

        // 1. 승인대기 상태인 신청목록 모두 호출
        //    - 기안일자 기준 내림차순 정렬
        List<BcdMaster> bcdMasters = bcdMasterRepository.findAllByStatusOrderByDraftDateDesc("A");

        // 2. Detail 테이블 정보와 매핑해, ResponseDto로 반환
        return bcdMasters.stream()
                .filter(bcdMaster -> {
                    // 승인자 체인을 확인하고 현재 승인자가 userId인지 확인
                    String[] approverChainArray = bcdMaster.getApproverChain().split(", ");
                    int currentIndex = bcdMaster.getCurrentApproverIndex();

                    // 현재 승인자가 userId가 아닐 경우 필터링
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
        List<BcdMaster> myBcdMasters = bcdMasterRepository.findByDrafterIdAndStatus(userId, "A")
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
                    return bcdMasterRepository.findByDraftIdAndStatusAndDrafterIdNot(bcdDetail.getDraftId(), "A", userId)
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
