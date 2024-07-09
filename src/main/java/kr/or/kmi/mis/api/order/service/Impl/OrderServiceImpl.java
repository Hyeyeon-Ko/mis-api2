package kr.or.kmi.mis.api.order.service.Impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
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
        List<BcdMaster> bcdMasterList = bcdMasterRepository.findAllByStatus("B");

        // 2. 각 신청건의 상세 정보 불러오기 using Streams
        return bcdMasterList.stream()
                .map(bcdMaster -> {
                    Integer quantity = bcdDetailRepository.findQuantityByDraftId(bcdMaster.getDraftId())
                            .orElseThrow(() -> new EntityNotFoundException("Quantity not found for draft ID: " + bcdMaster.getDraftId()));
                    return OrderListResponseDTO.builder()
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
        byte[] excelData = excelService.generateExcel(draftIds);
        sendEmailWithAttachment(excelData);
    }

    private void sendEmailWithAttachment(byte[] excelData) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        // 예시 이메일, 제목, 내용
        helper.setTo("khy33355@gmail.com"); // 수신자 이메일 주소
        helper.setSubject("발주 요청 엑셀 파일");
        helper.setText("발주 요청 상세정보가 포함된 엑셀 파일을 첨부합니다.");

        helper.addAttachment("order_details.xlsx", new ByteArrayResource(excelData));

        mailSender.send(message);
    }
}
