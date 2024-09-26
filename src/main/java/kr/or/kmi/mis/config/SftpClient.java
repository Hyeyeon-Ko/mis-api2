package kr.or.kmi.mis.config;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

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

    public void uploadFile(MultipartFile file, String fileName, String remoteDirectory) throws Exception {
        Session session = null;
        ChannelSftp channelSftp = null;

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

            System.out.println("remoteDirectory = " + remoteDirectory);

            channelSftp.cd(remoteDirectory);

            try (InputStream inputStream = file.getInputStream()) {
//                channelSftp.put(inputStream, fileName);
                channelSftp.put(inputStream, fileName, ChannelSftp.OVERWRITE);
            }


        } finally {
            if (channelSftp != null) {
                channelSftp.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }

    public InputStream downloadFile(String fileName, String remoteDirectory) throws Exception {
        Session session = null;
        ChannelSftp channelSftp = null;

        try {
            JSch jsch = new JSch();
            session = jsch.getSession(sftpUsername, sftpHost, sftpPort);
            session.setPassword(sftpPassword);

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            session.setTimeout(60000);  // 타임아웃 60초로 설정
            session.setServerAliveInterval(5000);  // 5초마다 keep-alive 패킷 전송
            session.connect();

            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();

            channelSftp.cd(remoteDirectory);
//            InputStream inputStream = channelSftp.get(fileName);
            InputStream inputStream = channelSftp.get(fileName, 1024 * 8);

            if (inputStream == null) {
                System.err.println("File not found on SFTP server: " + fileName);
            } else {
                System.out.println("File found on SFTP server: " + fileName);
            }

            return inputStream;

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
        Session session = null;
        ChannelSftp channelSftp = null;

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
