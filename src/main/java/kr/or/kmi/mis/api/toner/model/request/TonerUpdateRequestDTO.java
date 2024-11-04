package kr.or.kmi.mis.api.toner.model.request;

import lombok.Getter;

import java.util.List;

@Getter
public class TonerUpdateRequestDTO {

    String drafter;
    String drafterId;
    List<TonerDetailDTO> tonerDetailDTOs;

}
