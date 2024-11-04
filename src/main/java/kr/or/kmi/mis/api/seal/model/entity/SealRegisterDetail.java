package kr.or.kmi.mis.api.seal.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.or.kmi.mis.cmm.model.entity.BaseSystemFieldEntity;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Entity
@Table(name = "hrmtregd")
public class SealRegisterDetail extends BaseSystemFieldEntity {

    @Id
    @Column(nullable = false, length = 12)
    private String draftId;

    @Column(length = 20)
    private String sealNm;

    @Column(name = "seal_image", columnDefinition = "longtext")
    private String sealImage;

    @Column(name = "seal_image_nm", length = 255)
    private String sealImageNm;

    @Column(length = 50)
    private String useDept;

    @Column(length = 1000)
    private String purpose;

    @Column(length = 20)
    private String manager;

    @Column(length = 20)
    private String subManager;

    @Column(length = 20)
    private String draftDate;

    @Column(length = 20)
    private String instCd;

    private LocalDateTime deletedt;

    @Builder
    public SealRegisterDetail(String draftId, String sealNm, String sealImage, String sealImageNm, String useDept, String purpose,
                              String manager, String subManager, String draftDate, String instCd) {
        this.draftId = draftId;
        this.sealNm = sealNm;
        this.sealImage = sealImage;
        this.sealImageNm = sealImageNm;
        this.useDept = useDept;
        this.purpose = purpose;
        this.manager = manager;
        this.subManager = subManager;
        this.draftDate = draftDate;
        this.instCd = instCd;
    }

    public void deleteSeal(LocalDateTime deleteDt) {
        this.deletedt = deleteDt;
    }
}
