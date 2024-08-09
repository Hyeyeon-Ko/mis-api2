package kr.or.kmi.mis.api.bcd.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * packageName    : kr.or.kmi.mis.api.bcd.model.response
 * fileName       : BcdSampleResponseDTO
 * author         : KMI_DI
 * date           : 2024-07-17
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-17        KMI_DI       the first create
 */
@Data
@AllArgsConstructor
public class BcdSampleResponseDTO {
    private String detailCd;
    private String detailNm;
}
