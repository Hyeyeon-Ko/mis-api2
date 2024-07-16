package kr.or.kmi.mis.api.std.model.entity;

import lombok.*;

import java.io.Serializable;

@Data
public class StdId implements Serializable {
    private String clsCd;
    private String etcCd;
    private String etcDetlCd;
}
