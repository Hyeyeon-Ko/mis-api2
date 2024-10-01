package kr.or.kmi.mis.api.docstorage.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "hrmtdslm")
public class DocStorageMaster {

    @Id
    @Column(nullable = false, length = 12)
    private String draftId;

    @Column
    private LocalDateTime draftDate;

    @Column(length = 20)
    private String drafter;

    @Column(length = 20)
    private String drafterId;

    @Column(length = 20)
    private String instCd;

    @Column(length = 20)
    private String deptCd;

    @Column(length = 1)
    private String type;       // A: 이관, B: 파쇄

    @Column(length = 1)
    private String status;

    @Builder
    public DocStorageMaster(String draftId, LocalDateTime draftDate, String drafter, String drafterId,
                            String instCd, String deptCd, String type, String status) {
        this.draftId = draftId;
        this.draftDate = draftDate;
        this.drafter = drafter;
        this.drafterId = drafterId;
        this.instCd = instCd;
        this.deptCd = deptCd;
        this.type = type;
        this.status = status;
    }

    public void updateStatus(String newStatus) {
        this.status = newStatus;
    }
}
