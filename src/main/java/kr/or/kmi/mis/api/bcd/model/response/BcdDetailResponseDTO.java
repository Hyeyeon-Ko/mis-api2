package kr.or.kmi.mis.api.bcd.model.response;

import kr.or.kmi.mis.api.bcd.model.entity.BcdDetail;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
public class BcdDetailResponseDTO {

    private Long draftId;
    private Long logId;
    private String userId;
    private String division;
    private String korNm;
    private String engNm;
    private String center;
    private String dep;
    private String team;
    private String engTeam;
    private String grade;
    private String engGrade;
    private String extTel;
    private String faxTel;
    private String phoneTel;
    private String email;
    private String address;
    private String engAddress;
    private Integer quantity;

    // BcdMaster Entity -> BcdMaster response Dto
    public static BcdDetailResponseDTO of(BcdDetail bcdDetail) {
        return BcdDetailResponseDTO.builder()
                .draftId(bcdDetail.getDraftId())
                .logId(bcdDetail.getSeqId())
                .userId(bcdDetail.getUserId())
                .division(bcdDetail.getDivision())
                .korNm(bcdDetail.getKorNm())
                .engNm(bcdDetail.getEngNm())
                .center(bcdDetail.getInstNm())
                .dep(bcdDetail.getDeptNm())
                .team(bcdDetail.getTeamNm())
                .engTeam(bcdDetail.getEngTeamNm())
                .grade(bcdDetail.getGrade())
                .engGrade(bcdDetail.getEngGrade())
                .extTel(bcdDetail.getExtTel())
                .faxTel(bcdDetail.getFaxTel())
                .phoneTel(bcdDetail.getPhoneTel())
                .email(bcdDetail.getEmail())
                .address(bcdDetail.getAddress())
                .engAddress(bcdDetail.getEngAddress())
                .quantity(bcdDetail.getQuantity())
                .build();
    }
}
