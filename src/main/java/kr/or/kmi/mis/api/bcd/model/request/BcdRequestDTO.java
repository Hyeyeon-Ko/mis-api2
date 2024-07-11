package kr.or.kmi.mis.api.bcd.model.request;

import kr.or.kmi.mis.api.bcd.model.entity.BcdDetail;
import kr.or.kmi.mis.api.bcd.model.entity.BcdMaster;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class BcdRequestDTO {

    String drafter;     // 기안자
    String drafterId;   // 기안자 사번
    String userId;      // 대상자 사번
    String division;    // 명함구분
    String korNm;
    String engNm;
    String instNm;
    String deptNm;
    String teamNm;
    String grade;
    String extTel;
    String phoneTel;
    String faxTel;
    String email;
    String address;
    Integer quantity;

    // BcdRequest Dto -> BcdMaster Entity
    public BcdMaster toMasterEntity() {
        return BcdMaster.builder()
                .drafterId(drafterId)
                .drafter(drafter)
                .teamNm(teamNm)
                .korNm(korNm)
                .build();
    }

    // BcdRequest Dto -> BcdDetail Entity
    public BcdDetail toDetailEntity(Long draftId, Timestamp draftDate) {
        return BcdDetail.builder()
                .draftId(draftId)
                .draftDate(draftDate)
                .drafterId(drafterId)
                .drafter(drafter)
                .userId(userId)
                .division(division)
                .korNm(korNm)
                .engNm(engNm)
                .instNm(instNm)
                .deptNm(deptNm)
                .teamNm(teamNm)
                .grade(grade)
                .extTel(extTel)
                .faxTel(faxTel)
                .phoneTel(phoneTel)
                .email(email)
                .address(address)
                .quantity(quantity)
                .build();
    }
}