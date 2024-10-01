package kr.or.kmi.mis.cmm.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * packageName    : kr.or.kmi.mis.cmm.model.request
 * fileName       : PostSearchRequestDTO
 * author         : KMI_DI
 * date           : 2024-10-01
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-10-01        KMI_DI       the first create
 */
@Data
@NoArgsConstructor
public class PostSearchRequestDTO {
    @Schema(description = "검색범위-시작")
    private String startDate;
    @Schema(description = "검색범위-끝")
    private String endDate;
    @Schema(description = "검색조건")
    private String searchType;
    @Schema(description = "검색어")
    private String keyword;
}
