package kr.or.kmi.mis.api.rental.service;

import kr.or.kmi.mis.api.rental.model.response.RentalResponseDTO;
import kr.or.kmi.mis.api.rental.model.response.RentalTotalListResponseDTO;

import java.util.List;

public interface RentalListService {

    /* 센터별 렌탈현황 내역 */
    List<RentalResponseDTO> getCenterRentalList(String instCd);

    /* 전국센터 렌탈현황 내역 */
    RentalTotalListResponseDTO getTotalRentalList();
}
