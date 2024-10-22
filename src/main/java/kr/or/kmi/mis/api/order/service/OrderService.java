package kr.or.kmi.mis.api.order.service;

import jakarta.mail.MessagingException;
import kr.or.kmi.mis.api.order.model.request.OrderRequestDTO;
import kr.or.kmi.mis.api.order.model.response.EmailSettingsResponseDTO;
import kr.or.kmi.mis.api.order.model.response.OrderListResponseDTO;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface OrderService {

    /* 승인 완료된 목록 불러오기 */
    List<OrderListResponseDTO> getOrderList(String instCd);
//    Page<OrderListResponseDTO> getOrderList2(String instCd, Pageable page);

    /* 발주 요청 -> 이메일로 엑셀 파일 전송 */
    void orderRequest(OrderRequestDTO orderRequest) throws IOException, MessagingException, GeneralSecurityException;

    /* 기본 수신 이메일 설정 */
    EmailSettingsResponseDTO getEmailSettings();
}
