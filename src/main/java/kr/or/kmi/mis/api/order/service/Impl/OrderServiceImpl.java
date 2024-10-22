package kr.or.kmi.mis.api.order.service.Impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import kr.or.kmi.mis.api.bcd.model.entity.BcdDetail;
import kr.or.kmi.mis.api.bcd.model.entity.BcdMaster;
import kr.or.kmi.mis.api.bcd.repository.BcdDetailRepository;
import kr.or.kmi.mis.api.bcd.repository.BcdMasterRepository;
import kr.or.kmi.mis.api.exception.EntityNotFoundException;
import kr.or.kmi.mis.api.noti.service.NotificationSendService;
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
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
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
    private final StdBcdService stdBcdService;
    private final NotificationSendService notificationSendService;

//    private final OrderQueryRepository orderQueryRepository;

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

//    @Override
//    public Page<OrderListResponseDTO> getOrderList2(String instCd, Pageable page) {
//        return orderQueryRepository.getOrderList2(instCd, page);
//    }

    @Override
    @Transactional
    public void orderRequest(OrderRequestDTO orderRequest) throws IOException, MessagingException, GeneralSecurityException {
        // 엑셀 데이터 생성
        byte[] excelData = excelService.generateExcel(orderRequest.getDraftIds());

        // 엑셀 파일 암호화
        byte[] encryptedExcelData = excelService.getEncryptedExcelBytes(excelData, "06960");

        // 첨부 파일과 함께 이메일 전송 (동적 SMTP 설정 사용)
        sendEmailWithDynamicCredentials(
                "smtp.sirteam.net",
                465,
                orderRequest.getFromEmail(),
                orderRequest.getPassword(),
                orderRequest.getFromEmail(),
                orderRequest.getToEmail(),
                encryptedExcelData,
                orderRequest.getEmailSubject(),
                orderRequest.getEmailBody(),
                orderRequest.getFileName()
        );

        SimpleDateFormat simpleDataFormat = new SimpleDateFormat("yyyy.MM.dd");
        SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

        // 발주일시 업데이트
        orderRequest.getDraftIds().forEach(draftId -> {
            BcdMaster bcdMaster = bcdMasterRepository.findById(draftId)
                    .orElseThrow(() -> new EntityNotFoundException("Draft not found with id " + draftId));
            bcdMaster.updateOrder(LocalDateTime.now());
            bcdMasterRepository.save(bcdMaster);

            // 알림 전송
            BcdDetail bcdDetail = bcdDetailRepository.findById(draftId)
                    .orElseThrow(() -> new EntityNotFoundException("BcdDetail not found with id " + draftId));
            notificationSendService.sendBcdOrder(bcdMaster.getDraftDate(), bcdDetail.getUserId());

        });
    }

    @Override
    @Transactional(readOnly = true)
    public EmailSettingsResponseDTO getEmailSettings() {
        StdGroup stdGroup = stdGroupRepository.findByGroupCd("B003")
                .orElseThrow(() -> new EntityNotFoundException("B003"));
        StdDetail stdDetail2 = stdDetailRepository.findByGroupCdAndDetailCd(stdGroup, "001")
                .orElseThrow(() -> new EntityNotFoundException("001"));

        return new EmailSettingsResponseDTO(stdDetail2.getEtcItem2());
    }

    private void sendEmailWithDynamicCredentials(String smtpHost, int smtpPort, String username, String password, String fromEmail, String toEmail, byte[] excelData, String subject, String body, String fileName) throws MessagingException {

        JavaMailSenderImpl mailSenderImpl = new JavaMailSenderImpl();
        mailSenderImpl.setHost(smtpHost);
        mailSenderImpl.setPort(smtpPort);
        mailSenderImpl.setUsername(username); // 사용자가 입력한 이메일 ID
        mailSenderImpl.setPassword(password); // 사용자가 입력한 이메일 비밀번호

        Properties props = mailSenderImpl.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");
        props.put("mail.smtp.ssl.trust", smtpHost);
        props.put("mail.smtp.ssl.enable", "true");

        MimeMessage message = mailSenderImpl.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        // 이메일 설정
        helper.setFrom(fromEmail);
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(body);

        // 엑셀 파일 첨부
        String fileFullName = fileName + ".xlsx";
        helper.addAttachment(fileFullName, new ByteArrayResource(excelData));

        // 이메일 전송
        mailSenderImpl.send(message);
    }
}
