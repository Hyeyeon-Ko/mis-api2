package kr.or.kmi.mis.cmm.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Sort;

/**
 * packageName    : kr.or.kmi.mis.cmm.model.request
 * fileName       : PostPageRequest
 * author         : KMI_DI
 * date           : 2024-10-01
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-10-01        KMI_DI       the first create
 */
@Getter
public class PostPageRequest {

    @Schema(description = "페이지 번호", required = true, example = "1")
    private int pageIndex;

    @Schema(description = "페이지 사이즈", required = true, example = "10")
    private int pageSize;

    @Builder
    public PostPageRequest(int pageIndex, int pageSize) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    public void setPage(int page) {
        this.pageIndex = page <= 0 ? 1 : page;
    }

    public void setSize(int size) {
        int DEFAULT_SIZE = 10;
        int MAX_SIZE = 50;
        this.pageSize = size > MAX_SIZE ? DEFAULT_SIZE : size;
    }


    public org.springframework.data.domain.PageRequest of() {
        // updtDt 컬럼 확인
        return org.springframework.data.domain.PageRequest.of(pageIndex -1, pageSize, Sort.Direction.DESC, "updtDt");
    }
}
