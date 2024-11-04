package kr.or.kmi.mis.api.toner.model.request;

import kr.or.kmi.mis.api.apply.model.request.ConfirmRequestDTO;
import lombok.Data;

import java.util.List;

@Data
public class TonerConfirmRequestDTO {

    private List<String> draftIds;
    private ConfirmRequestDTO confirmRequestDTO;

}
