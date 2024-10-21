package kr.or.kmi.mis.api.toner.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
public class TonerDetailId implements Serializable {

    private String draftId;
    private Long itemId;
}
