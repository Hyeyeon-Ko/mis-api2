package kr.or.kmi.mis.api.docstorage.service;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public interface DocstorageExcelService {

    /* 문서보관 내역 엑셀 파일 다운로드 */
    void downloadExcel(HttpServletResponse response, List<Long> detailIds) throws IOException;

    /* 문서보관 내역 엑셀 파일 생성 */
    byte[] generateExcel(List<Long> detailIds) throws IOException;
}
