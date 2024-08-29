package kr.or.kmi.mis.api.seal.model.entity;

import jakarta.persistence.*;
import kr.or.kmi.mis.api.seal.model.request.SealUpdateRequestDTO;
import kr.or.kmi.mis.cmm.model.entity.BaseSystemFieldEntity;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Entity
@Table(name = "hrmtregd")
public class SealRegisterDetail extends BaseSystemFieldEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long draftId;

    @Column(length = 20)
    private String sealNm;

    @Column(length = 100)
    private String sealImage;

    @Column(length = 100)
    private String sealImagePath;

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

    @Builder
    public SealRegisterDetail(Long draftId, String sealNm, String sealImage, String sealImagePath, String useDept,
                              String purpose, String manager, String subManager, String draftDate, String instCd) {
        this.draftId = draftId;
        this.sealNm = sealNm;
        this.sealImage = sealImage;
        this.sealImagePath = sealImagePath;
        this.useDept = useDept;
        this.purpose = purpose;
        this.manager = manager;
        this.subManager = subManager;
        this.draftDate = draftDate;
        this.instCd = instCd;
    }

    public void update(SealUpdateRequestDTO sealUpdateRequestDTO) {
        this.sealNm = sealUpdateRequestDTO.getSealNm();
        this.sealImage = sealUpdateRequestDTO.getSealImage();
        this.useDept = sealUpdateRequestDTO.getUseDept();
        this.purpose = sealUpdateRequestDTO.getPurpose();
        this.manager = sealUpdateRequestDTO.getManager();
        this.subManager = sealUpdateRequestDTO.getSubManager();
    }
}
