package kr.or.kmi.mis.api.bcd.service.impl;

import kr.or.kmi.mis.api.bcd.model.entity.BcdDetail;
import kr.or.kmi.mis.api.bcd.model.entity.BcdMaster;
import kr.or.kmi.mis.api.bcd.model.request.BcdRequestDTO;
import kr.or.kmi.mis.api.bcd.model.response.BcdDetailResponseDTO;
import kr.or.kmi.mis.api.bcd.model.response.BcdMasterResponseDTO;
import kr.or.kmi.mis.api.bcd.model.response.BcdPendingResponseDTO;
import kr.or.kmi.mis.api.bcd.repository.BcdDetailRepository;
import kr.or.kmi.mis.api.bcd.repository.BcdMasterRepository;
import kr.or.kmi.mis.api.bcd.service.BcdService;
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

    @Override
    @Transactional
    public void applyBcd(BcdRequestDTO bcdRequestDTO) {

        // 명함신청
        BcdMaster bcdMaster = bcdRequestDTO.toMasterEntity();
        Timestamp draftDate = new Timestamp(System.currentTimeMillis());
        bcdMaster.updateDate(draftDate);
        bcdMaster = bcdMasterRepository.save(bcdMaster);

        // 명함상세
        Long draftId = bcdMaster.getDraftId();
        BcdDetail bcdDetail = bcdRequestDTO.toDetailEntity(draftId, draftDate);
        bcdDetail.updateSeqId(1L);
        bcdDetailRepository.save(bcdDetail);

    }

    @Override
    @Transactional
    public void updateBcd(Long draftId, Long seqId, BcdRequestDTO bcdUpdateRequest) {

        // 가장 첫번째 신청 이력(BcdDetail) 찾음
        Optional<BcdDetail> existingDetailOpt = bcdDetailRepository.findTopByDraftIdOrderBySeqIdAsc(draftId);

        if (existingDetailOpt.isPresent()) {
            //  - 명함신청에 포함되는 정보를 수정한다고 해도, 명함신청 기안자와 기안일시는 변경되면 안됨
            //  - 명함기안자와 명함수정자가 다를 수 있으므로, 각각에 대한 정보를 로그로 남겨둬야 함
            BcdDetail bcdDetail = existingDetailOpt.get();
            Timestamp draftDate = bcdDetail.getDraftDate();
            BcdDetail newBcdDetail = bcdUpdateRequest.toDetailEntity(draftId, draftDate);

            // 명함수정자의 정보와 수정시각 업데이트
            // todo: 로그인 한 세션의 id(사번) 값 -> 이름
            String lastUpdtId = "2024000002";
            Timestamp lastUpdtDt = new Timestamp(System.currentTimeMillis());
            newBcdDetail.update(lastUpdtId, lastUpdtDt);

            // seqId 값 설정
            newBcdDetail.updateSeqId(seqId + 1);

            // 명함수정내역 저장
            bcdDetailRepository.save(newBcdDetail);

        } else {
            throw new IllegalArgumentException("신청 이력이 없습니다.");
        }
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
        return mapDetailToMasterResponse(bcdMasters);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BcdMasterResponseDTO> getMyBcdApplyByDateRange(Timestamp startDate, Timestamp endDate) {

        //todo: 로그인 한 세션의 id 값 받아오기
        String userId = "2024000111";

        // 2. 나의 모든 명함신청 내역을 호출한다.
        //  - DrafterId(기안자 사번)로 나의 명함신청 내역을 불러온다.
        //  - 기안 일자를 기준으로 내림차순 정렬한다.

        // 2-1. 내가 신청한 나의 명함신청 내역
        List<BcdMaster> bcdMasters = bcdMasterRepository.findByDrafterIdAndDraftDateBetweenOrderByDraftDateDesc(userId, startDate, endDate)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        // 2-2. 타인이 신청해준 나의 명함신청 내역
        List<BcdDetail> bcdDetails = bcdDetailRepository.findAllByUserIdAndDraftDateBetween(userId, startDate, endDate);

        Set<Long> draftIds = bcdDetails.stream()
                .map(BcdDetail::getDraftId)
                .collect(Collectors.toSet());

        for (Long draftId : draftIds) {
            bcdMasterRepository.findById(draftId)
                    .ifPresent(bcdMasters::add);
        }

        // 3. 신청 내역을 하나씩 꺼내, response dto 와 매핑하여 반환한다.
        return mapDetailToMasterResponse(bcdMasters);
    }

    public List<BcdMasterResponseDTO> mapDetailToMasterResponse(List<BcdMaster> bcdMasters) {
        return bcdMasters.stream()
                .distinct()
                .map(bcdMaster -> {
                    // BcdDetail 중 가장 최근에 생성된 BcdDetail(명함신청 최종 수정본)의 데이터를 가져온다.
                    // -  seqId: 시퀀스 넘버
                    // -  instNm: 센터명
                    // -  lastUpdateId: 최종 수정자
                    // -  lastUpdateDate: 최종 수정일
                    Optional<BcdDetail> latestBcdDetail = bcdDetailRepository.findTopByDraftIdOrderBySeqIdDesc(bcdMaster.getDraftId());

                    Long seqId = latestBcdDetail.map(BcdDetail::getSeqId).orElse(null);
                    String instNm = latestBcdDetail.map(BcdDetail::getInstNm).orElse(null);
                    String lastUpdateId = latestBcdDetail.map(BcdDetail::getLastUpdtId).orElse(null);
                    Timestamp lastUpdtDt = latestBcdDetail.map(BcdDetail::getLastUpdtDate).orElse(null);

                    return BcdMasterResponseDTO.of(bcdMaster, seqId, instNm, lastUpdateId, lastUpdtDt);

                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BcdDetailResponseDTO getBcd(Long draftId) {

        BcdDetail bcdDetail = bcdDetailRepository.findTopByDraftIdOrderBySeqIdDesc(draftId)
                .orElseThrow(()-> new  IllegalArgumentException("Not Found"));

        return BcdDetailResponseDTO.of(bcdDetail);

    }

    @Override
    public List<BcdPendingResponseDTO> getPendingList() {

        // 1. 승인대기 상태인 신청목록 모두 호출
        //   - 기안일자 기준 내림차순 정렬
        List<BcdMaster> bcdMasters = bcdMasterRepository.findAllByStatusOrderByDraftDateDesc("A");

        // 2. Detail 테이블의 seqId와 수정자, 수정일시, 센터 정보와 매핑해, ResponseDto 형태로 반환
        List<BcdMasterResponseDTO> bcdPendingLists = mapDetailToMasterResponse(bcdMasters);


        // 3. Detail 테이블의 seqId와 수정자, 수정일시 정보와 매핑해, ResponseDto 형태로 반환
        return bcdPendingLists.stream()
                .map(BcdPendingResponseDTO::of)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BcdPendingResponseDTO> getMyPendingList() {

        //todo: 로그인 한 세션의 id 값 받아오기
        String userId = "2024000111";

        // 2. 나의 모든 명함신청 승인대기 내역을 호출한다.
        //  - DrafterId(기안자 사번)로 나의 명함신청 승인대기 내역을 불러온다.
        //  - 기안 일자를 기준으로 내림차순 정렬한다.

        // 2-1. 내가 신청한 나의 명함신청 승인대기 내역
        List<BcdMaster> myBcdMasters = bcdMasterRepository.findByDrafterIdAndStatusOrderByDraftDateDesc(userId, "A")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        // 2-2. 타인이 신청해준 나의 명함신청 승인대기 내역
        //  - 명함대상자가 userId인 명함신청의 pk(draftId)를 조회
        //  - draftId로 bcdMaster 찾아, 앞선 bcdMasters(2-1)에 추가
        List<BcdDetail> bcdDetails = bcdDetailRepository.findAllByUserId(userId);

        Set<Long> draftIds = bcdDetails.stream()
                .map(BcdDetail::getDraftId)
                .collect(Collectors.toSet());

        for (Long draftId : draftIds) {
            bcdMasterRepository.findByDraftIdAndStatus(draftId, "A")
                    .ifPresent(myBcdMasters::add);
        }

        List<BcdMasterResponseDTO> myBcdPendingLists = mapDetailToMasterResponse(myBcdMasters);

        return myBcdPendingLists.stream()
                .map(BcdPendingResponseDTO::of)
                .collect(Collectors.toList());
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
}
