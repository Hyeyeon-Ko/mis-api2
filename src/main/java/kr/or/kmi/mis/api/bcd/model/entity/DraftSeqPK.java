package kr.or.kmi.mis.api.bcd.model.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class DraftSeqPK implements Serializable {

    private String draftId;
    private Long seqId;
}