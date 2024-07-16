package kr.or.kmi.mis.api.std.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "cmm_group_code")
public class StdGroup {

    @Id
    @Column(name = "group_cd", length = 20)
    private String groupCd;

    @ManyToOne
    @JoinColumn(name = "class_cd", nullable = false)
    private StdClass classCd;

    @Column(name = "group_nm", length = 20, nullable = false)
    private String groupNm;

}
