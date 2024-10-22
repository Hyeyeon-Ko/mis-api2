package kr.or.kmi.mis.api.toner.model.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class TonerDetailId implements Serializable {

    private String draftId;
    private Long itemId;

    public TonerDetailId() {}

    public TonerDetailId(String draftId, Long itemId) {
        this.draftId = draftId;
        this.itemId = itemId;
    }
}
