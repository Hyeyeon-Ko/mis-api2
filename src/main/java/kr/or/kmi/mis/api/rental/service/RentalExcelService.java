package kr.or.kmi.mis.api.rental.service;

import jakarta.servlet.http.HttpServletResponse;
import kr.or.kmi.mis.api.rental.model.response.RentalExcelResponseDTO;

import java.io.IOException;
import java.util.List;

public interface RentalExcelService {

    /* 렌탈현황 내역 엑셀 파일 다운로드 */
    void downloadExcel(HttpServletResponse response, List<Long> detailIds) throws IOException;

    /* 렌탈현황 내역 엑셀 파일 생성 */
    byte[] generateExcel(List<Long> detailIds) throws IOException;

    /* 렌탈현황 내역 저장 */
    void saveRentalDetails(List<RentalExcelResponseDTO> details);

    /* 렌탈현황 내역 수정 */
    void updateRentalDetails(List<RentalExcelResponseDTO> details);
}
