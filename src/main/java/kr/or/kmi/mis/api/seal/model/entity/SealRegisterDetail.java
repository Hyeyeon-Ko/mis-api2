package kr.or.kmi.mis.api.seal.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Entity
@Table(name = "hrmtregd")
public class SealRegisterDetail {

    @Id
    @Column(name = "draft_id")
    private Long draftId;

    @Column(length = 20)
    private String sealNm;

    @Column(length = 100)
    private Timestamp sealImage;

    @Column(length = 50)
    private String useDept;

    @Column(length = 1000)
    private int purpose;

    @Column(length = 20)
    private String manager;

    @Column(length = 20)
    private String subManager;

    @Column(length = 20)
    private String instCd;

}
