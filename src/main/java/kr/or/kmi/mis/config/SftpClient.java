package kr.or.kmi.mis.config;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Component
public class SftpClient {

    @Value("${sftp.host}")
    private String sftpHost;

    @Value("${sftp.port}")
    private int sftpPort;

    @Value("${sftp.username}")
    private String sftpUsername;

    @Value("${sftp.password}")
    private String sftpPassword;

    private Session session;
    private ChannelSftp channelSftp;

    public String uploadFile(MultipartFile file, String fileName, String remoteDirectory) throws Exception {
        String newFileName = fileName;
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(sftpUsername, sftpHost, sftpPort);
            session.setPassword(sftpPassword);

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();

            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();

            channelSftp.cd(remoteDirectory);

            // 동일한 파일명이 존재할 경우 새로운 이름 생성
            int count = 1;
            while (isFileExist(channelSftp, newFileName)) {
                int dotIndex = fileName.lastIndexOf('.');
                if (dotIndex != -1) {
                    newFileName = fileName.substring(0, dotIndex) + "(" + count + ")" + fileName.substring(dotIndex);
                } else {
                    newFileName = fileName + "(" + count + ")";
                }
                count++;
            }

            try (InputStream inputStream = file.getInputStream()) {
                channelSftp.put(inputStream, newFileName, ChannelSftp.OVERWRITE);
            }

            return newFileName;

        } finally {
            if (channelSftp != null) {
                channelSftp.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }

    public boolean isFileExist(ChannelSftp channelSftp, String fileName) {
        try {
            channelSftp.lstat(fileName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public byte[] downloadFile(String fileName, String remoteDirectory) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        try {
            JSch jsch = new JSch();
            session = jsch.getSession(sftpUsername, sftpHost, sftpPort);
            session.setPassword(sftpPassword);

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            session.setTimeout(60000);
            session.setServerAliveInterval(5000); 
            session.connect();

            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();

            channelSftp.cd(remoteDirectory);

            try (InputStream inputStream = channelSftp.get(fileName, 1024 * 8)) {
                byte[] temp = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(temp)) != -1) {
                    buffer.write(temp, 0, bytesRead);
                }
            }
            // 파일 내용을 byte array로 반환
            return buffer.toByteArray();

        } catch (Exception e) {
            System.err.println("Error downloading file from SFTP: " + e.getMessage());
            throw new IOException("SFTP 파일 다운로드 실패", e);
        } finally {
            if (channelSftp != null) {
                channelSftp.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }

    public void deleteFile(String fileName, String remoteDirectory) throws Exception {

        try {
            JSch jsch = new JSch();
            session = jsch.getSession(sftpUsername, sftpHost, sftpPort);
            session.setPassword(sftpPassword);

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();

            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();

            channelSftp.cd(remoteDirectory);

            channelSftp.rm(fileName);

        } finally {
            if (channelSftp != null) {
                channelSftp.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }
}
