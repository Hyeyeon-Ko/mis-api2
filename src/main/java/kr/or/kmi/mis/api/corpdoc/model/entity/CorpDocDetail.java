package kr.or.kmi.mis.api.corpdoc.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.or.kmi.mis.cmm.model.entity.BaseSystemFieldEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "hrmtcorpd")
public class CorpDocDetail extends BaseSystemFieldEntity {

    @Id
    @Column(nullable = false)
    private Long draftId;

    @Column(length = 50)
    private String submission;

    @Column(length = 1000)
    private String purpose;

    @Column(length = 20)
    private String useDate;

    @Column(length = 255)
    private String fileName;

    @Column(length = 255)
    private String filePath;

    @Column(length = 10)
    private String certCorpseal;

    @Column(length = 10)
    private String certCoregister;

    @Column(length = 10)
    private String certUsesignet;

    @Column(length = 10)
    private String warrant;

    @Column(length = 1)
    private String type;

    @Column(length = 1000)
    private String notes;

}
