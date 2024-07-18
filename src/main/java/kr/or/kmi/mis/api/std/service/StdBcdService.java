package kr.or.kmi.mis.api.std.service;

import kr.or.kmi.mis.api.std.model.response.StdBcdResponseDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface StdBcdService {

    StdBcdResponseDTO getAllBcdStd();
    List<String> getDetailNames(String detailCd);
}
