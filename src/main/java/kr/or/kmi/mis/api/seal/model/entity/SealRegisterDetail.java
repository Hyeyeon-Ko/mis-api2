package kr.or.kmi.mis.api.seal.model.entity;

import jakarta.persistence.*;
import kr.or.kmi.mis.api.seal.model.request.SealRegisterRequestDTO;
import kr.or.kmi.mis.api.seal.model.request.SealUpdateRequestDTO;
import kr.or.kmi.mis.cmm.model.entity.BaseSystemFieldEntity;
import lombok.*;

import java.sql.Timestamp;
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

    @Column
    private LocalDateTime lastUpdateDate;

    @Column(length = 20)
    private String instCd;

    private LocalDateTime deletedt;

    @Builder
    public SealRegisterDetail(String draftId, String sealNm, String sealImage, String sealImageNm, String useDept, String purpose,
                              String manager, String subManager, String draftDate, String instCd, LocalDateTime lastUpdateDate) {
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
        this.lastUpdateDate = lastUpdateDate;
    }

    public void update(SealRegisterRequestDTO sealRegisterRequestDTO, String newSealImage) {
        this.sealNm = sealRegisterRequestDTO.getSealNm();
        this.sealImage = newSealImage;
        this.useDept = sealRegisterRequestDTO.getUseDept();
        this.purpose = sealRegisterRequestDTO.getPurpose();
        this.manager = sealRegisterRequestDTO.getManager();
        this.subManager = sealRegisterRequestDTO.getSubManager();
    }

    public void updateFile(SealUpdateRequestDTO sealUpdateRequestDTO, String sealImage, String sealImageNm) {
        this.sealNm = sealUpdateRequestDTO.getSealNm();
        this.useDept = sealUpdateRequestDTO.getUseDept();
        this.purpose = sealUpdateRequestDTO.getPurpose();
        this.manager = sealUpdateRequestDTO.getManager();
        this.subManager = sealUpdateRequestDTO.getSubManager();
        this.sealImage = sealImage;
        this.sealImageNm = sealImageNm;
    }

    public void deleteSeal(LocalDateTime deletedt) {
        this.deletedt = deletedt;
    }
}
