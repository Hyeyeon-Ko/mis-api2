package kr.or.kmi.mis.api.order.service.Impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import kr.or.kmi.mis.api.bcd.model.entity.BcdDetail;
import kr.or.kmi.mis.api.bcd.model.entity.BcdMaster;
import kr.or.kmi.mis.api.bcd.repository.BcdDetailRepository;
import kr.or.kmi.mis.api.bcd.repository.BcdMasterRepository;
import kr.or.kmi.mis.api.exception.EntityNotFoundException;
import kr.or.kmi.mis.api.order.model.request.OrderRequestDTO;
import kr.or.kmi.mis.api.order.model.response.EmailSettingsResponseDTO;
import kr.or.kmi.mis.api.order.model.response.OrderListResponseDTO;
import kr.or.kmi.mis.api.order.service.ExcelService;
import kr.or.kmi.mis.api.order.service.OrderService;
import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import kr.or.kmi.mis.api.std.repository.StdDetailRepository;
import kr.or.kmi.mis.api.std.repository.StdGroupRepository;
import kr.or.kmi.mis.api.std.service.StdBcdService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final BcdMasterRepository bcdMasterRepository;
    private final BcdDetailRepository bcdDetailRepository;
    private final StdGroupRepository stdGroupRepository;
    private final StdDetailRepository stdDetailRepository;
    private final ExcelService excelService;
    private final JavaMailSender mailSender;
    private final StdBcdService stdBcdService;

    @Override
    @Transactional(readOnly = true)
    public List<OrderListResponseDTO> getOrderList(String instCd) {

        List<BcdMaster> bcdMasterList = bcdMasterRepository.findAllByStatusAndOrderDateIsNull("B")
                .orElseThrow(() -> new EntityNotFoundException("BcdMaster"));

        return bcdMasterList.stream()
                .map(bcdMaster -> {
                    BcdDetail bcdDetail = bcdDetailRepository.findById(bcdMaster.getDraftId())
                            .orElseThrow(() -> new EntityNotFoundException("BcdDetail"));

                    if (bcdDetail.getInstCd().equals(instCd)) {
                        Integer quantity = bcdDetail.getQuantity();

                        return OrderListResponseDTO.builder()
                                .draftId(bcdDetail.getDraftId())
                                .instNm(stdBcdService.getInstNm(bcdDetail.getInstCd()))
                                .title(bcdMaster.getTitle())
                                .draftDate(bcdMaster.getDraftDate())
                                .respondDate(bcdMaster.getRespondDate())
                                .drafter(bcdMaster.getDrafter())
                                .quantity(quantity)
                                .build();
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void orderRequest(OrderRequestDTO orderRequest) throws IOException, MessagingException, GeneralSecurityException {

        // 엑셀 데이터 생성
        byte[] excelData = excelService.generateExcel(orderRequest.getDraftIds());

        // 엑셀 파일 암호화
        byte[] encryptedExcelData = excelService.getEncryptedExcelBytes(excelData, "password@!");

        // 첨부 파일과 함께 이메일 전송
        sendEmailWithAttachment(orderRequest.getFromEmail(), orderRequest.getToEmail(), encryptedExcelData, orderRequest.getEmailSubject(), orderRequest.getEmailBody(), orderRequest.getFileName());

        // 발주일시 업데이트
        orderRequest.getDraftIds().forEach(draftId -> {
            BcdMaster bcdMaster = bcdMasterRepository.findById(draftId)
                    .orElseThrow(() -> new EntityNotFoundException("Draft not found with id " + draftId));
            bcdMaster.updateOrder(new Timestamp(System.currentTimeMillis()));
            bcdMasterRepository.save(bcdMaster);
        });
    }

    @Override
    public EmailSettingsResponseDTO getEmailSettings() {
        StdGroup stdGroup = stdGroupRepository.findByGroupCd("B003")
                .orElseThrow(() -> new EntityNotFoundException("B003"));
        StdDetail stdDetail1 = stdDetailRepository.findByGroupCdAndDetailCd(stdGroup, "003")
                .orElseThrow(() -> new EntityNotFoundException("003"));
        StdDetail stdDetail2 = stdDetailRepository.findByGroupCdAndDetailCd(stdGroup, "002")
                .orElseThrow(() -> new EntityNotFoundException("002"));

        return new EmailSettingsResponseDTO(stdDetail1.getEtcItem2(), stdDetail2.getEtcItem2());
    }

    private void sendEmailWithAttachment(String fromEmail, String toEmail, byte[] excelData, String subject, String body, String fileName) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        // 이메일 설정
        helper.setFrom(fromEmail); // 발신자 이메일 주소
        helper.setTo(toEmail); // 수신자 이메일 주소
        helper.setSubject(subject); // 이메일 제목
        helper.setText(body); // 이메일 내용

        // 엑셀 파일 첨부
        String fileFullName = fileName + ".xlsx";
        helper.addAttachment(fileFullName, new ByteArrayResource(excelData));

        // 이메일 전송
        mailSender.send(message);
    }
}
