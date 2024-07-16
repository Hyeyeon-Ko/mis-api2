package kr.or.kmi.mis.api.std.model.response;

import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Builder
@Data
@AllArgsConstructor
public class StdDetailResponseDTO {

    private String clsCd;          // 대분류코드 : 공통(A), 권한(B)
    private String etcCd;          // 중분류코드 : 센터정보(A001), 부서정보(A002)
    private String cdName;         // 분류명칭 : 센터정보, 부서정보
    private String etcDetlCd;      // 소분류코드 (시퀀스)
    private String fromDd;         // 시작일자
    private String toDd;           // 종료일자
    private String fstRgstr;       // 최초등록자
    private Timestamp fstRgstrDt;  // 최초등록일시
    private String lastUpdtr;      // 최종수정자
    private Timestamp lastUpdtDt;  // 최종수정일시
    private String etcItem1;       // 센터명
    private String etcItem2;       // 센터주소1
    private String etcItem3;       // 센터주소1(영문)
    private String etcItem4;       // 센터주소2
    private String etcItem5;       // 센터주소2(영문)

/*    public static StdDetailResponseDTO of(StdDetail stdDetail) {

        return StdDetailResponseDTO.builder()
                .clsCd(stdDetail.getClsCd())
                .etcCd(stdDetail.getEtcCd())
                .cdName(stdDetail.getCdName())
                .etcDetlCd(stdDetail.getEtcDetlCd())
                .fromDd(stdDetail.getFromDd())
                .toDd(stdDetail.getToDd())
                .fstRgstr(stdDetail.getFstRgstr())
                .fstRgstrDt(stdDetail.getFstRgstrDt())
                .lastUpdtr(stdDetail.getLastUpdtr())
                .lastUpdtDt(stdDetail.getLastUpdtDt())
                .etcItem1(stdDetail.getEtcItem1())
                .etcItem2(stdDetail.getEtcItem2())
                .etcItem3(stdDetail.getEtcItem3())
                .etcItem4(stdDetail.getEtcItem4())
                .etcItem5(stdDetail.getEtcItem5())
                .build();
    }*/
}
