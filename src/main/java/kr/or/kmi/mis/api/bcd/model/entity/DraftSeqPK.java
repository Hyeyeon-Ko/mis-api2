package kr.or.kmi.mis.api.bcd.model.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class DraftSeqPK implements Serializable {

    private Long draftId;
    private Long seqId;

}