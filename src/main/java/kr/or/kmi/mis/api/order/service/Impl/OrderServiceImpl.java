package kr.or.kmi.mis.api.order.service.Impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import kr.or.kmi.mis.api.bcd.model.entity.BcdDetail;
import kr.or.kmi.mis.api.bcd.model.entity.BcdMaster;
import kr.or.kmi.mis.api.bcd.repository.BcdDetailRepository;
import kr.or.kmi.mis.api.bcd.repository.BcdMasterRepository;
import kr.or.kmi.mis.api.exception.EntityNotFoundException;
import kr.or.kmi.mis.api.order.model.response.OrderListResponseDTO;
import kr.or.kmi.mis.api.order.service.ExcelService;
import kr.or.kmi.mis.api.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final BcdMasterRepository bcdMasterRepository;
    private final BcdDetailRepository bcdDetailRepository;
    private final ExcelService excelService;
    private final JavaMailSender mailSender;

    @Override
    @Transactional(readOnly = true)
    public List<OrderListResponseDTO> getOrderList() {

        // 1. 승인상태인 신청건 리스트 불러오기
        List<BcdMaster> bcdMasterList = bcdMasterRepository.findAllByStatus("B")
                .orElseThrow(() -> new EntityNotFoundException("BcdMaster"));

        // 2. 각 신청건의 발주일시 저장 및 상세 정보 불러오기
        return bcdMasterList.stream()
                .map(bcdMaster -> {
                    // 발주일시 업데이트
                    bcdMaster.updateOrderDate(new Timestamp(System.currentTimeMillis()));

                    // 상세 정보 불러오기
                    BcdDetail bcdDetail = bcdDetailRepository.findByDraftId(bcdMaster.getDraftId())
                            .orElseThrow(() -> new EntityNotFoundException("BcdDetail"));

                    // 수량 가져오기
                    Integer quantity = bcdDetail.getQuantity();

                    // OrderListResponseDTO 생성 및 반환
                    return OrderListResponseDTO.builder()
                            .draftId(bcdDetail.getDraftId())
                            .instNm(bcdDetail.getInstNm())
                            .title(bcdMaster.getTitle())
                            .draftDate(bcdMaster.getDraftDate())
                            .respondDate(bcdMaster.getRespondDate())
                            .drafter(bcdMaster.getDrafter())
                            .quantity(quantity)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public void orderRequest(List<Long> draftIds) throws IOException, MessagingException {
        // 엑셀 데이터 생성
        byte[] excelData = excelService.generateExcel(draftIds);
        // 첨부 파일과 함께 이메일 전송
        sendEmailWithAttachment(excelData);
    }

    private void sendEmailWithAttachment(byte[] excelData) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        // 이메일 설정
        helper.setFrom("gdkimm@kmi.or.kr"); // 발신자 이메일 주소
        helper.setTo("khy33355@naver.com"); // 수신자 이메일 주소
        helper.setSubject("발주 요청 엑셀 파일"); // 이메일 제목
        helper.setText("발주 요청 상세정보가 포함된 엑셀 파일을 첨부합니다."); // 이메일 내용

        // 엑셀 파일 첨부
        helper.addAttachment("order_details.xlsx", new ByteArrayResource(excelData));

        // 이메일 전송
        mailSender.send(message);
    }
}
