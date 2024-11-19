package kr.or.kmi.mis.api.user.service.Impl;

import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.MessagingException;
import jakarta.mail.SendFailedException;
import jakarta.mail.internet.MimeMessage;
import kr.or.kmi.mis.api.user.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.Properties;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    public void sendEmailWithDynamicCredentials(
            String smtpHost,
            int smtpPort,
            String username,
            String password,
            String fromEmail,
            String toEmail,
            String subject,
            String body,
            byte[] previewFileData,
            List<MultipartFile> additionalFiles,
            String previewFileName
    ) {
        try {
            JavaMailSenderImpl mailSenderImpl = new JavaMailSenderImpl();
            mailSenderImpl.setHost(smtpHost);
            mailSenderImpl.setPort(smtpPort);
            mailSenderImpl.setUsername(username);
            mailSenderImpl.setPassword(password);

            Properties props = mailSenderImpl.getJavaMailProperties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.debug", "true");
            props.put("mail.smtp.ssl.trust", smtpHost);
            props.put("mail.smtp.ssl.enable", "true");

            MimeMessage message = mailSenderImpl.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body);

            if (previewFileData != null) {
                helper.addAttachment(previewFileName + ".xlsx", new ByteArrayResource(previewFileData));
            }

            if (additionalFiles != null) {
                for (MultipartFile file : additionalFiles) {
                    helper.addAttachment(Objects.requireNonNull(file.getOriginalFilename()), file);
                }
            }

            mailSenderImpl.send(message);

        } catch (AuthenticationFailedException e) {
            log.error("Authentication failed: 잘못된 사용자 이름 또는 비밀번호. {}", e.getMessage(), e);
        } catch (SendFailedException e) {
            log.error("Message sending failed: 수신자 주소가 올바른지 확인하세요. {}", e.getMessage(), e);
        } catch (MessagingException e) {
            log.error("MessagingException occurred: 이메일 발송 중 오류 발생. {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error occurred: 알 수 없는 오류 발생. {}", e.getMessage(), e);
        }
    }

}
