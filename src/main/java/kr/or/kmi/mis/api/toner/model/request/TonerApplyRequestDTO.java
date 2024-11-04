package kr.or.kmi.mis.api.toner.model.request;

import kr.or.kmi.mis.api.toner.model.entity.TonerMaster;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class TonerApplyRequestDTO {

    String drafter;
    String drafterId;
    String instCd;
    List<TonerDetailDTO> tonerDetailDTOs;

    public TonerMaster toMasterEntity(String draftId) {
        return TonerMaster.builder()
                .draftId(draftId)
                .draftDate(LocalDateTime.now())
                .drafter(drafter)
                .drafterId(drafterId)
                .status("A")
                .instCd(instCd)
                .build();
    }
}
