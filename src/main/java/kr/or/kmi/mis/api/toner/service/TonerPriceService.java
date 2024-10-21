package kr.or.kmi.mis.api.toner.service;

import kr.or.kmi.mis.api.toner.model.request.TonerPriceRequestDTO;
import kr.or.kmi.mis.api.toner.model.response.TonerPriceResponseDTO;

import java.util.List;

public interface TonerPriceService {

    /* 토너 단가표 정보 */
    List<TonerPriceResponseDTO> getTonerPriceList();

    /* 토너 단가 정보 조회 */
    TonerPriceResponseDTO getTonerPriceInfo(String tonerNm);

    /* 토너 단가 정보 추가 */
    void addTonerPriceInfo(TonerPriceRequestDTO tonerPriceRequestDTO, String userId);

    /* 토너 단가 정보 수정 */
    void updateTonerPriceInfo(String tonerNm, TonerPriceRequestDTO tonerPriceRequestDTO, String userId);

    /* 토너 단가 정보 삭제 */
    void deleteTonerPriceInfo(String tonerNm);

}
