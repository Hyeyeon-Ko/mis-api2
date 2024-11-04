package kr.or.kmi.mis.api.toner.model.request;

import lombok.Data;

import java.util.List;

@Data
public class TonerOrderRequestDTO {

    private List<String> draftIds;
    private String instCd;
}
