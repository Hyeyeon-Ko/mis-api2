package kr.or.kmi.mis.api.doc.model.request;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * packageName    : kr.or.kmi.mis.api.doc.model.request
 * fileName       : DocRequestDTO
 * author         : KMI_DI
 * date           : 2024-10-07
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-10-07        KMI_DI       the first create
 */
@RequiredArgsConstructor
@Data
public class DocRequestDTO {
    private String instCd;
    private String status;
}
