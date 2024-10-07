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
import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import kr.or.kmi.mis.api.std.repository.StdDetailRepository;
import kr.or.kmi.mis.api.std.repository.StdGroupRepository;
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
import java.util.Optional;
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
    private final StdGroupRepository stdGroupRepository;
    private final StdDetailRepository stdDetailRepository;

    @Value("${sftp.remote-directory.doc}")
    private String docRemoteDirectory;

    @Value("${sftp.remote-directory.export}")
    private String exportRemoteDirectory;

    @Value("${sftp.remote-directory.corpdoc}")
    private String corpdocRemoteDirectory;


    @Operation(summary = "파일 다운로드", description = "유저 > 파일 다운로드")
    @GetMapping("/download/{filename}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable("filename") String filename, FileDownloadRequestDTO fileDownloadRequestDTO) {

        StdGroup stdGroup = stdGroupRepository.findByGroupCd("A007")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        StdDetail corpDocStdDetail = stdDetailRepository.findByGroupCdAndDetailCd(stdGroup, "C")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        StdDetail sealStdDetail = stdDetailRepository.findByGroupCdAndDetailCd(stdGroup, "D")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        System.out.println("fileDownloadRequestDTO = " + fileDownloadRequestDTO.getDownloadType());
        System.out.println("fileDownloadRequestDTO = " + fileDownloadRequestDTO.getDownloadNotes());

        Map<String, String> directoryMap = Map.of(
                "doc", docRemoteDirectory,
                "seal", exportRemoteDirectory,
                "corpdoc", corpdocRemoteDirectory
        );

        String remoteDirectory;

        if (fileDownloadRequestDTO.getDraftId().substring(0, 2).equalsIgnoreCase(corpDocStdDetail.getEtcItem1())) {
            remoteDirectory = directoryMap.get("corpdoc");
        } else if (fileDownloadRequestDTO.getDraftId().substring(0, 2).equalsIgnoreCase(sealStdDetail.getEtcItem1())) {
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
            String attachId = generateAttachId();

            FileDownloadHistory fileDownloadHistory = fileDownloadRequestDTO.toEntity(fileDownloadRequestDTO, attachId);
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

        StdGroup stdGroup = stdGroupRepository.findByGroupCd("A007")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        StdDetail corpDocStdDetail = stdDetailRepository.findByGroupCdAndDetailCd(stdGroup, "C")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        StdDetail sealStdDetail = stdDetailRepository.findByGroupCdAndDetailCd(stdGroup, "D")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

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
                if (requestDTO.getDraftId().substring(0, 2).equalsIgnoreCase(corpDocStdDetail.getEtcItem1())) {
                    remoteDirectory = directoryMap.get("corpdoc");
                } else if (requestDTO.getDraftId().substring(0, 2).equalsIgnoreCase(sealStdDetail.getEtcItem1())) {
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
                String attachId = generateAttachId();

                FileDownloadHistory fileDownloadHistory = requestDTO.toEntity(requestDTO, attachId);
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

    private String generateAttachId() {
        Optional<FileDetail> lastFileDetailOpt = fileDetailRepository.findTopByOrderByAttachIdDesc();

        StdGroup stdGroup = stdGroupRepository.findByGroupCd("A007")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        StdDetail stdDetail = stdDetailRepository.findByGroupCdAndDetailCd(stdGroup, "F")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        if (lastFileDetailOpt.isPresent()) {
            String lastAttachId = lastFileDetailOpt.get().getAttachId();
            int lastIdNum = Integer.parseInt(lastAttachId.substring(2));
            return stdDetail.getEtcItem1() + String.format("%010d", lastIdNum + 1);
        } else {
            return stdDetail.getEtcItem1() + "0000000001";
        }
    }

}
