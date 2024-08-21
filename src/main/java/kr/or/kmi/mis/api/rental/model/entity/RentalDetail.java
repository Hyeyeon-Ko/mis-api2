package kr.or.kmi.mis.api.rental.model.entity;

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
@Table(name = "HRMTRSMD")
public class RentalDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long detailId;

    @Column
    private Long draftId;

    @Column(length = 100)
    private String category;

    @Column(length = 20)
    private String companyNm;

    @Column(length = 20)
    private String contractNum;

    @Column(length = 20)
    private String modelNm;

    @Column
    private Timestamp installDate;

    @Column
    private Timestamp expiryDate;

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

}
