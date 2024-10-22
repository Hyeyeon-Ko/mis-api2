package kr.or.kmi.mis.api.toner.service;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public interface TonerExcelService {

    /* 토너 관리표 파일 첨부 */
//    void saveTonerDetails(TonerExcelRequestDTO tonerExcelRequestDTO);

    /* 기안 상신용 엑셀 파일 다운로드 */
    void downloadPendingExcel(HttpServletResponse response, List<String> draftIds) throws IOException;

//    /* 기안 상신용 엑셀 파일 생성 */
//    byte[] generatePendingExcel(List<String> draftIds) throws IOException;

    /* 발주용 엑셀 파일 다운로드 */
    void downloadOrderExcel(HttpServletResponse response, List<String> draftIds);


    /* 발주용 엑셀 파일 생성 */
//    byte[] generateOrderExcel(List<String> draftIds) throws IOException;

}
