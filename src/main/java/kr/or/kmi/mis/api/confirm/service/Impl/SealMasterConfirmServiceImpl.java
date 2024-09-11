package kr.or.kmi.mis.api.confirm.service.Impl;

import kr.or.kmi.mis.api.confirm.service.SealMasterConfirmService;
import kr.or.kmi.mis.api.noti.model.response.SseResponseDTO;
import kr.or.kmi.mis.api.noti.service.NotificationService;
import kr.or.kmi.mis.api.seal.model.entity.SealMaster;
import kr.or.kmi.mis.api.seal.repository.SealMasterRepository;
import kr.or.kmi.mis.api.user.service.InfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Service
@RequiredArgsConstructor
public class SealMasterConfirmServiceImpl implements SealMasterConfirmService {

    private final SealMasterRepository sealMasterRepository;
    private final InfoService infoService;
    private final NotificationService notificationService;

    private static final SimpleDateFormat simpleDataFormat = new SimpleDateFormat("yyyy.MM.dd");
    private static final SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

    private static final String type = "SEAL";
    private static final String now = simpleDateTimeFormat.format(new Timestamp(System.currentTimeMillis()));

    @Override
    @Transactional
    public void approve(Long draftId) {

        // 1. 인장신청 승인
        SealMaster sealMaster = sealMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        String approver = infoService.getUserInfo().getUserName();
        String approverId = infoService.getUserInfo().getUserId();

        sealMaster.confirm("E", approver, approverId);
        sealMasterRepository.save(sealMaster);

        // 2. 알림 전송
        String content = "[승인완료] " + simpleDataFormat.format(sealMaster.getDraftDate())
                + " 신청한 [날인요청] 접수가 완료되었습니다./담당부서 방문 요청드립니다.";

        SseResponseDTO sseResponseDTO = SseResponseDTO.of(sealMaster.getDraftId(), content, type, now);
        Long drafterId = Long.parseLong(sealMaster.getDrafterId());
        notificationService.customNotify(drafterId, sseResponseDTO, "인장신청 승인");
    }

    @Override
    @Transactional
    public void disapprove(Long draftId, String rejectReason) {

        // 1. 인장신청 반려
        SealMaster sealMaster = sealMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        String disapprover = infoService.getUserInfo().getUserName();
        String disapproverId = infoService.getUserInfo().getUserId();

        sealMaster.reject("C", disapprover, disapproverId, rejectReason);
        sealMasterRepository.save(sealMaster);

        // 2. 알림 전송
        String content = "[반려] " + simpleDataFormat.format(sealMaster.getDraftDate())
                + " [인장신청]이 반려되었습니다./반려 사유를 확인하세요.";

        SseResponseDTO sseResponseDTO = SseResponseDTO.of(sealMaster.getDraftId(), content, type, now);
        Long drafterId = Long.parseLong(sealMaster.getDrafterId());
        notificationService.customNotify(drafterId, sseResponseDTO, "인장신청 반려");
    }
}
