package kr.or.kmi.mis.api.sample.model.entity;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * packageName    : kr.or.kmi.mis.api.sample.model.entity
 * fileName       : SampleEntity
 * author         : KMI_DI
 * date           : 2024-07-02
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-02        KMI_DI       the first create
 */
@Data
@RequiredArgsConstructor
//@Entity DB설정되고나서 적용
public class SampleEntity {
    private String id;
    private String name;

    @Builder
    public SampleEntity(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
