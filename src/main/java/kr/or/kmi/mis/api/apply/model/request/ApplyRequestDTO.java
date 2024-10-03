package kr.or.kmi.mis.api.apply.model.request;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * packageName    : kr.or.kmi.mis.api.apply.model.request
 * fileName       : ApplyRequestDTO
 * author         : KMI_DI
 * date           : 2024-10-01
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-10-01        KMI_DI       the first create
 */
@Data
    @RequiredArgsConstructor
    public class ApplyRequestDTO {
        private String userId;
        private String instCd;
        private String documentType;
        //권한
}
