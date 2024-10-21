package kr.or.kmi.mis.api.toner.service;

import kr.or.kmi.mis.api.toner.model.request.TonerAddRequestDTO;
import kr.or.kmi.mis.api.toner.model.response.TonerExcelResponseDTO;
import kr.or.kmi.mis.api.toner.model.response.TonerTotalListResponseDTO;

import java.util.List;

public interface TonerManageService {

    /* 전체 신청내역*/

    /* 승인대기 내역*/

    /* 토너 발주내역 */

    /* 센터별 토너 관리표 정보 */
    List<TonerExcelResponseDTO> getTonerList(String instCd);

    /* 전국 토너 관리표 정보 */
    TonerTotalListResponseDTO getTotalTonerList();

    /* 토너 정보 추가 */
    void addToner(TonerAddRequestDTO tonerAddRequestDTO);
}
