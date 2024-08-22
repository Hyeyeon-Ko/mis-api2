package kr.or.kmi.mis.api.rental.model.response;

import lombok.Builder;
import lombok.Data;

@Data
public class RentalExcelResponseDTO {

    private String category;
    private String companyNm;
    private String contractNum;
    private String modelNm;
    private String installDate;
    private String expiryDate;
    private String rentalFee;
    private String location;
    private String installationSite;
    private String specialNote;
    private String instCd;

    @Builder
    public RentalExcelResponseDTO(String category, String companyNm, String contractNum, String modelNm,
                                  String installDate, String expiryDate, String rentalFee, String location,
                                  String installationSite, String specialNote, String instCd) {
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
        this.instCd = instCd;
    }

}
