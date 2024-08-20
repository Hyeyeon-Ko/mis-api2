package kr.or.kmi.mis.api.doc.model.request;

import kr.or.kmi.mis.api.doc.model.entity.DocDetail;
import kr.or.kmi.mis.api.doc.model.entity.DocMaster;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class DocRequestDTO {

    private String drafterId;
    private String drafter;
    private String division;
    private String sender;
    private String receiver;
    private String docTitle;
    private String purpose;
    private String instCd;

    // DocRequest Dto -> DocMaster Entity
    public DocMaster toMasterEntity() {
        return DocMaster.builder()
                .draftDate(new Timestamp(System.currentTimeMillis()))
                .drafter(drafter)
                .drafterId(drafterId)
                .status("A")
                .instCd(instCd)
                .build();
    }

    // DocRequest Dto -> DocDetail Entity
    public DocDetail toDetailEntity(Long draftId, String fileName, String filePath) {
        return DocDetail.builder()
                .draftId(draftId)
                .division(division)
                .sender(sender)
                .receiver(receiver)
                .docTitle(docTitle)
                .purpose(purpose)
                .fileName(fileName)
                .filePath(filePath)
                .build();
    }
}
