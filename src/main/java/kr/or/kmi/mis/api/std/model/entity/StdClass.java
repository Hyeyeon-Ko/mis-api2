package kr.or.kmi.mis.api.std.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "cmm_cl_code")
public class StdClass {

    @Id
    @Column(name = "class_cd", length = 20)
    private String classCd;          // 대분류코드 : A, B

    @Column(name = "class_nm", length = 20, nullable = false)
    private String classNm;          // 중분류코드 : 공통, 권한

}
