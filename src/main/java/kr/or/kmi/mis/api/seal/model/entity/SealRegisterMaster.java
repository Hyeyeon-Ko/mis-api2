package kr.or.kmi.mis.api.seal.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Entity
@Table(name = "hrmtregm")
public class SealRegisterMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long draftId;

    @Column(nullable = false)
    private Timestamp draftDate;

    @Column(nullable = false, length = 20)
    private String drafter;

    @Column(length = 20)
    private String drafterId;

}
