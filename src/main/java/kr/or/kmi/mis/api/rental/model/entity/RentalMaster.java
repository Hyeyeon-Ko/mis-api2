package kr.or.kmi.mis.api.rental.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Entity
@Table(name = "HRMTRSMM")
public class RentalMaster {

    @Id
    @Column(name = "draft_id", nullable = false)
    private Long draftId;

    @Column
    private Timestamp draftDate;

    @Column(length = 20)
    private String drafter;

    @Column(length = 20)
    private String drafterId;

    @Column(length = 20)
    private String instCd;

    @Column(length = 20)
    private String deptCd;

}
