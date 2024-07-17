package kr.or.kmi.mis.api.authority.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Entity
@Table(name = "userauth")
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long authId;

    @Column(nullable = false, length = 20)
    private String userId;

    @Column(nullable = false, length = 20)
    private String hngNm;

    @Column(nullable = false, length = 20)
    private String instCd;

    @Column(nullable = false, length = 10)
    private String deptCd;

    @Column(nullable = false, length = 20)
    private String deptNm;

    @Column(nullable = false, length = 50)
    private String email;

    @Column(nullable = false, length = 10)
    private String role; // A: Master, B: Admin

    @CreationTimestamp
    private Timestamp createdt;
    private Timestamp deletedt;

    @Builder
    public Authority(String userId, String hngNm, String instCd, String deptCd, String deptNm, String email, String role, Timestamp createdt) {
        this.userId = userId;
        this.hngNm = hngNm;
        this.instCd = instCd;
        this.deptCd = deptCd;
        this.deptNm = deptNm;
        this.email = email;
        this.role = role;
        this.createdt = createdt;
    }

    // 권한 수정
    public void updateAdmin(String userRole) {
        this.role = userRole;
    }

    // 권한 취소 -> 종료일시 기록
    public void deleteAdmin(Timestamp deletedt) {
        this.deletedt = deletedt;
    }
}
