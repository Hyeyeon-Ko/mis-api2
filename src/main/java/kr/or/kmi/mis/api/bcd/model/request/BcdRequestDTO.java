package kr.or.kmi.mis.api.bcd.model.request;

import kr.or.kmi.mis.api.bcd.model.entity.BcdDetail;
import kr.or.kmi.mis.api.bcd.model.entity.BcdMaster;
import lombok.Getter;

import java.util.List;

@Getter
public class BcdRequestDTO {

    String drafter;     // 기안자
    String drafterId;   // 기안자 사번
    String userId;      // 대상자 사번
    String korNm;       // 대상자 이름
    String engNm;
    String instCd;
    String deptCd;
    String teamCd;
    String teamNm;
    String engTeamNm;
    String gradeCd;
    String gradeNm;
    String enGradeNm;
    String extTel;
    String phoneTel;
    String faxTel;
    String email;
    String address;
    String engAddress;
    String division;    // 명함구분
    Integer quantity;

    List<String> approverIds;
    Integer currentApproverIndex;

    // BcdRequest Dto -> BcdMaster Entity
    public BcdMaster toMasterEntity(String status) {
        String approverChain = String.join(", ", approverIds);

        return BcdMaster.builder()
                .drafter(drafter)
                .drafterId(drafterId)
                .teamNm(teamNm)
                .korNm(korNm)
                .approverChain(approverChain)
                .status(status)
                .build();
    }

    // BcdRequest Dto -> BcdDetail Entity
    public BcdDetail toDetailEntity(Long draftId) {
        return BcdDetail.builder()
                .draftId(draftId)
                .division(division)
                .userId(userId)
                .korNm(korNm)
                .engNm(engNm)
                .instCd(instCd)
                .deptCd(deptCd)
                .teamCd(teamCd)
                .teamNm(teamNm)
                .engteamNm(engTeamNm)
                .gradeCd(gradeCd)
                .gradeNm(gradeNm)
                .enGradeNm(enGradeNm)
                .extTel(extTel)
                .faxTel(faxTel)
                .phoneTel(phoneTel)
                .email(email)
                .address(address)
                .engAddress(engAddress)
                .quantity(quantity)
                .build();
    }
}