package kr.or.kmi.mis.api.order.model.request;

import lombok.Data;

import java.util.List;

@Data
public class ExcelRequestDTO {

    private String instCd;
    private List<String> selectedApplications;
}
