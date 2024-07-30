package kr.or.kmi.mis.api.order.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.util.List;

public interface ExcelService {

    /* 발주 요청내역 엑셀 파일 다운로드 */
    void downloadExcel(HttpServletResponse response, List<Long> draftIds) throws IOException;

    /* 발주 완료내역 엑셀 파일 다운로드*/
    void downloadOrderExcel(HttpServletResponse response, @RequestBody List<Long> draftIds) throws IOException;

    /* 발주 요청내역 엑셀 파일 생성 */
    byte[] generateExcel(List<Long> draftIds) throws IOException;

    /* 발주 완료내역 엑셀 파일 생성*/
    byte[] generateOrderExcel(List<Long> draftIds) throws IOException;
}
