package kr.or.kmi.mis.api.toner.service;

import kr.or.kmi.mis.api.toner.model.response.TonerExcelResponseDTO;
import kr.or.kmi.mis.api.toner.model.response.TonerTotalListResponseDTO;

import java.util.List;

public interface TonerListService {

    /* 전체 신청내역*/

    /* 승인대기 내역*/

    /* 토너 발주내역 */

    /* 토너 단가표 정보 */

    /* 센터별 토너 관리표 정보 */
    List<TonerExcelResponseDTO> getTonerList(String instCd);

    /* 전국 토너 관리표 정보 */
    TonerTotalListResponseDTO getTotalTonerList();
}
