package kr.or.kmi.mis.api.rental.model.request;

import kr.or.kmi.mis.api.rental.model.entity.RentalDetail;
import lombok.Getter;

@Getter
public class RentalRequestDTO {

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

    public RentalDetail toEntity(String instCd) {
        return RentalDetail.builder()
                .instCd(instCd)
                .category(category)
                .companyNm(companyNm)
                .contractNum(contractNum)
                .modelNm(modelNm)
                .installDate(installDate)
                .expiryDate(expiryDate)
                .rentalFee(rentalFee)
                .location(location)
                .installationSite(installationSite)
                .specialNote(specialNote)
                .build();
    }

}
