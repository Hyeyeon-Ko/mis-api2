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
    String korNm;       // 대상자 이름
    String engNm;
    String instCd;
    String deptCd;
    String teamCd;
    String teamNm;
    String gradeCd;
    String extTel;
    String phoneTel;
    String faxTel;
    String email;
    String address;
    String endAddress;
    String division;    // 명함구분
    Integer quantity;

    // BcdRequest Dto -> BcdMaster Entity
    public BcdMaster toMasterEntity() {
        return BcdMaster.builder()
                .drafter(drafter)
                .drafterId(drafterId)
                .teamNm(teamNm)
                .korNm(korNm)
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
                .gradeCd(gradeCd)
                .extTel(extTel)
                .faxTel(faxTel)
                .phoneTel(phoneTel)
                .email(email)
                .address(address)
                .engAddress(endAddress)
                .quantity(quantity)
                .build();
    }
}