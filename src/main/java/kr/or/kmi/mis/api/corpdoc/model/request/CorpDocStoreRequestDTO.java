package kr.or.kmi.mis.api.corpdoc.model.request;

import kr.or.kmi.mis.api.corpdoc.model.entity.CorpDocDetail;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CorpDocStoreRequestDTO {
    private String userId;
    private String userNm;
    private String instCd;
    private String storeDate;
    private String purpose;
    private int certCorpseal;
    private int certCoregister;
    private int totalCorpseal;
    private int totalCoregister;

    public CorpDocDetail toEntity(Long draftId) {
        return CorpDocDetail.builder()
                .draftId(draftId)
                .purpose(purpose)
                .issueDate(storeDate)
                .certCorpseal(certCorpseal)
                .certCoregister(certCoregister)
                .build();
    }
}
