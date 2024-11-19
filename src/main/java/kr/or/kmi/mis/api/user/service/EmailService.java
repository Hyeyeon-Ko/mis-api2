package kr.or.kmi.mis.api.user.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmailService {

    void sendEmailWithDynamicCredentials(String smtpHost, int smtpPort, String username,
                                         String password, String fromEmail, String toEmail,
                                         String subject, String body, byte[] previewFileData,
                                         List<MultipartFile> addtionalFiles, String previewFileName);

}
