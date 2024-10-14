package kr.or.kmi.mis.api.std.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StdDetailId implements Serializable {
    private String groupCd;
    private String detailCd;
}
