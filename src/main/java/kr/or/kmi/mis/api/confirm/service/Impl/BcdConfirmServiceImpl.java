package kr.or.kmi.mis.api.confirm.service.Impl;

import kr.or.kmi.mis.api.apply.model.request.ConfirmRequestDTO;
import kr.or.kmi.mis.api.bcd.model.entity.BcdDetail;
import kr.or.kmi.mis.api.bcd.model.entity.BcdMaster;
import kr.or.kmi.mis.api.bcd.model.response.BcdDetailResponseDTO;
import kr.or.kmi.mis.api.bcd.repository.BcdDetailRepository;
import kr.or.kmi.mis.api.bcd.repository.BcdMasterRepository;
import kr.or.kmi.mis.api.bcd.repository.impl.BcdApplyQueryRepositoryImpl;
import kr.or.kmi.mis.api.confirm.model.request.BcdApproveRequestDTO;
import kr.or.kmi.mis.api.confirm.model.request.BcdDisapproveRequestDTO;
import kr.or.kmi.mis.api.confirm.model.response.BcdHistoryResponseDTO;
import kr.or.kmi.mis.api.confirm.service.BcdConfirmService;
import kr.or.kmi.mis.api.exception.EntityNotFoundException;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class BcdConfirmServiceImpl implements BcdConfirmService {

    private final BcdMasterRepository bcdMasterRepository;
    private final BcdDetailRepository bcdDetailRepository;
    private final NotificationSendService notificationSendService;
    private final StdBcdService stdBcdService;
    private final InfoService infoService;
    private final EmailService emailService;
    private final StdGroupRepository stdGroupRepository;
    private final StdDetailRepository stdDetailRepository;
    private final BcdApplyQueryRepositoryImpl bcdApplyQueryRepositoryImpl;

    /**
     * BcdMaster 엔티티를 draftId로 조회하여 반환.
     * @param draftId 결재 문서 ID
     * @return BcdMaster 엔티티
     */
    private BcdMaster getBcdMaster(String draftId) {
        return bcdMasterRepository.findById(draftId)
                .orElseThrow(() -> new EntityNotFoundException("BcdMaster not found for draft ID: " + draftId));
    }

    /**
     * BcdDetail 엔티티를 draftId로 조회하여 반환.
     * @param draftId 결재 문서 ID
     * @return BcdDetail 엔티티
     */
    private BcdDetail getBcdDetail(String draftId) {
        return bcdDetailRepository.findById(draftId)
                .orElseThrow(() -> new EntityNotFoundException("BcdDetail not found for draft ID: " + draftId));
    }

    /**
     * 결재 상세 정보를 반환.
     * @param draftId 결재 문서 ID
     * @return BcdDetailResponseDTO 결재 상세 정보 DTO
     */
    @Override
    @Transactional(readOnly = true)
    public BcdDetailResponseDTO getBcdDetailInfo(String draftId) {
        BcdMaster bcdMaster = getBcdMaster(draftId);
        BcdDetail bcdDetail = getBcdDetail(draftId);

        String drafter = bcdMaster.getDrafter();
        List<String> names = stdBcdService.getBcdStdNames(bcdDetail);

        return BcdDetailResponseDTO.of(bcdDetail, drafter, names);
    }

    /**
     * 결재 승인 처리.
     * @param draftId 결재 문서 ID
     * @param confirmRequestDTO 결재 요청 데이터
     */
    @Override
    @Transactional
    public void approve(String draftId, ConfirmRequestDTO confirmRequestDTO) {

        BcdMaster bcdMaster = getBcdMaster(draftId);

        String approverId = confirmRequestDTO.getUserId();
        String approver = infoService.getUserInfoDetail(approverId).getUserName();

        BcdApproveRequestDTO approveRequest = createApproveRequest(approverId, approver);
        bcdMaster.updateApprove(approveRequest);
        bcdMasterRepository.save(bcdMaster);
    }

    /**
     * 결재 반려 처리.
     * @param draftId 결재 문서 ID
     * @param confirmRequestDTO 결재 요청 데이터
     */
    @Override
    @Transactional
    public void disapprove(String draftId, ConfirmRequestDTO confirmRequestDTO) {

        BcdMaster bcdMaster = getBcdMaster(draftId);
        BcdDetail bcdDetail = getBcdDetail(draftId);

        String disapproverId = confirmRequestDTO.getUserId();
        String disapprover = infoService.getUserInfoDetail(disapproverId).getUserName();

        // 1. 반려 처리 요청 생성
        BcdDisapproveRequestDTO disapproveRequest = createDisapproveRequest(confirmRequestDTO, disapprover, disapproverId);
        bcdMaster.updateDisapprove(disapproveRequest);
        bcdMasterRepository.save(bcdMaster);

        // 2. 반려 알림 및 메일 전송
        StdGroup stdGroup = stdGroupRepository.findByGroupCd("B003")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        StdDetail stdDetail = stdDetailRepository.findByGroupCdAndDetailCd(stdGroup, "003")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        String mailTitle = "[반려] 명함 신청이 반려되었습니다.";
        String mailContent = "[반려] 명함 신청이 반려되었습니다.\n반려 사유를 확인하신 후, 재신청해 주시기 바랍니다.\n\n아래 링크에서 확인하실 수 있습니다:\nhttp://172.16.250.87/login";

        sendRejectionNotifications(bcdMaster, bcdDetail);
        emailService.sendEmailWithDynamicCredentials(
                "smtp.sirteam.net",
                465,
                stdDetail.getEtcItem3(),
                stdDetail.getEtcItem4(),
                stdDetail.getEtcItem3(),
                bcdDetail.getEmail(),
                mailTitle,
                mailContent,
                null,
                null,
                null
        );
    }

    /**
     * 승인 요청 생성
     * @param approverId 결재자 ID
     * @param approver 결재자 이름
     * @return BcdApproveRequestDTO 승인 요청
     */
    private BcdApproveRequestDTO createApproveRequest(String approverId, String approver) {
        return BcdApproveRequestDTO.builder()
                .approverId(approverId)
                .approver(approver)
                .respondDate(LocalDateTime.now())
                .status("B")
                .build();
    }

    /**
     * 반려 요청 생성
     * @param confirmRequestDTO 반려 요청 데이터
     * @param disapprover 반려자 이름
     * @param disapproverId 반려자 ID
     * @return BcdDisapproveRequestDTO 반려 요청
     */
    private BcdDisapproveRequestDTO createDisapproveRequest(ConfirmRequestDTO confirmRequestDTO, String disapprover, String disapproverId) {
        return BcdDisapproveRequestDTO.builder()
                .disapproverId(disapproverId)
                .disapprover(disapprover)
                .rejectReason(confirmRequestDTO.getRejectReason())
                .respondDate(LocalDateTime.now())
                .status("C")
                .build();
    }

    /**
     * 반려 알림 전송
     * @param bcdMaster 결재 마스터 엔티티
     * @param bcdDetail 결재 상세 엔티티
     */
    private void sendRejectionNotifications(BcdMaster bcdMaster, BcdDetail bcdDetail) {
        if (!Objects.equals(bcdMaster.getDrafterId(), bcdDetail.getUserId())) {
            notificationSendService.sendBcdRejection(bcdMaster.getDraftDate(), bcdDetail.getUserId());
        }
        notificationSendService.sendBcdRejection(bcdMaster.getDraftDate(), bcdMaster.getDrafterId());
    }

    /**
     * 신청 이력 조회
     * @param postSearchRequestDTO 검색 요청 데이터
     * @param page 페이지 정보
     * @param draftId 결재 문서 ID
     * @return 신청 이력 페이지
     */
    @Override
    @Transactional(readOnly = true)
    public Page<BcdHistoryResponseDTO> getBcdApplicationHistory2(PostSearchRequestDTO postSearchRequestDTO, Pageable page, String draftId) {
        return bcdApplyQueryRepositoryImpl.getBcdApplicationHistory(postSearchRequestDTO, page, draftId);
    }
}
