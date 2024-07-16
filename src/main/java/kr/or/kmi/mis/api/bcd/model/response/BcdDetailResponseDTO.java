package kr.or.kmi.mis.api.bcd.model.response;

import kr.or.kmi.mis.api.bcd.model.entity.BcdDetail;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
public class BcdDetailResponseDTO {

    private Long draftId;
    private String userId;
    private String division;
    private String korNm;
    private String engNm;
    private String instCd;
    private String deptCd;
    private String teamCd;
    private String gradeCd;
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
                .build();
    }
}
