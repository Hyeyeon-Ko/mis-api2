package kr.or.kmi.mis.api.confirm.service.Impl;

import kr.or.kmi.mis.api.confirm.service.CorpDocConfirmService;
import kr.or.kmi.mis.api.corpdoc.model.entity.CorpDocMaster;
import kr.or.kmi.mis.api.corpdoc.repository.CorpDocMasterRepository;
import kr.or.kmi.mis.api.exception.EntityNotFoundException;
import kr.or.kmi.mis.api.noti.model.response.SseResponseDTO;
import kr.or.kmi.mis.api.noti.service.NotificationService;
import kr.or.kmi.mis.api.user.service.InfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Service
@RequiredArgsConstructor
public class CorpDocConfirmServiceImpl implements CorpDocConfirmService {

    private final CorpDocMasterRepository corpDocMasterRepository;
    private final NotificationService notificationService;
    private final InfoService infoService;

    private static final SimpleDateFormat simpleDataFormat = new SimpleDateFormat("yyyy.MM.dd");
    private static final SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

    private static final String type = "CORPDOC";
    private static final String now = simpleDateTimeFormat.format(new Timestamp(System.currentTimeMillis()));

    @Override
    @Transactional
    public void approve(Long draftId) {

        // 1. 법인서류신청 승인
        CorpDocMaster corpDocMaster = corpDocMasterRepository.findById(draftId)
                .orElseThrow(() -> new EntityNotFoundException("Not found: " + draftId));

        String approver = infoService.getUserInfo().getUserName();
        String approverId = infoService.getUserInfo().getUserId();
        corpDocMaster.approve(approver, approverId);

        corpDocMasterRepository.save(corpDocMaster);

        // 2. 알림 전송
        String content = "[승인완료] " + simpleDataFormat.format(corpDocMaster.getDraftDate())
                + " 신청한 [법인서류] 접수가 완료되었습니다./담당부서 방문 요청드립니다.";

        SseResponseDTO sseResponseDTO = SseResponseDTO.of(corpDocMaster.getDraftId(), content, type, now);
        Long drafterId = Long.parseLong(corpDocMaster.getDrafterId());
        notificationService.customNotify(drafterId, sseResponseDTO, "법인서류 승인완료");
    }

    @Override
    @Transactional
    public void reject(Long draftId, String rejectReason) {

        // 1. 법인서류신청 반려
        CorpDocMaster corpDocMaster = corpDocMasterRepository.findById(draftId)
                .orElseThrow(() -> new EntityNotFoundException("Not found: " + draftId));

        String disapprover = infoService.getUserInfo().getUserName();
        String disapproverId = infoService.getUserInfo().getUserId();
        corpDocMaster.disapprove(disapprover, disapproverId, rejectReason);

        corpDocMasterRepository.save(corpDocMaster);

        // 2. 알림 전송
        String content = "[반려] " + simpleDataFormat.format(corpDocMaster.getDraftDate())
                + " [법인서류] 신청이 반려되었습니다./반려 사유를 확인하세요.";

        SseResponseDTO sseResponseDTO = SseResponseDTO.of(corpDocMaster.getDraftId(), content, type, now);
        Long drafterId = Long.parseLong(corpDocMaster.getDrafterId());
        notificationService.customNotify(drafterId, sseResponseDTO, "법인서류신청 반려");

    }
}
