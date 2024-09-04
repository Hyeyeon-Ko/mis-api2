package kr.or.kmi.mis.api.docstorage.domain.response;

import lombok.Builder;
import lombok.Data;

@Data
public class DeptResponseDTO {

    private String detailCd;
    private String detailNm;

    @Builder
    public DeptResponseDTO(String detailCd, String detailNm) {
        this.detailCd = detailCd;
        this.detailNm = detailNm;
    }
}
