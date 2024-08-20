package kr.or.kmi.mis.api.doc.service.impl;

import kr.or.kmi.mis.api.doc.model.entity.DocDetail;
import kr.or.kmi.mis.api.doc.model.entity.DocMaster;
import kr.or.kmi.mis.api.doc.model.request.DocRequestDTO;
import kr.or.kmi.mis.api.doc.model.request.DocUpdateRequestDTO;
import kr.or.kmi.mis.api.doc.model.response.DocDetailResponseDTO;
import kr.or.kmi.mis.api.doc.model.response.DocMasterResponseDTO;
import kr.or.kmi.mis.api.doc.model.response.DocMyResponseDTO;
import kr.or.kmi.mis.api.doc.model.response.DocPendingResponseDTO;
import kr.or.kmi.mis.api.doc.repository.DocDetailRepository;
import kr.or.kmi.mis.api.doc.repository.DocMasterRepository;
import kr.or.kmi.mis.api.doc.service.DocHistoryService;
import kr.or.kmi.mis.api.doc.service.DocService;
import kr.or.kmi.mis.api.std.service.StdBcdService;
import kr.or.kmi.mis.api.user.service.InfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocServiceImpl implements DocService {

    private final DocMasterRepository docMasterRepository;
    private final DocDetailRepository docDetailRepository;
    private final DocHistoryService docHistoryService;
    private final StdBcdService stdBcdService;
    private final InfoService infoService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    @Transactional
    public void applyDoc(DocRequestDTO docRequestDTO, MultipartFile file) throws IOException {

        // 문서수발신
        DocMaster docMaster = docRequestDTO.toMasterEntity();
        docMaster = docMasterRepository.save(docMaster);

        // 파일 저장 처리
        String fileName = null;
        String filePath = null;
        if (file != null && !file.isEmpty()) {
            fileName = file.getOriginalFilename();

            // 파일 저장 경로 생성
            Path fileStoragePath = Paths.get(uploadDir).toAbsolutePath().normalize();
            if (Files.notExists(fileStoragePath)) {
                Files.createDirectories(fileStoragePath);
            }

            // 파일 저장
            Path targetLocation = fileStoragePath.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation);

            filePath = targetLocation.toString();
        }

        // 문서수발신 상세
        Long draftId = docMaster.getDraftId();
        DocDetail docDetail = docRequestDTO.toDetailEntity(draftId, fileName, filePath);
        docDetailRepository.save(docDetail);
    }

    @Override
    @Transactional
    public void updateDocApply(Long draftId, DocUpdateRequestDTO docUpdateRequestDTO) {

        DocMaster docMaster = docMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        // 문서수발신 상세 조회
        DocDetail docDetailInfo = docDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        // 문서수발신 히스토리 저장
        docHistoryService.createDocHistory(docDetailInfo);

        // 문서수발신 수정사항 저장
        docDetailInfo.update(docUpdateRequestDTO);
        docDetailRepository.save(docDetailInfo);

        docMaster.setUpdtDt(new Timestamp(System.currentTimeMillis()));
        docMaster.setUpdtrId(docMaster.getDrafterId());
        docDetailInfo.setUpdtDt(new Timestamp(System.currentTimeMillis()));
    }

    @Override
    @Transactional
    public void cancelDocApply(Long draftId) {

        DocMaster docMaster = docMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        docMaster.updateStatus("F");
    }

    @Override
    @Transactional(readOnly = true)
    public DocDetailResponseDTO getDoc(Long draftId) {

        DocMaster docMaster = docMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        DocDetail docDetail = docDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        return DocDetailResponseDTO.of(docMaster, docDetail);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocMyResponseDTO> getMyDocApplyByDateRange(Timestamp startDate, Timestamp endDate) {

        String userId = infoService.getUserInfo().getUserId();

        return new ArrayList<>(this.getMyDocMasterList(userId, startDate, endDate));
    }

    public List<DocMyResponseDTO> getMyDocMasterList(String userId, Timestamp startDate, Timestamp endDate) {

        List<DocMaster> docMasterList = docMasterRepository.findByDrafterIdAndDraftDateBetween(userId, startDate, endDate)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        return docMasterList.stream()
                .map(docMaster -> {
                    DocDetail docDetail = docDetailRepository.findById(docMaster.getDraftId())
                            .orElseThrow(() -> new IllegalArgumentException("Division Not Found"));
                    return DocMyResponseDTO.of(docMaster, docDetail.getDivision());
                }).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocPendingResponseDTO> getMyDocPendingList() {

        String userId = infoService.getUserInfo().getUserId();

        return new ArrayList<>(this.getMyDocPendingMasterList(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocMasterResponseDTO> getDocApplyByDateRange(Timestamp startDate, Timestamp endDate) {

        List<DocMaster> docMasters = docMasterRepository.findAllByStatusNotAndDraftDateBetweenOrderByDraftDateDesc("F", startDate, endDate);

        if (docMasters == null) {
            docMasters = new ArrayList<>();
        }

        return docMasters.stream()
                .map(docMaster -> {
                    DocDetail docDetail = docDetailRepository.findById(docMaster.getDraftId())
                            .orElseThrow(() -> new IllegalArgumentException("Division Not Found"));
                    DocMasterResponseDTO docMasterResponseDTO = DocMasterResponseDTO.of(docMaster, docDetail.getDivision());
                    docMasterResponseDTO.setInstNm(stdBcdService.getInstNm(docMaster.getInstCd()));
                    return  docMasterResponseDTO;
                }).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocPendingResponseDTO> getDocPendingList() {
        List<DocMaster> docMasters = docMasterRepository.findAllByStatusOrderByDraftDateDesc("A");

        return docMasters.stream()
                .map(docMaster -> {
                    DocDetail docDetail = docDetailRepository.findById(docMaster.getDraftId())
                            .orElseThrow(() -> new IllegalArgumentException("Division Not Found"));
                    DocPendingResponseDTO docPendingResponseDTO = DocPendingResponseDTO.of(docMaster, docDetail.getDivision());
                    docPendingResponseDTO.setInstNm(stdBcdService.getInstNm(docMaster.getInstCd()));
                    return  docPendingResponseDTO;
                }).toList();
    }

    public List<DocPendingResponseDTO> getMyDocPendingMasterList(String userId) {
        List<DocMaster> docMasterList = docMasterRepository.findByDrafterIdAndStatus(userId, "A")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        return docMasterList.stream()
                .map(docMaster -> {
                    DocDetail docDetail = docDetailRepository.findById(docMaster.getDraftId())
                            .orElseThrow(() -> new IllegalArgumentException("Division Not Found"));
                    return DocPendingResponseDTO.of(docMaster, docDetail.getDivision());
                }).toList();
    }
}
