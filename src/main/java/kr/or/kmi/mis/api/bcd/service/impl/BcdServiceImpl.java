package kr.or.kmi.mis.api.bcd.service.impl;

import kr.or.kmi.mis.api.apply.model.request.ApplyRequestDTO;
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
import kr.or.kmi.mis.api.noti.service.NotificationSendService;
import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import kr.or.kmi.mis.api.std.repository.StdDetailRepository;
import kr.or.kmi.mis.api.std.repository.StdGroupRepository;
import kr.or.kmi.mis.api.std.service.StdBcdService;
import kr.or.kmi.mis.api.user.service.EmailService;
import kr.or.kmi.mis.api.user.service.InfoService;
import kr.or.kmi.mis.cmm.model.request.PostSearchRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utils.SearchUtils;

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
    private final BcdHistoryService bcdHistoryService;
    private final InfoService infoService;
    private final StdBcdService stdBcdService;
    private final EmailService emailService;
    private final NotificationSendService notificationSendService;
    private final StdGroupRepository stdGroupRepository;
    private final StdDetailRepository stdDetailRepository;

    private final BcdApplyQueryRepository bcdApplyQueryRepository;
    private final BcdPendingQueryRepository bcdPendingQueryRepository;

    @Override
    @Transactional
    public void applyBcd(BcdRequestDTO bcdRequestDTO) {
        BcdMaster bcdMaster = saveBcdMaster(bcdRequestDTO);
        saveBcdDetail(bcdRequestDTO, bcdMaster.getDraftId());
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
        bcdMaster.setRgstDt(LocalDateTime.now());
        bcdMaster.setRgstrId(bcdRequestDTO.getDrafter());
        return bcdMasterRepository.save(bcdMaster);
    }

    private void saveBcdDetail(BcdRequestDTO bcdRequestDTO, String draftId) {
        BcdDetail bcdDetail = bcdRequestDTO.toDetailEntity(draftId);
        bcdDetail.setRgstDt(LocalDateTime.now());
        bcdDetail.setRgstrId(bcdRequestDTO.getDrafter());
        bcdDetailRepository.save(bcdDetail);
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

        existingDetail.setUpdtDt(LocalDateTime.now());
        existingDetail.setUpdtrId(existingMaster.getDrafter());
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

        return SearchUtils.matchesSearchCriteria(searchType,keyword, bcdMaster.getTitle(), bcdMaster.getDrafter());
    }

    @Override
    public Page<BcdMasterResponseDTO> getBcdApply2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page) {
        return bcdApplyQueryRepository.getBcdApply2(applyRequestDTO, postSearchRequestDTO, page);
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

    public List<BcdMyResponseDTO> getMyMasterList2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO) {
        return bcdApplyQueryRepository.getMyBcdList(applyRequestDTO, postSearchRequestDTO);
    }

    public List<BcdMyResponseDTO> getAnotherMasterList2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO) {
        return bcdApplyQueryRepository.getAnotherMasterList(applyRequestDTO, postSearchRequestDTO);
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
     * @param userId userId
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
     * @param userId userId
     * @return List<BcdPendingResponseDTO>
     */
    public List<BcdPendingResponseDTO> getAnotherPndMasterList(String userId) {

        // 1. 명함신청 대상자 사번이 나의 사번과 일치하는 명함상세를 조회한다.
        List<BcdDetail> bcdDetails = bcdDetailRepository.findAllByUserId(userId);

        // 2. 명함상세의 draftId로 BcdMaster와 매핑해 PendingResponseDTO로 반환한다.
        return bcdDetails.stream()
                .map(bcdDetail -> bcdMasterRepository.findByDraftIdAndStatusAndDrafterIdNot(bcdDetail.getDraftId(), "A", userId)
                        .map(newBcdMaster -> BcdPendingResponseDTO.of(newBcdMaster, bcdDetail))
                        .orElse(null)).filter(Objects::nonNull).toList();
    }

    /**
     * 수령 완료 후, 처리 완료 상태로
     * @param draftId draftId
     */
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

    /**
     * 수령 안내 메일 및 알림 전송
     * @param draftIds draftIds
     */
    @Override
    public void sendReceiptBcd(List<String> draftIds) {

        List<BcdDetail> bcdDetails = bcdDetailRepository.findAllByDraftIdIn(draftIds);

        // 1. 처리완료 상태
        bcdDetails.forEach(bcdDetail -> {
            BcdMaster bcdMaster = bcdMasterRepository.findById(bcdDetail.getDraftId())
                    .orElseThrow(() -> new  IllegalArgumentException("Not Found"));
            bcdMaster.updateStatus();
        });

        // 2. 알림 전송
        notificationSendService.sendBcdReceipt(draftIds);

        // 3. 메일 전송
        StdGroup stdGroup = stdGroupRepository.findByGroupCd("B003")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        StdDetail stdDetail = stdDetailRepository.findByGroupCdAndDetailCd(stdGroup, "003")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        String mailTitle = "[수령안내] 신청하신 명함이 도착했습니다.";
        String mailContent = "[수령안내] 신청하신 명함이 도착했습니다.\n담당 부서를 방문하여 명함을 수령하시고, 수령 확인 버튼을 눌러주시기 바랍니다.\n\n신청 내역은 아래 링크에서 확인하실 수 있습니다:\nhttp://172.16.250.87/login\n\n감사합니다.";

        bcdDetails.forEach(bcdDetail -> {
            String recipientEmail = bcdDetail.getEmail();
            try {
                emailService.sendEmailWithDynamicCredentials(
                        "smtp.sirteam.net",
                        465,
                        stdDetail.getEtcItem3(),
                        stdDetail.getEtcItem4(),
                        stdDetail.getEtcItem3(),
                        recipientEmail,
                        mailTitle,
                        mailContent,
                        null,
                        null,
                        null
                );
            } catch (Exception e) {
                log.error("Failed to send email to " + recipientEmail, e);
            }
        });
    }
}
