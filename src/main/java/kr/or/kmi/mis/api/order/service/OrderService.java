package kr.or.kmi.mis.api.order.service;

import jakarta.mail.MessagingException;
import kr.or.kmi.mis.api.order.model.request.OrderRequestDTO;
import kr.or.kmi.mis.api.order.model.response.EmailSettingsResponseDTO;
import kr.or.kmi.mis.api.order.model.response.OrderListResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface OrderService {

    /* 승인 완료된 목록 불러오기 */
    List<OrderListResponseDTO> getOrderList(String instCd);

    /* 발주 요청 -> 이메일로 엑셀 파일 전송 */
    void orderRequest(OrderRequestDTO orderRequest, MultipartFile file) throws IOException, MessagingException, GeneralSecurityException;

    /* 엑셀 파일 미리보기 생성 */
    byte[] previewOrderFile(List<String> draftIds) throws IOException;

    /* 기본 수신 이메일 설정 */
    EmailSettingsResponseDTO getEmailSettings();
}
