package kr.or.kmi.mis.api.docstorage.domain.response;

import lombok.Builder;
import lombok.Data;

@Data
public class CenterResponseDTO {

    private String detailNm;
    private String detailCd;

    @Builder
    public CenterResponseDTO(String detailNm, String detailCd) {
        this.detailNm = detailNm;
        this.detailCd = detailCd;
    }
}
