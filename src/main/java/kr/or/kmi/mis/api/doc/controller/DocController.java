package kr.or.kmi.mis.api.doc.controller;

import com.jcraft.jsch.SftpException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.doc.model.request.ReceiveDocRequestDTO;
import kr.or.kmi.mis.api.doc.model.request.SendDocRequestDTO;
import kr.or.kmi.mis.api.doc.model.request.DocUpdateRequestDTO;
import kr.or.kmi.mis.api.doc.model.response.DocDetailResponseDTO;
import kr.or.kmi.mis.api.doc.service.DocService;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import kr.or.kmi.mis.config.SftpClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/doc")
@RequiredArgsConstructor
@Tag(name = "DocApply", description = "문서수발신 신청 관련 API")
public class DocController {

    private final DocService docService;
    private final SftpClient sftpClient;

    @Value("${sftp.remote-directory.doc}")
    private String docRemoteDirectory;

    @Operation(summary = "create receive doc apply", description = "유저 > 문서수신 신청")
    @PostMapping("/receive")
    public ApiResponse<?> createReceiveDoc(
            @RequestPart("docRequest") ReceiveDocRequestDTO docRequestDTO,
            @RequestPart(value = "file", required = false) MultipartFile file) throws Exception {
        docService.applyReceiveDoc(docRequestDTO, file);
        return ResponseWrapper.success();
    }

    @Operation(summary = "create receive doc apply by leader", description = "팀원 제외 유저 > 문서수신 신청")
    @PostMapping("/receive/leader")
    public ApiResponse<?> createReceiveDocByLeader(
            @RequestPart("docRequest") ReceiveDocRequestDTO docRequestDTO,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {
        docService.applyReceiveDocByLeader(docRequestDTO, file);
        return ResponseWrapper.success();
    }

    @Operation(summary = "create send doc apply", description = "유저 > 문서발신 신청")
    @PostMapping("/send")
    public ApiResponse<?> createSendDoc(
            @RequestPart("docRequest") SendDocRequestDTO docRequestDTO,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {
        docService.applySendDoc(docRequestDTO, file);
        return ResponseWrapper.success();
    }

    @Operation(summary = "create send doc apply by leader", description = "팀원 제외 유저 > 문서발신 신청")
    @PostMapping("/send/leader")
    public ApiResponse<?> createSendDocByLeader(
            @RequestPart("docRequest") SendDocRequestDTO docRequestDTO,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {
        docService.applySendDocByLeader(docRequestDTO, file);
        return ResponseWrapper.success();
    }

    @Operation(summary = "modify doc apply", description = "유저 > 문서수발신 수정")
    @PostMapping(value = "/update")
    public ApiResponse<?> updateDocApply(
            @RequestParam("draftId") Long draftId,
            @RequestPart("docUpdateRequest") DocUpdateRequestDTO docUpdateRequestDTO,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "isFileDeleted", defaultValue = "false") boolean isFileDeleted) throws IOException {
        docService.updateDocApply(draftId, docUpdateRequestDTO, file, isFileDeleted);
        return ResponseWrapper.success();
    }

    @Operation(summary = "cancel doc apply", description = "유저 > 문서수발신 취소")
    @PutMapping(value = "/{draftId}")
    public ApiResponse<?> cancelDoc(@PathVariable Long draftId) {
        docService.cancelDocApply(draftId);
        return ResponseWrapper.success();
    }

    @Operation(summary = "get doc detail", description = "유저 > 문서수발신 상세 정보 조회")
    @GetMapping(value = "/{draftId}")
    public ApiResponse<DocDetailResponseDTO> getDocDetail(@PathVariable Long draftId) {
        return ResponseWrapper.success(docService.getDoc(draftId));
    }

    @Operation(summary = "파일 다운로드", description = "유저 > 파일 다운로드")
    @GetMapping("/download/{filename}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable("filename") String filename) {
        InputStream inputStream = null;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        try {
            inputStream = sftpClient.downloadFile(filename, docRemoteDirectory);

            if (inputStream == null) {
                throw new IOException("File not found on SFTP server.");
            }

//            byte[] temp = new byte[4096];
            byte[] temp = new byte[2048];

            int bytesRead;
            while ((bytesRead = inputStream.read(temp)) != -1) {
                buffer.write(temp, 0, bytesRead);
            }

            byte[] fileBytes = buffer.toByteArray();
            String encodedFileName = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");

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
        } finally {
            // 스트림 닫기
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                System.err.println("Error closing input stream: " + e.getMessage());
            }
        }
    }
}
