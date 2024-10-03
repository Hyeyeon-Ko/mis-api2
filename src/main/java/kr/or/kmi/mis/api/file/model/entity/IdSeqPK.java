package kr.or.kmi.mis.api.file.model.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class IdSeqPK implements Serializable {

    private String attachId;
    private Long seqId;
}
