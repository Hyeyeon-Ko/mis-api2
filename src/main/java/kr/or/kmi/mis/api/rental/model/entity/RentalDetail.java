package kr.or.kmi.mis.api.rental.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Entity
@Table(name = "HRMTRSMD")
public class RentalDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long detailId;

    @Column(length = 20)
    private String instCd;

    @Column(length = 100)
    private String category;

    @Column(length = 20)
    private String companyNm;

    @Column(length = 20)
    private String contractNum;

    @Column(length = 20)
    private String modelNm;

    @Column
    private String installDate;

    @Column
    private String expiryDate;

    @Column(length = 20)
    private String rentalFee;

    @Column(length = 100)
    private String location;

    @Column(length = 20)
    private String installationSite;

    @Column(length = 200)
    private String specialNote;

    @Column(length = 1)
    private String status;

    @Builder
    public RentalDetail(String instCd, String category, String companyNm, String contractNum,
                        String modelNm, String installDate, String expiryDate, String rentalFee,
                        String location, String installationSite, String specialNote, String status) {
        this.instCd = instCd;
        this.category = category;
        this.companyNm = companyNm;
        this.contractNum = contractNum;
        this.modelNm = modelNm;
        this.installDate = installDate;
        this.expiryDate = expiryDate;
        this.rentalFee = rentalFee;
        this.location = location;
        this.installationSite = installationSite;
        this.specialNote = specialNote;
        this.status = status;
    }


}
