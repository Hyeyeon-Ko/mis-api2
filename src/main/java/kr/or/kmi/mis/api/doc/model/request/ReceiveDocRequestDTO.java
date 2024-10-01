package kr.or.kmi.mis.api.doc.model.request;

import kr.or.kmi.mis.api.doc.model.entity.DocDetail;
import kr.or.kmi.mis.api.doc.model.entity.DocMaster;
import lombok.Getter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
public class ReceiveDocRequestDTO {

    String drafterId;
    String drafter;
    String division;
    String sender;
    String receiver;
    String docTitle;
    String purpose;
    String instCd;
    String deptCd;

    // DocRequest Dto -> DocMaster Entity
    public DocMaster toMasterEntity(String draftId, String status) {
        return DocMaster.builder()
                .draftId(draftId)
                .title(String.format("문서수신 신청서 (%s)", drafter))
                .draftDate(LocalDateTime.now())
                .drafter(drafter)
                .drafterId(drafterId)
                .status(status)
                .instCd(instCd)
                .deptCd(deptCd)
                .build();
    }

    // DocRequest Dto -> DocDetail Entity
    public DocDetail toDetailEntity(String draftId) {
        return DocDetail.builder()
                .draftId(draftId)
                .division(division)
                .sender(sender)
                .receiver(receiver)
                .docTitle(docTitle)
                .purpose(purpose)
                .build();
    }

}
