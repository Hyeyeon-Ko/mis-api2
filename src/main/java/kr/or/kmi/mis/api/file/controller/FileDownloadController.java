package kr.or.kmi.mis.api.file.controller;

import com.jcraft.jsch.SftpException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.file.model.entity.FileDownloadHistory;
import kr.or.kmi.mis.api.file.model.request.FileDownloadRequestDTO;
import kr.or.kmi.mis.api.file.repository.FileDownloadHistoryRepository;
import kr.or.kmi.mis.config.SftpClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Map;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
@Tag(name = "File Download", description = "파일 다운로드 API")
public class FileDownloadController {

    private final SftpClient sftpClient;
    private final FileDownloadHistoryRepository fileDownloadHistoryRepository;

    @Value("${sftp.remote-directory.doc}")
    private String docRemoteDirectory;

    @Value("${sftp.remote-directory.export}")
    private String exportRemoteDirectory;

    @Value("${sftp.remote-directory.corpdoc}")
    private String corpdocRemoteDirectory;


    @Operation(summary = "파일 다운로드", description = "유저 > 파일 다운로드")
    @GetMapping("/download/{filename}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable("filename") String filename, FileDownloadRequestDTO fileDownloadRequestDTO) {

        Map<String, String> directoryMap = Map.of(
                "doc", docRemoteDirectory,
                "seal", exportRemoteDirectory,
                "corpdoc", corpdocRemoteDirectory
        );

        String remoteDirectory = directoryMap.get(fileDownloadRequestDTO.getDocType());

        try {
            byte[] fileBytes = sftpClient.downloadFile(filename, remoteDirectory);

            if (fileBytes == null || fileBytes.length == 0) {
                throw new IOException("File not found on SFTP server.");
            }

            // 파일명을 URL 인코딩하여 헤더에 추가
            String encodedFileName = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");

            // 다운로드 기록 저장
            FileDownloadHistory fileDownloadHistory = fileDownloadRequestDTO.toEntity(filename, fileDownloadRequestDTO);
            fileDownloadHistory.setRgstDt(new Timestamp(System.currentTimeMillis()));
            fileDownloadHistory.setRgstrId(fileDownloadRequestDTO.getDownloaderId());
            fileDownloadHistoryRepository.save(fileDownloadHistory);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
                    .body(fileBytes);

        } catch (SftpException e) {
            System.err.println("SFTP error occurred during file download: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (IOException e) {
            System.err.println("IO error occurred during file download: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (Exception e) {
            System.err.println("Unexpected error occurred during file download: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
