package kr.or.kmi.mis.api.toner.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import kr.or.kmi.mis.api.exception.EntityNotFoundException;
import kr.or.kmi.mis.api.order.model.request.OrderRequestDTO;
import kr.or.kmi.mis.api.order.model.response.EmailSettingsResponseDTO;
import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import kr.or.kmi.mis.api.std.repository.StdDetailRepository;
import kr.or.kmi.mis.api.std.repository.StdGroupRepository;
import kr.or.kmi.mis.api.toner.model.entity.TonerDetail;
import kr.or.kmi.mis.api.toner.model.entity.TonerMaster;
import kr.or.kmi.mis.api.toner.model.response.TonerOrderResponseDTO;
import kr.or.kmi.mis.api.toner.repository.TonerDetailRepository;
import kr.or.kmi.mis.api.toner.repository.TonerMasterRepository;
import kr.or.kmi.mis.api.toner.service.TonerExcelService;
import kr.or.kmi.mis.api.toner.service.TonerOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TonerOrderServiceImpl implements TonerOrderService {

    private final TonerExcelService tonerExcelService;
    private final TonerMasterRepository tonerMasterRepository;
    private final TonerDetailRepository tonerDetailRepository;
    private final StdGroupRepository stdGroupRepository;
    private final StdDetailRepository stdDetailRepository;

    /**
     * 발주 대기 상태의 항목들을 TonerOrderResponseDTO 리스트로 반환.
     * @param instCd 센터 코드 (instCd)로 해당 센터의 TonerMaster 및 관련 TonerDetail 항목을 조회합니다.
     * @return TonerOrderResponseDTO의 리스트. 각 TonerMaster와 연결된 TonerDetail 항목을 기반으로 한 DTO 리스트.
     */
    @Override
    @Transactional(readOnly = true)
    public List<TonerOrderResponseDTO> getTonerOrderList(String instCd) {

        List<TonerMaster> tonerMasterList = tonerMasterRepository.findAllByStatus("B")
                .orElseThrow(() -> new EntityNotFoundException("TonerMaster"));

        return tonerMasterList.stream()
                .flatMap(tonerMaster -> {
                    List<TonerDetail> tonerDetails = tonerDetailRepository.findAllByDraftId(tonerMaster.getDraftId());
                    return tonerDetails.stream().map(tonerDetail -> TonerOrderResponseDTO.builder()
                            .tonerNm(tonerDetail.getTonerNm())
                            .quantity(tonerDetail.getQuantity())
                            .price(tonerDetail.getUnitPrice())
                            .totalPrice(tonerDetail.getTotalPrice())
                            .mngNum(tonerDetail.getMngNum())
                            .build());
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void orderToner(OrderRequestDTO orderRequestDTO) throws IOException, MessagingException, GeneralSecurityException {

//        // 1. 엑셀 데이터 생성
//        byte[] excelData = tonerExcelService.generateExcel(orderRequestDTO.getDraftIds());
//
//        // 2. 첨부 파일과 함께 이메일 전송 (동적 SMTP 설정 사용)
//        sendEmailWithDynamicCredentials(
//                "smtp.sirteam.net",
//                465,
//                orderRequestDTO.getFromEmail(),
//                orderRequestDTO.getPassword(),
//                orderRequestDTO.getFromEmail(),
//                orderRequestDTO.getToEmail(),
//                excelData,
//                orderRequestDTO.getEmailSubject(),
//                orderRequestDTO.getEmailBody(),
//                orderRequestDTO.getFileName()
//        );

        // 3. 발주일시 업데이트
        orderRequestDTO.getDraftIds().forEach(draftId -> {
            TonerMaster tonerMaster = tonerMasterRepository.findById(draftId)
                    .orElseThrow(() -> new EntityNotFoundException("DraftId not found with id " + draftId));
            tonerMaster.updateOrder();
            tonerMasterRepository.save(tonerMaster);

            // TODO: 알림 전송
        });
    }

    @Override
    @Transactional
    public EmailSettingsResponseDTO getEmailSettings() {
        StdGroup stdGroup = stdGroupRepository.findByGroupCd("B003")
                .orElseThrow(() -> new EntityNotFoundException("B003"));
        StdDetail stdDetail = stdDetailRepository.findByGroupCdAndDetailCd(stdGroup, "002")
                .orElseThrow(() -> new EntityNotFoundException("002"));

        return new EmailSettingsResponseDTO(stdDetail.getEtcItem2());
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
