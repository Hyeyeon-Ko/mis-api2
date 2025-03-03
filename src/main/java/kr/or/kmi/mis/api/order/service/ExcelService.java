package kr.or.kmi.mis.api.order.service;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface ExcelService {

    /* 발주 요청내역 엑셀 파일 다운로드 */
    void downloadExcel(HttpServletResponse response, List<String> draftIds) throws IOException;

    /* 발주 완료내역 엑셀 파일 다운로드*/
    void downloadOrderExcel(HttpServletResponse response, List<String> draftIds, String instCd) throws IOException;

    /* 발주 요청내역 엑셀 파일 생성 */
    byte[] generateExcel(List<String> draftIds) throws IOException;

    /* 발주 완료내역 엑셀 파일 생성*/
    byte[] generateOrderExcel(List<String> draftIds, String instNm) throws IOException;

    /* 엑셀 파일 암호화 */
    byte[] getEncryptedExcelBytes(byte[] excelData, String password) throws IOException, GeneralSecurityException;
}
