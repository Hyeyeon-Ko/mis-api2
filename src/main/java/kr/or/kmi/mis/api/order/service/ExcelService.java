package kr.or.kmi.mis.api.order.service;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public interface ExcelService {

    /* 엑셀 파일 다운로드 */
    void downloadExcel(HttpServletResponse response, List<Long> draftIds) throws IOException;

    /* 엑셀 파일 생성 */
    byte[] generateExcel(List<Long> draftIds) throws IOException;
}
