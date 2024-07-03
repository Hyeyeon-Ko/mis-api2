package kr.or.kmi.mis.api.sample.model.response;


import lombok.Builder;
import lombok.Data;

/**
 * packageName    : kr.or.kmi.mis.api.sample.model.response
 * fileName       : SampleResponseDTO
 * author         : KMI_DI
 * date           : 2024-07-02
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-02        KMI_DI       the first create
 */
@Data
public class SampleResponseDTO {
    private String id;
    private String name;

    @Builder
    public SampleResponseDTO(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
