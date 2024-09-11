package kr.or.kmi.mis.api.confirm.service.Impl;

import kr.or.kmi.mis.api.confirm.service.DocConfirmService;
import kr.or.kmi.mis.api.doc.model.entity.DocDetail;
import kr.or.kmi.mis.api.doc.model.entity.DocMaster;
import kr.or.kmi.mis.api.doc.repository.DocDetailRepository;
import kr.or.kmi.mis.api.doc.repository.DocMasterRepository;
import kr.or.kmi.mis.api.exception.EntityNotFoundException;
import kr.or.kmi.mis.api.noti.model.response.SseResponseDTO;
import kr.or.kmi.mis.api.noti.service.NotificationService;
import kr.or.kmi.mis.api.user.service.InfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DocConfirmServiceImpl implements DocConfirmService {

    private final InfoService infoService;
    private final NotificationService notificationService;
    private final DocMasterRepository docMasterRepository;
    private final DocDetailRepository docDetailRepository;

    @Override
    @Transactional
    public void confirm(Long draftId) {

        // 1. 문서수발신신청 승인
        DocMaster docMaster = docMasterRepository.findById(draftId)
                .orElseThrow(() -> new EntityNotFoundException("docMaster not found: " + draftId));
        DocDetail docDetail = docDetailRepository.findById(draftId)
                .orElseThrow(() -> new EntityNotFoundException("docDetail not found: " + draftId));

        // 승인 상태 변경
        String approver = infoService.getUserInfo().getUserName();
        String approverId = infoService.getUserInfo().getUserId();
        docMaster.confirm("E", approver, approverId);

        // 문서번호 생성해, 업데이트
        DocDetail lastDocDetail = docDetailRepository
                .findFirstByDocIdNotNullAndDivisionOrderByDocIdDesc(docDetail.getDivision()).orElse(null);
        String docId = "";
        if (lastDocDetail != null) {
            docId = createDocId(lastDocDetail.getDocId());
        } else {
            docId = createDocId("");
        }

        docDetail.updateDocId(docId);

        // 2. 알림 전송
        SimpleDateFormat simpleDataFormat = new SimpleDateFormat("yyyy.MM.dd");
        SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

        String type = "DOC";
        String now = simpleDateTimeFormat.format(new Timestamp(System.currentTimeMillis()));
        String docType = Objects.equals(docDetail.getDivision(), "A") ? "수신문서" : "발신문서";

        String content = "[승인완료] " + simpleDataFormat.format(docMaster.getDraftDate())
                + " 신청한 [" + docType + "] 접수가 완료되었습니다./담당부서 방문 요청드립니다.";

        SseResponseDTO sseResponseDTO = SseResponseDTO.of(docMaster.getDraftId(), content, type, now);
        Long drafterId = Long.parseLong(docMaster.getDrafterId());
        notificationService.customNotify(drafterId, sseResponseDTO, "문서수발신신청 승인완료");
    }

    @Override
    @Transactional
    public void delete(Long draftId) {

        DocMaster docMaster = docMasterRepository.findById(draftId)
                .orElseThrow(() -> new EntityNotFoundException("docMaster not found: " + draftId));

        // 신청 취소 상태로 변경, 취소일시로 update
        docMaster.delete("F");

        docMasterRepository.save(docMaster);
    }

    public static String createDocId(String lastDocId) {

        int num = 1;
        String nowYear = String.valueOf(LocalDate.now().getYear()).substring(2);

        if (!Objects.equals(lastDocId, "")) {
            String[] parts = lastDocId.split("-");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid code format");
            }

            String year = parts[0];
            num = Integer.parseInt(parts[1]);

            if (nowYear.equals(year)) {
                num++;
            } else {
                num = 1;
            }
        }

        return nowYear + "-" + String.format("%03d", num);
    }


}
