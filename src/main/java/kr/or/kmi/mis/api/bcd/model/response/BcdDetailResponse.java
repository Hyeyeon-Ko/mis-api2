package kr.or.kmi.mis.api.bcd.model.response;

import kr.or.kmi.mis.api.bcd.model.entity.BcdDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class BcdDetailResponse {

    private Long draftId;
    private Long logId;
    private String userId;
    private String division;
    private String korNm;
    private String engNm;
    private String center;
    private String dep;
    private String team;
    private String grade;
    private String extTel;
    private String faxTel;
    private String phoneTel;
    private String email;
    private String address;
    private Integer quantity;

    // BcdMaster Entity -> BcdMaster response Dto
    public static BcdDetailResponse of(BcdDetail bcdDetail) {
        return BcdDetailResponse.builder()
                .draftId(bcdDetail.getDraftId())
                .logId(bcdDetail.getSeqId())
                .userId(bcdDetail.getUserId())
                .division(bcdDetail.getDivision())
                .korNm(bcdDetail.getKorNm())
                .engNm(bcdDetail.getEngNm())
                .center(bcdDetail.getInstNm())
                .dep(bcdDetail.getDeptNm())
                .team(bcdDetail.getTeamNm())
                .grade(bcdDetail.getGrade())
                .extTel(bcdDetail.getExtTel())
                .faxTel(bcdDetail.getFaxTel())
                .phoneTel(bcdDetail.getPhoneTel())
                .email(bcdDetail.getEmail())
                .address(bcdDetail.getAddress())
                .quantity(bcdDetail.getQuantity())
                .build();
    }
}
