package kr.or.kmi.mis.api.toner.service;

import kr.or.kmi.mis.api.toner.model.response.TonerPendingResponseDTO;

import java.util.List;

public interface TonerPendingService {

    /* 승인 대기중인 목록 불러오기 */
    List<TonerPendingResponseDTO> getTonerPendingList(String instCd);

}
