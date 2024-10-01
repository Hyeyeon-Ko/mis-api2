package kr.or.kmi.mis.api.file.controller;

import com.jcraft.jsch.SftpException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.file.model.entity.FileDetail;
import kr.or.kmi.mis.api.file.model.entity.FileDownloadHistory;
import kr.or.kmi.mis.api.file.model.entity.FileHistory;
import kr.or.kmi.mis.api.file.model.request.FileDownloadRequestDTO;
import kr.or.kmi.mis.api.file.repository.FileDetailRepository;
import kr.or.kmi.mis.api.file.repository.FileDownloadHistoryRepository;
import kr.or.kmi.mis.api.file.repository.FileHistoryRepository;
import kr.or.kmi.mis.config.SftpClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
@Tag(name = "File Download", description = "파일 다운로드 API")
public class FileDownloadController {

    private final SftpClient sftpClient;
    private final FileDownloadHistoryRepository fileDownloadHistoryRepository;
    private final FileDetailRepository fileDetailRepository;
    private final FileHistoryRepository fileHistoryRepository;

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

        String remoteDirectory;
        // TODO: draftId 관련 코드 기준자료에 입력 후 수정!!!!!!! 임시 if문
        if (fileDownloadRequestDTO.getDraftId().substring(0, 2).equalsIgnoreCase("cd")) {
            remoteDirectory = directoryMap.get("corpdoc");
        } else if (fileDownloadRequestDTO.getDraftId().substring(0, 2).equalsIgnoreCase("sl")) {
            remoteDirectory = directoryMap.get("seal");
        } else {
            remoteDirectory = directoryMap.get("doc");
        }

        try {
            byte[] fileBytes = sftpClient.downloadFile(filename, remoteDirectory);

            if (fileBytes == null || fileBytes.length == 0) {
                throw new IOException("File not found on SFTP server.");
            }

            String encodedFileName = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");

            // 다운로드 사유 기록
            FileDownloadHistory fileDownloadHistory = fileDownloadRequestDTO.toEntity(filename, fileDownloadRequestDTO);
            fileDownloadHistory.setRgstDt(LocalDateTime.now());
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

    @Operation(summary = "파일 다운로드", description = "유저 > 여러 파일 다운로드")
    @PostMapping("/download/multiple")
    public ResponseEntity<byte[]> downloadMultipleFiles(@RequestBody List<FileDownloadRequestDTO> fileDownloadRequestDTOs) {

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ZipOutputStream zipOut = new ZipOutputStream(byteArrayOutputStream)) {

            for (FileDownloadRequestDTO requestDTO : fileDownloadRequestDTOs) {

                FileDetail fileDetail = fileDetailRepository.findByDraftId(requestDTO.getDraftId())
                        .orElseThrow(() -> new IllegalArgumentException("Not Found"));
                FileHistory fileHistory = fileHistoryRepository.findTopByAttachIdOrderBySeqIdDesc(fileDetail.getAttachId())
                        .orElseThrow(() -> new IllegalArgumentException("Not Found"));
                String filename = fileHistory.getFileName();

                Map<String, String> directoryMap = Map.of(
                        "doc", docRemoteDirectory,
                        "seal", exportRemoteDirectory,
                        "corpdoc", corpdocRemoteDirectory
                );

                String remoteDirectory;
                // TODO: draftId 관련 코드 기준자료에 입력 후 수정!!!!!!! 임시 if문
                if (requestDTO.getDraftId().substring(0, 2).equalsIgnoreCase("cd")) {
                    remoteDirectory = directoryMap.get("corpdoc");
                } else if (requestDTO.getDraftId().substring(0, 2).equalsIgnoreCase("sl")) {
                    remoteDirectory = directoryMap.get("seal");
                } else {
                    remoteDirectory = directoryMap.get("doc");
                }

                byte[] fileBytes = sftpClient.downloadFile(filename, remoteDirectory);

                if (fileBytes != null && fileBytes.length > 0) {
                    ZipEntry zipEntry = new ZipEntry(filename);
                    zipOut.putNextEntry(zipEntry);

                    zipOut.write(fileBytes, 0, fileBytes.length);
                    zipOut.closeEntry();
                }

                // 다운로드 사유 기록
                FileDownloadHistory fileDownloadHistory = requestDTO.toEntity(filename, requestDTO);
                fileDownloadHistory.setRgstDt(LocalDateTime.now());
                fileDownloadHistory.setRgstrId(requestDTO.getDownloaderId());
                fileDownloadHistoryRepository.save(fileDownloadHistory);
            }

            zipOut.finish();
            byte[] zipBytes = byteArrayOutputStream.toByteArray();

            String encodedZipFileName = URLEncoder.encode("download.zip", StandardCharsets.UTF_8).replace("+", "%20");

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedZipFileName + "\"")
                    .body(zipBytes);

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
