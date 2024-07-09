package kr.or.kmi.mis.api.order.model.request;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class OrderRequestDTO {

    private List<Long> draftIds;
}
