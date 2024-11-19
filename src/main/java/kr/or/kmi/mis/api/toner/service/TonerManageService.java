package kr.or.kmi.mis.api.toner.service;

import kr.or.kmi.mis.api.toner.model.request.TonerInfoRequestDTO;
import kr.or.kmi.mis.api.toner.model.response.TonerExcelResponseDTO;
import kr.or.kmi.mis.api.toner.model.response.TonerInfoResponseDTO;
import kr.or.kmi.mis.api.toner.model.response.TonerTotalListResponseDTO;

import java.util.List;

public interface TonerManageService {

    /* 센터별 토너 관리표 정보 */
    List<TonerExcelResponseDTO> getCenterTonerList(String instCd);

    /* 전국 토너 관리표 정보 */
    TonerTotalListResponseDTO getTotalTonerList();

    /* 토너 정보 조회 */
    TonerInfoResponseDTO getTonerInfo(String mngNum);

    /* 토너 정보 추가 */
    void addTonerInfo(TonerInfoRequestDTO tonerInfoRequestDTO, String userId, String instCd);

    /* 토너 정보 수정 */
    void updateTonerInfo(String mngNum, TonerInfoRequestDTO tonerInfoRequestDTO, String userId);

    /* 토너 정보 삭제 */
    void deleteTonerInfo(String mngNum);

}
