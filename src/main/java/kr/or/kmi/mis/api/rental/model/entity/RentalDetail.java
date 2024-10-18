package kr.or.kmi.mis.api.rental.model.entity;

import jakarta.persistence.*;
import kr.or.kmi.mis.api.rental.model.request.RentalRequestDTO;
import kr.or.kmi.mis.api.rental.model.response.RentalExcelResponseDTO;
import kr.or.kmi.mis.cmm.model.entity.BaseSystemFieldEntity;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Entity
@Table(name = "HRMTRSMD")
public class RentalDetail extends BaseSystemFieldEntity {

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
    public RentalDetail(Long detailId, String instCd, String category, String companyNm, String contractNum,
                        String modelNm, String installDate, String expiryDate, String rentalFee,
                        String location, String installationSite, String specialNote) {
        this.detailId = detailId;
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
        this.status = "A";
        this.setRgstDt(LocalDateTime.now());
    }

    public void update(RentalRequestDTO rentalUpdateRequestDTO) {
        this.category = rentalUpdateRequestDTO.getCategory();
        this.companyNm = rentalUpdateRequestDTO.getCompanyNm();
        this.contractNum = rentalUpdateRequestDTO.getContractNum();
        this.modelNm = rentalUpdateRequestDTO.getModelNm();
        this.installDate = rentalUpdateRequestDTO.getInstallDate();
        this.expiryDate = rentalUpdateRequestDTO.getExpiryDate();
        this.rentalFee = rentalUpdateRequestDTO.getRentalFee();
        this.location = rentalUpdateRequestDTO.getLocation();
        this.installationSite = rentalUpdateRequestDTO.getInstallationSite();
        this.specialNote = rentalUpdateRequestDTO.getSpecialNote();
    }

    public void updateStatus(String newStatus) {
        this.status = newStatus;
    }
    public void updateExcelData(RentalExcelResponseDTO dto) {
        this.category = dto.getCategory();
        this.companyNm = dto.getCompanyNm();
        this.contractNum = dto.getContractNum();
        this.modelNm = dto.getModelNm();
        this.installDate = dto.getInstallDate();
        this.expiryDate = dto.getExpiryDate();
        this.rentalFee = dto.getRentalFee();
        this.location = dto.getLocation();
        this.installationSite = dto.getInstallationSite();
        this.specialNote = dto.getSpecialNote();
    }
}
