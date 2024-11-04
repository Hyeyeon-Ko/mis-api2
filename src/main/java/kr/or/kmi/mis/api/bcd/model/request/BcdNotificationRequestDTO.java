package kr.or.kmi.mis.api.bcd.model.request;

import lombok.Data;

import java.util.List;

@Data
public class BcdNotificationRequestDTO {
    private List<String> draftIds;
}
