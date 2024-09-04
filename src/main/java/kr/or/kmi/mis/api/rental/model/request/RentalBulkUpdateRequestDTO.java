package kr.or.kmi.mis.api.rental.model.request;

import lombok.Data;

import java.util.List;

@Data
public class RentalBulkUpdateRequestDTO {

    private String category;
    private String companyNm;
    private String modelNm;
    private String installDate;
    private String expiryDate;
    private String rentalFee;
    private String location;
    private String installationSite;
    private String specialNote;

    private List<Long> detailIds;
}
