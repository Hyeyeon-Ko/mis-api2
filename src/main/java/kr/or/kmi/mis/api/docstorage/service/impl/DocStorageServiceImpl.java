package kr.or.kmi.mis.api.docstorage.service.impl;

import kr.or.kmi.mis.api.docstorage.domain.entity.DocStorageDetail;
import kr.or.kmi.mis.api.docstorage.domain.entity.DocStorageMaster;
import kr.or.kmi.mis.api.docstorage.domain.request.DocStorageApplyRequestDTO;
import kr.or.kmi.mis.api.docstorage.domain.request.DocStorageRequestDTO;
import kr.or.kmi.mis.api.docstorage.domain.request.DocStorageUpdateRequestDTO;
import kr.or.kmi.mis.api.docstorage.domain.response.DocStorageDetailResponseDTO;
import kr.or.kmi.mis.api.docstorage.repository.DocStorageDetailRepository;
import kr.or.kmi.mis.api.docstorage.repository.DocStorageMasterRepository;
import kr.or.kmi.mis.api.docstorage.service.DocStorageService;
import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import kr.or.kmi.mis.api.std.repository.StdDetailRepository;
import kr.or.kmi.mis.api.std.repository.StdGroupRepository;
import kr.or.kmi.mis.api.user.service.InfoService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class DocStorageServiceImpl implements DocStorageService {

    private final DocStorageMasterRepository docStorageMasterRepository;
    private final DocStorageDetailRepository docStorageDetailRepository;
    private final InfoService infoService;
    private final StdGroupRepository stdGroupRepository;
    private final StdDetailRepository stdDetailRepository;

    @Override
    @Transactional
    public void addStorageInfo(DocStorageRequestDTO docStorageRequestDTO) {
        DocStorageDetail docStorageDetail = docStorageRequestDTO.toDetailEntity();

        docStorageDetail.setRgstrId(infoService.getUserInfo().getUserName());
        docStorageDetail.setRgstDt(new Timestamp(System.currentTimeMillis()));

        docStorageDetailRepository.save(docStorageDetail);
    }

    @Override
    @Transactional
    public void updateStorageInfo(Long detailId, DocStorageUpdateRequestDTO docStorageUpdateDTO) {
        DocStorageDetail docStorageDetail = docStorageDetailRepository.findById(detailId)
                .orElseThrow(() -> new IllegalArgumentException("DocStorageDetail not found By DetailId : " + detailId));

        docStorageDetail.update(docStorageUpdateDTO);
        docStorageDetail.setUpdtDt(new Timestamp(System.currentTimeMillis()));
        docStorageDetail.setUpdtrId(infoService.getUserInfo().getUserName());
        docStorageDetailRepository.save(docStorageDetail);
    }

    @Override
    @Transactional
    public void deleteStorageInfo(Long detailId) {
        DocStorageDetail docStorageDetail = docStorageDetailRepository.findById(detailId)
                .orElseThrow(() -> new IllegalArgumentException("DocStorageDetail not found By DetailId : " + detailId));

        docStorageDetailRepository.delete(docStorageDetail);
    }

    @Override
    @Transactional(readOnly = true)
    public DocStorageDetailResponseDTO getStorageInfo(Long detailId) {
        DocStorageDetail docStorageDetail = docStorageDetailRepository.findById(detailId)
                .orElseThrow(() -> new IllegalArgumentException("DocStorageDetail not found By DetailId : " + detailId));

        if (docStorageDetail.getDraftId() != null) {
            DocStorageMaster docStorageMaster = docStorageMasterRepository.findById(docStorageDetail.getDraftId())
                    .orElseThrow(() -> new IllegalArgumentException("DocStorageMaster not found By DetailId : " + detailId));
            return DocStorageDetailResponseDTO.of(docStorageDetail, docStorageMaster.getType(), docStorageMaster.getStatus());
        } else {
            return DocStorageDetailResponseDTO.of(docStorageDetail, "", "미신청");
        }
    }

    @Override
    @Transactional
    public void applyStorage(DocStorageApplyRequestDTO docStorageApplyRequestDTO) {

        String drafter = infoService.getUserInfo().getUserName();
        String drafterId = infoService.getUserInfo().getUserId();

        DocStorageMaster docStorageMaster = docStorageMasterRepository.save(docStorageApplyRequestDTO.toMasterEntity(drafter, drafterId));

        docStorageApplyRequestDTO.getDetailIds().forEach(detailId -> {
            docStorageDetailRepository.findById(detailId).ifPresent(docStorageDetail -> {
                docStorageDetail.updateDraftId(docStorageMaster.getDraftId());
                docStorageDetailRepository.save(docStorageDetail);
            });
        });
    }

    @Override
    @Transactional
    public List<DocStorageDetail> parseAndSaveFileData(MultipartFile file, String teamCd) {

        List<DocStorageDetail> documents = new ArrayList<>();

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !(originalFilename.endsWith(".xlsx") || originalFilename.endsWith(".xls"))) {
            throw new IllegalArgumentException("Invalid file format. Please upload an .xlsx or .xls file.");
        }

        try {
            // 파일을 서버에 임시로 저장
            Path tempFile = Paths.get(System.getProperty("java.io.tmpdir"), originalFilename);
            Files.write(tempFile, file.getBytes());

            // 저장된 파일을 다시 읽어서 처리
            Workbook workbook = originalFilename.endsWith(".xlsx") ?
                    new XSSFWorkbook(Files.newInputStream(tempFile)) :
                    new HSSFWorkbook(Files.newInputStream(tempFile));

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 4; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || row.getCell(0) == null) {
                    continue;
                }

                DocStorageDetail detail = DocStorageDetail.builder()
                        .teamNm(getCellValue(row, 1))
                        .docId(getCellValue(row, 2))
                        .location(getCellValue(row, 3))
                        .docNm(getCellValue(row, 4))
                        .manager(getCellValue(row, 5))
                        .subManager(getCellValue(row, 6))
                        .storageYear(getCellValue(row, 7))
                        .createDate(getCellValue(row, 8))
                        .transferDate(getCellValue(row, 9))
                        .tsdNum(getCellValue(row, 10))
                        .disposalDate(getCellValue(row, 11))
                        .dpdNum(getCellValue(row, 12))
                        .build();

                documents.add(detail);
            }

            saveFileData(documents, teamCd);

        } catch (NotOfficeXmlFileException e) {
            throw new IllegalArgumentException("The uploaded file is not a valid Excel (.xlsx or .xls) file.", e);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse Excel file", e);
        }

        return documents;
    }

    private String getCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "";
        }
    }

    private void saveFileData(List<DocStorageDetail> documents, String teamCd) {
        StdGroup stdGroup = stdGroupRepository.findByGroupCd("A003")
                .orElseThrow(() -> new IllegalArgumentException("StdGroup Not Found"));
        StdDetail stdDetail = stdDetailRepository.findByGroupCdAndDetailCd(stdGroup, teamCd)
                .orElseThrow(() -> new IllegalArgumentException("StdDetail Not Found"));

        String deptCd = stdDetail.getEtcItem1();

        documents.forEach(document -> document.updateDeptCd(deptCd));

        docStorageDetailRepository.saveAll(documents);
    }
    @Override
    @Transactional
    public void approveStorage(List<Long> draftIds) {
        draftIds.forEach(draftId -> {
            docStorageMasterRepository.findById(draftId).ifPresent(docStorageMaster -> {
                docStorageMaster.updateStatus("E");
                docStorageMasterRepository.save(docStorageMaster);
            });
        });
    }
}
