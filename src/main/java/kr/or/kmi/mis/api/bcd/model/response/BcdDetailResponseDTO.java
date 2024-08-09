package kr.or.kmi.mis.api.bcd.model.response;

import kr.or.kmi.mis.api.bcd.model.entity.BcdDetail;
import lombok.*;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class BcdDetailResponseDTO {

    private Long draftId;
    private String drafter;
    private String userId;
    private String division;
    private String korNm;
    private String engNm;
    private String instCd;
    private String instNm;
    private String deptCd;
    private String deptNm;
    private String teamCd;
    private String teamNm;
    private String gradeCd;
    private String gradeNm;
    private String addGradeNm;
    private String enGradeNm;
    private String extTel;
    private String faxTel;
    private String phoneTel;
    private String email;
    private String address;
    private String engAddress;
    private Integer quantity;

    // BcdMaster Entity -> BcdMaster response Dto
    public static BcdDetailResponseDTO of(BcdDetail bcdDetail, String drafter, List<String> names) {
        return BcdDetailResponseDTO.builder()
                .draftId(bcdDetail.getDraftId())
                .drafter(drafter)
                .division(bcdDetail.getDivision())
                .userId(bcdDetail.getUserId())
                .korNm(bcdDetail.getKorNm())
                .engNm(bcdDetail.getEngNm())
                .instCd(bcdDetail.getInstCd())
                .deptCd(bcdDetail.getDeptCd())
                .teamCd(bcdDetail.getTeamCd())
                .gradeCd(bcdDetail.getGradeCd())
                .extTel(bcdDetail.getExtTel())
                .faxTel(bcdDetail.getFaxTel())
                .phoneTel(bcdDetail.getPhoneTel())
                .email(bcdDetail.getEmail())
                .address(bcdDetail.getAddress())
                .engAddress(bcdDetail.getEngAddress())
                .quantity(bcdDetail.getQuantity())
                .instNm(names.get(0))
                .deptNm(names.get(1))
                .teamNm(names.get(2))
                .gradeNm(names.get(3))
                .addGradeNm(bcdDetail.getGradeNm())
                .enGradeNm(bcdDetail.getEngradeNm())
                .build();
    }
}
