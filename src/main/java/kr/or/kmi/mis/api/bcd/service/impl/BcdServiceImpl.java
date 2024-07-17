package kr.or.kmi.mis.api.bcd.service.impl;

import kr.or.kmi.mis.api.bcd.model.entity.BcdDetail;
import kr.or.kmi.mis.api.bcd.model.entity.BcdMaster;
import kr.or.kmi.mis.api.bcd.model.request.BcdRequestDTO;
import kr.or.kmi.mis.api.bcd.model.request.BcdUpdateRequestDTO;
import kr.or.kmi.mis.api.bcd.model.response.BcdDetailResponseDTO;
import kr.or.kmi.mis.api.bcd.model.response.BcdMasterResponseDTO;
import kr.or.kmi.mis.api.bcd.model.response.BcdPendingResponseDTO;
import kr.or.kmi.mis.api.bcd.model.response.BcdSampleResponseDTO;
import kr.or.kmi.mis.api.bcd.repository.BcdDetailRepository;
import kr.or.kmi.mis.api.bcd.repository.BcdMasterRepository;
import kr.or.kmi.mis.api.bcd.repository.impl.BcdSampleQueryRepositoryImpl;
import kr.or.kmi.mis.api.bcd.service.BcdHistoryService;
import kr.or.kmi.mis.api.bcd.service.BcdService;
import kr.or.kmi.mis.api.user.service.InfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BcdServiceImpl implements BcdService {

    private final BcdMasterRepository bcdMasterRepository;
    private final BcdDetailRepository bcdDetailRepository;
    private final BcdHistoryService bcdHistoryService;
    private final InfoService infoService;

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

        // 2. 현재 명함상세 정보, 상세이력 테이블에 저장
        bcdHistoryService.createBcdHistory(existingDetailOpt);

        // 3. 수정된 명함상세 정보로 명함상세 update
        //   1) 수정자 조회
        //   2) 정보 업데이트
        String updtr = infoService.getUserInfo().getUserName();
        existingDetailOpt.update(updateBcdRequestDTO, updtr);
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
    public List<BcdMasterResponseDTO> getBcdApplyByDateRange(Timestamp startDate, Timestamp endDate) {

        // 1. 모든 명함신청 내역을 호출한다.
        //  - 이때, 취소된 명함신청 내역은 제외한다.
        //  - 기안 일자 범위를 기준으로 내림차순 정렬한다.

        List<BcdMaster> bcdMasters = bcdMasterRepository.findAllByStatusNotAndDraftDateBetweenOrderByDraftDateDesc("F", startDate, endDate)
                .orElseThrow(()-> new  IllegalArgumentException("Not Found"));

        // 2. 신청 내역을 하나씩 꺼내, response dto 와 매핑하여 반환한다.
        return bcdMasters.stream()
                .map(bcdMaster -> {
                    BcdDetail bcdDetail = bcdDetailRepository.findById(bcdMaster.getDraftId())
                            .orElseThrow(() -> new  IllegalArgumentException("Not Found : " + bcdMaster.getDraftId()));

                    return BcdMasterResponseDTO.of(bcdMaster, bcdDetail.getInstCd());
                }).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BcdMasterResponseDTO> getMyBcdApplyByDateRange(Timestamp startDate, Timestamp endDate) {

        List<BcdMasterResponseDTO> results = new ArrayList<>();

        // 1. 로그인한 사용자 정보 호출
        String userId = infoService.getUserInfo().getUserId();

        // 2. 나의 모든 명함신청 내역을 호출한다.
        //  - DrafterId(기안자 사번)로 나의 명함신청 내역을 불러온다.
        //  - 기안 일자를 기준으로 내림차순 정렬한다.

        // 2-1. 내가 신청한 나의 명함신청 내역
        List<BcdMaster> bcdMasters = bcdMasterRepository.findByDrafterIdAndDraftDateBetween(userId, startDate, endDate)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        List<BcdMasterResponseDTO> bcdMasterResponses = bcdMasters.stream()
                .map(bcdMaster -> {
                    BcdDetail bcdDetail = bcdDetailRepository.findById(bcdMaster.getDraftId())
                            .orElseThrow(() -> new  IllegalArgumentException("Not Found : " + bcdMaster.getDraftId()));

                    return BcdMasterResponseDTO.of(bcdMaster, bcdDetail.getInstCd());
                }).toList();

        // 2-2. 타인이 신청해준 나의 명함신청 내역
        List<BcdDetail> bcdDetails = bcdDetailRepository.findAllByUserId(userId);

        List<BcdMasterResponseDTO> anotherBcdResponses = bcdDetails.stream()
                .map(bcdDetail -> {
                            // startDate와 endDate 사이에 있는 BcdMaster 조회
                            List<BcdMaster> newBcdMasters = bcdMasterRepository.findByDraftIdAndDraftDateBetween(bcdDetail.getDraftId(), startDate, endDate)
                                    .orElseThrow(() -> new IllegalArgumentException("Not Found : " + bcdDetail.getDraftId()));

                            return newBcdMasters.stream()
                                    .map(bcdMaster -> BcdMasterResponseDTO.of(bcdMaster, bcdDetail.getInstCd()))
                                    .collect(Collectors.toList());
                }).flatMap(Collection::stream).toList();

        // 3. 명함신청 내역 반환
        results.addAll(bcdMasterResponses);
        results.addAll(anotherBcdResponses);
        return results;
    }

    @Override
    @Transactional(readOnly = true)
    public BcdDetailResponseDTO getBcd(Long draftId) {

        BcdDetail bcdDetail = bcdDetailRepository.findById(draftId)
                .orElseThrow(()-> new  IllegalArgumentException("Not Found"));
        BcdMaster bcdMaster = bcdMasterRepository.findById(draftId)
                .orElseThrow(() -> new  IllegalArgumentException("Not Found"));

        return BcdDetailResponseDTO.of(bcdDetail, bcdMaster.getDrafter());

    }

    @Override
    public List<BcdPendingResponseDTO> getPendingList() {

        // 1. 승인대기 상태인 신청목록 모두 호출
        //   - 기안일자 기준 내림차순 정렬
        List<BcdMaster> bcdMasters = bcdMasterRepository.findAllByStatusOrderByDraftDateDesc("A");

        // 2. Detail 테이블 정보와 매핑해, ResponseDto로 반환
        return bcdMasters.stream()
                .map(bcdMaster -> {
                            BcdDetail bcdDetail = bcdDetailRepository.findById(bcdMaster.getDraftId())
                                    .orElseThrow(() -> new IllegalArgumentException("Not Found : " + bcdMaster.getDraftId()));

                            return BcdPendingResponseDTO.of(bcdMaster, bcdDetail);
                }).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BcdPendingResponseDTO> getMyPendingList() {

        List<BcdPendingResponseDTO> results = new ArrayList<>();

        String userId = infoService.getUserInfo().getUserId();

        // 2. 나의 모든 명함신청 승인대기 내역을 호출한다.
        //  - DrafterId(기안자 사번)로 나의 명함신청 승인대기 내역을 불러온다.
        //  - 기안 일자를 기준으로 내림차순 정렬한다.

        // 2-1. 내가 신청한 나의 명함신청 승인대기 내역
        List<BcdMaster> myBcdMasters = bcdMasterRepository.findByDrafterIdAndStatus(userId, "A")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        List<BcdPendingResponseDTO> bcdMasterResponses = myBcdMasters.stream()
                .map(bcdMaster -> {
                    BcdDetail bcdDetail = bcdDetailRepository.findById(bcdMaster.getDraftId())
                            .orElseThrow(() -> new  IllegalArgumentException("Not Found : " + bcdMaster.getDraftId()));

                    return BcdPendingResponseDTO.of(bcdMaster, bcdDetail);
                }).toList();

        // 2-2. 타인이 신청해준 나의 명함신청 승인대기 내역
        //  - 명함대상자가 userId인 명함신청의 pk(draftId)를 조회
        //  - draftId로 bcdMaster 찾아, 앞선 bcdMasters(2-1)에 추가
        List<BcdDetail> bcdDetails = bcdDetailRepository.findAllByUserId(userId);

        List<BcdPendingResponseDTO> anotherBcdResponses = bcdDetails.stream()
                .map(bcdDetail -> {
                    BcdMaster newBcdMaster = bcdMasterRepository.findByDraftIdAndStatus(bcdDetail.getDraftId(), "A")
                            .orElseThrow(() -> new  IllegalArgumentException("Not Found : " + bcdDetail.getDraftId()));

                    return BcdPendingResponseDTO.of(newBcdMaster, bcdDetail);
                }).toList();

        results.addAll(bcdMasterResponses);
        results.addAll(anotherBcdResponses);
        return results;
    }

    @Override
    @Transactional
    public void completeBcdApply(Long draftId){

        // 1. 명함신청 Entity 호출
        BcdMaster bcdMaster = bcdMasterRepository.findById(draftId)
                .orElseThrow(() -> new  IllegalArgumentException("Not Found"));

        // 2. 명함신청 상태 "완료, End"로 변경
        bcdMaster.updateStatus("E");
    }

    @Override
    public BcdSampleResponseDTO getDetailNm(String groupCd, String detailCd) {
        return bcdSampleQueryRepositoryImpl.getBcdSampleNm(groupCd, detailCd);
    }
}
