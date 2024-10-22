package kr.or.kmi.mis.api.toner.service;

import jakarta.servlet.http.HttpServletResponse;
import kr.or.kmi.mis.api.toner.model.response.TonerPendingResponseDTO;

import java.io.IOException;
import java.util.List;

public interface TonerPendingService {

    /* 승인 대기중인 목록 불러오기 */
    List<TonerPendingResponseDTO> getTonerPendingList(String instCd);

    /* 기안 상신용 엑셀 파일 다운로드 */
    void downloadExcel(HttpServletResponse response, List<String> draftIds) throws IOException;

//    /* 기안 상신용 엑셀 파일 생성 */
//    byte[] generateExcel(List<String> draftIds) throws IOException;
}
