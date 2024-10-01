package kr.or.kmi.mis.cmm.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Data
public class BaseSystemFieldEntity {

    @Column(name = "rgstr_id")
    private String rgstrId;

    @Column(name = "rgst_dt")
    private LocalDateTime rgstDt;

    @Column(name = "updtr_id")
    private String updtrId;

    @Column(name = "updt_dt")
    private LocalDateTime updtDt;

}
