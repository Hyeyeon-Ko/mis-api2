package kr.or.kmi.mis.api.toner.service;

import jakarta.servlet.http.HttpServletResponse;
import kr.or.kmi.mis.api.toner.model.request.TonerOrderRequestDTO;

import java.io.IOException;
import java.util.List;

public interface TonerExcelService {

    /* 토너 관리표 파일 첨부 */
//    void saveTonerDetails(TonerExcelRequestDTO tonerExcelRequestDTO);

    /* 토너 단가표 엑셀 파일 다운로드 */
    void downloadPriceExcel(HttpServletResponse response, List<String> tonerNms) throws IOException;

    /* 토너 단가표 엑셀 파일 생성 */
    byte[] generatePriceExcel(List<String> draftIds) throws IOException;

    /* 토너 관리표 엑셀 파일 다운로드 */
    void downloadManageExcel(HttpServletResponse response, List<String> mngNums) throws IOException;

    /* 토너 관리표 엑셀 파일 생성 */
    byte[] generateManageExcel(List<String> draftIds) throws IOException;

    /* 기안 상신용 엑셀 파일 다운로드 */
    void downloadPendingExcel(HttpServletResponse response, TonerOrderRequestDTO tonerOrderRequestDTO) throws IOException;

    /* 기안 상신용 엑셀 파일 생성 */
    byte[] generatePendingExcel(List<String> draftIds, String instCd) throws IOException;

    /* 발주용 엑셀 파일 다운로드 */
    void downloadOrderExcel(HttpServletResponse response, TonerOrderRequestDTO tonerOrderRequestDTO) throws IOException;

    /* 발주용 엑셀 파일 생성 */
    byte[] generateOrderExcel(List<String> draftIds, String instCd) throws IOException;

}
