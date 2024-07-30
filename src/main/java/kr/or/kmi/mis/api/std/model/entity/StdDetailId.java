package kr.or.kmi.mis.api.std.model.entity;

import lombok.*;

import java.io.Serializable;

@Data
public class StdDetailId implements Serializable {
    private String groupCd;
    private String detailCd;
}
