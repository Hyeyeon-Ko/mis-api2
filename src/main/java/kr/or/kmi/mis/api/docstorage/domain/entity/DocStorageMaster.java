package kr.or.kmi.mis.api.docstorage.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "hrmtdslm")
public class DocStorageMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(length = 1)
    private String status;

    @Builder
    public DocStorageMaster(Timestamp draftDate, String drafter, String drafterId,
                            String instCd, String deptCd, String status) {
        this.draftDate = draftDate;
        this.drafter = drafter;
        this.drafterId = drafterId;
        this.instCd = instCd;
        this.deptCd = deptCd;
        this.status = status;
    }
}
