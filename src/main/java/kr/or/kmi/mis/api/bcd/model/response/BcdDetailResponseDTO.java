package kr.or.kmi.mis.api.bcd.model.response;

import kr.or.kmi.mis.api.bcd.model.entity.BcdDetail;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
public class BcdDetailResponseDTO {

    private Long draftId;
    private Long seqId;
    private String drafter;
    private String userId;
    private String division;
    private String korNm;
    private String engNm;
    private String instNm;
    private String deptNm;
    private String teamNm;
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
                .seqId(bcdDetail.getSeqId())
                .drafter(bcdDetail.getDrafter())
                .userId(bcdDetail.getUserId())
                .division(bcdDetail.getDivision())
                .korNm(bcdDetail.getKorNm())
                .engNm(bcdDetail.getEngNm())
                .instNm(bcdDetail.getInstNm())
                .deptNm(bcdDetail.getDeptNm())
                .teamNm(bcdDetail.getTeamNm())
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
