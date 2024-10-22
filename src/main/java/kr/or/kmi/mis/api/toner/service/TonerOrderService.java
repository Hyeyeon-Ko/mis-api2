package kr.or.kmi.mis.api.toner.service;

import jakarta.mail.MessagingException;
import kr.or.kmi.mis.api.order.model.request.OrderRequestDTO;
import kr.or.kmi.mis.api.order.model.response.EmailSettingsResponseDTO;
import kr.or.kmi.mis.api.toner.model.response.TonerOrderResponseDTO;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface TonerOrderService {

    /* 발주 목록 불러오기 */
    List<TonerOrderResponseDTO> getTonerOrderList(String instCd);

    /* 발주 요청 -> 이메일로 엑셀 파일 전송 */
    void orderToner(OrderRequestDTO orderRequestDTO) throws IOException, MessagingException, GeneralSecurityException;

    /* 기본 수신 이메일 설정 */
    EmailSettingsResponseDTO getEmailSettings();
}
