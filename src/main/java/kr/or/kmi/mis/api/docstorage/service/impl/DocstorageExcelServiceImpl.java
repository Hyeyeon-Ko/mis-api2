package kr.or.kmi.mis.api.docstorage.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import kr.or.kmi.mis.api.docstorage.domain.entity.DocStorageDetail;
import kr.or.kmi.mis.api.docstorage.domain.request.DocStorageUpdateRequestDTO;
import kr.or.kmi.mis.api.docstorage.domain.response.DocstorageExcelResponseDTO;
import kr.or.kmi.mis.api.docstorage.repository.DocStorageDetailRepository;
import kr.or.kmi.mis.api.docstorage.service.DocstorageExcelService;
import kr.or.kmi.mis.api.std.repository.StdDetailRepository;
import kr.or.kmi.mis.api.std.repository.StdGroupRepository;
import kr.or.kmi.mis.api.user.service.InfoService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DocstorageExcelServiceImpl implements DocstorageExcelService {

    private final DocStorageDetailRepository docStorageDetailRepository;
    private final StdGroupRepository stdGroupRepository;
    private final StdDetailRepository stdDetailRepository;
    private final InfoService infoService;

    @Override
    public void downloadExcel(HttpServletResponse response, List<Long> detailIds) throws IOException {
        byte[] excelData = generateExcel(detailIds);

        try {
            String encodedFileName = URLEncoder.encode("문서보관 목록표.xlsx", StandardCharsets.UTF_8.toString());

            // HTTP 응답에 엑셀 파일 첨부
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);
            response.setContentLength(excelData.length);
            response.getOutputStream().write(excelData);
            response.getOutputStream().flush();
            response.getOutputStream().close();
        } catch (Exception e) {
            throw new IOException("Failed to send Excel file", e);
        }
    }

    @Override
    public byte[] generateExcel(List<Long> detailIds) throws IOException {
        List<DocStorageDetail> docStorageDetails = docStorageDetailRepository.findAllByDetailIdIn(detailIds);

        Workbook wb = new XSSFWorkbook();
        try {
            byte[] excelData = createExcelData(docStorageDetails);
            return excelData;
        } catch (Exception e) {
            throw new IOException("Error generating Excel file", e);
        } finally {
            wb.close();
        }
    }

    @Override
    public void saveDocstorageDetails(List<DocstorageExcelResponseDTO> details) {

        String rgstrId = infoService.getUserInfo().getUserName();
        Timestamp rgstDt = new Timestamp(System.currentTimeMillis());

        List<DocStorageDetail> entities = details.stream().map(dto -> {
            DocStorageDetail entity = DocStorageDetail.builder()
                    .teamNm(dto.getTeamNm())
                    .docId(dto.getDocId())
                    .location(dto.getLocation())
                    .docNm(dto.getDocNm())
                    .manager(dto.getManager())
                    .subManager(dto.getSubManager())
                    .storageYear(dto.getStorageYear())
                    .createDate(dto.getCreateDate())
                    .transferDate(dto.getTransferDate())
                    .tsdNum(dto.getTsdNum())
                    .disposalDate(dto.getDisposalDate())
                    .dpdNum(dto.getDpdNum())
                    .deptCd(dto.getDeptCd())
                    .build();

            entity.setRgstrId(rgstrId);
            entity.setRgstDt(rgstDt);

            return entity;
        }).collect(Collectors.toList());

        docStorageDetailRepository.saveAll(entities);
    }

    @Override
    public void updateDocstorageDetails(List<DocstorageExcelResponseDTO> details) {
        List<DocStorageDetail> entities = details.stream().map(dto -> {

            DocStorageDetail existingDetail = docStorageDetailRepository.findByDocId(dto.getDocId())
                    .orElseThrow(() -> new IllegalArgumentException("Not Found"));

            return DocStorageDetail.builder()
                    .detailId(existingDetail.getDetailId())
                    .docId(existingDetail.getDocId())
                    .teamNm(dto.getTeamNm() != null ? dto.getTeamNm() : existingDetail.getTeamNm())
                    .location(dto.getLocation() != null ? dto.getLocation() : existingDetail.getLocation())
                    .docNm(dto.getDocNm() != null ? dto.getDocNm() : existingDetail.getDocNm())
                    .manager(dto.getManager() != null ? dto.getManager() : existingDetail.getManager())
                    .subManager(dto.getSubManager() != null ? dto.getSubManager() : existingDetail.getSubManager())
                    .storageYear(dto.getStorageYear() != null ? dto.getStorageYear() : existingDetail.getStorageYear())
                    .createDate(dto.getCreateDate() != null ? dto.getCreateDate() : existingDetail.getCreateDate())
                    .transferDate(dto.getTransferDate() != null ? dto.getTransferDate() : existingDetail.getTransferDate())
                    .tsdNum(dto.getTsdNum() != null ? dto.getTsdNum() : existingDetail.getTsdNum())
                    .disposalDate(dto.getDisposalDate() != null ? dto.getDisposalDate() : existingDetail.getDisposalDate())
                    .dpdNum(dto.getDpdNum() != null ? dto.getDpdNum() : existingDetail.getDpdNum())
                    .deptCd(existingDetail.getDeptCd())
                    .build();
        }).collect(Collectors.toList());

        docStorageDetailRepository.saveAll(entities);
    }

    private byte[] createExcelData(List<DocStorageDetail> docStorageDetails) throws IOException {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("문서박스 목록표");

        // 스타일 설정
        CellStyle headerStyle = createHeaderStyle(wb);
        CellStyle thinBorderStyle = createThinBorderStyle(wb);
        CellStyle titleStyle = createTitleStyle(wb);
        CellStyle centeredStyle = createCenteredStyle(wb);

        // 제목 생성
        createTitle(sheet, titleStyle);

        // 병합된 헤더 생성
        createMergedHeader(sheet, headerStyle);

        // 데이터 채우기
        AtomicInteger rowNum = new AtomicInteger(4);
        AtomicInteger no = new AtomicInteger(1);

        docStorageDetails.forEach(detail -> {
            Row row = sheet.createRow(rowNum.getAndIncrement());
            row.setHeight((short) 600);

            createCell(row, 0, no.getAndIncrement(), thinBorderStyle, centeredStyle);
            createCell(row, 1, detail.getTeamNm(), thinBorderStyle, centeredStyle);
            createCell(row, 2, detail.getDocId(), thinBorderStyle, centeredStyle);
            createCell(row, 3, detail.getLocation() != null ? detail.getLocation() : "", thinBorderStyle, centeredStyle);
            createCell(row, 4, detail.getDocNm(), thinBorderStyle, centeredStyle);
            createCell(row, 5, detail.getManager(), thinBorderStyle, centeredStyle);
            createCell(row, 6, detail.getSubManager(), thinBorderStyle, centeredStyle);
            createCell(row, 7, detail.getStorageYear(), thinBorderStyle, centeredStyle);
            createCell(row, 8, detail.getCreateDate() != null ? detail.getCreateDate().toString() : "", thinBorderStyle, centeredStyle);
            createCell(row, 9, detail.getTransferDate() != null ? detail.getTransferDate().toString() : "", thinBorderStyle, centeredStyle);
            createCell(row, 10, detail.getTsdNum() != null ? detail.getTsdNum() : "", thinBorderStyle, centeredStyle);
            createCell(row, 11, detail.getDisposalDate() != null ? detail.getDisposalDate().toString() : "", thinBorderStyle, centeredStyle);
            createCell(row, 12, detail.getDpdNum() != null ? detail.getDpdNum() : "", thinBorderStyle, centeredStyle);
        });

        // 열 너비 조정
        adjustColumnWidths(sheet);

        // 엑셀 파일을 ByteArrayOutputStream에 쓰기
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        wb.write(baos);
        return baos.toByteArray();
    }

    private void createTitle(Sheet sheet, CellStyle titleStyle) {
        Row titleRow = sheet.createRow(0);
        titleRow.setHeight((short) 800);
        Cell cell = titleRow.createCell(0);
        cell.setCellValue("문서박스 목록표");

        CellStyle combinedStyle = sheet.getWorkbook().createCellStyle();
        combinedStyle.cloneStyleFrom(titleStyle);
        combinedStyle.setBorderBottom(BorderStyle.MEDIUM);
        combinedStyle.setBorderTop(BorderStyle.MEDIUM);
        combinedStyle.setBorderLeft(BorderStyle.MEDIUM);
        combinedStyle.setBorderRight(BorderStyle.MEDIUM);

        cell.setCellStyle(combinedStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 12));

        // 병합된 영역에 스타일 적용
        applyStyleToMergedRegion(sheet, 0, 0, 0, 12, combinedStyle);
    }

    private void createMergedHeader(Sheet sheet, CellStyle style) {
        Row headerRow1 = sheet.createRow(2);
        headerRow1.setHeight((short) 500);

        Row headerRow2 = sheet.createRow(3);
        headerRow2.setHeight((short) 500);

        // NO 셀
        createMergedCell(headerRow1, headerRow2, sheet, 0, 2, 3, style, "No");

        // 팀명 셀
        createMergedCell(headerRow1, headerRow2, sheet, 1, 2, 3, style, "팀명");

        // 문서관리번호 셀
        createMergedCell(headerRow1, headerRow2, sheet, 2, 2, 3, style, "문서관리번호");

        // 입고위치 셀
        createMergedCell(headerRow1, headerRow2, sheet, 3, 2, 3, style, "입고위치");

        // 문서명 셀
        createMergedCell(headerRow1, headerRow2, sheet, 4, 2, 3, style, "문서명");

        // 관리자 셀 (5~6열 병합)
        createMergedCell(headerRow1, headerRow2, sheet, 5, 2, 2, 5, 6, style, "관리자");
        createMergedCell(headerRow2, sheet, 5, style, "정");
        createMergedCell(headerRow2, sheet, 6, style, "부");

        // 보존연한 셀
        createMergedCell(headerRow1, headerRow2, sheet, 7, 2, 3, style, "보존연한");

        // 생성일자 셀
        createMergedCell(headerRow1, headerRow2, sheet, 8, 2, 3, style, "생성일자");

        // 이관일자 셀
        createMergedCell(headerRow1, headerRow2, sheet, 9, 2, 3, style, "이관일자");

        // 기안번호 셀
        createMergedCell(headerRow1, headerRow2, sheet, 10, 2, 3, style, "기안번호");

        // 폐기일자 셀
        createMergedCell(headerRow1, headerRow2, sheet, 11, 2, 3, style, "폐기일자");

        // 기안번호 셀 (12열)
        createMergedCell(headerRow1, headerRow2, sheet, 12, 2, 3, style, "기안번호");
    }

    private void createMergedCell(Row headerRow1, Row headerRow2, Sheet sheet, int column, int startRow, int endRow, CellStyle style, String value) {
        Cell cell = headerRow1.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
        sheet.addMergedRegion(new CellRangeAddress(startRow, endRow, column, column));
        applyStyleToMergedRegion(sheet, startRow, endRow, column, column, style);
    }

    private void createMergedCell(Row headerRow, Sheet sheet, int column, CellStyle style, String value) {
        Cell cell = headerRow.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
        applyStyleToMergedRegion(sheet, headerRow.getRowNum(), headerRow.getRowNum(), column, column, style);
    }

    private void createMergedCell(Row headerRow1, Row headerRow2, Sheet sheet, int column, int startRow1, int endRow1, int startCol, int endCol, CellStyle style, String value) {
        Cell cell = headerRow1.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
        sheet.addMergedRegion(new CellRangeAddress(startRow1, endRow1, startCol, endCol));
        applyStyleToMergedRegion(sheet, startRow1, endRow1, startCol, endCol, style);
    }

    private void applyStyleToMergedRegion(Sheet sheet, int startRow, int endRow, int startCol, int endCol, CellStyle style) {
        for (int i = startRow; i <= endRow; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                row = sheet.createRow(i);
            }
            for (int j = startCol; j <= endCol; j++) {
                Cell cell = row.getCell(j);
                if (cell == null) {
                    cell = row.createCell(j);
                }
                cell.setCellStyle(style);
            }
        }
    }

    private void createCell(Row row, int column, Object value, CellStyle borderStyle, CellStyle centeredStyle) {
        Cell cell = row.createCell(column);

        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue(value.toString());
        }

        cell.setCellStyle(centeredStyle);
        if (borderStyle != null) {
            cell.setCellStyle(borderStyle);
        }
    }

    private void adjustColumnWidths(Sheet sheet) {
        sheet.setColumnWidth(0, 1500);
        sheet.setColumnWidth(1, 8000);
        sheet.setColumnWidth(2, 8000);
        sheet.setColumnWidth(3, 7000);
        sheet.setColumnWidth(4, 12000);
        sheet.setColumnWidth(5, 3000);
        sheet.setColumnWidth(6, 3000);
        sheet.setColumnWidth(7, 2500);
        sheet.setColumnWidth(8, 5000);
        sheet.setColumnWidth(9, 5000);
        sheet.setColumnWidth(10, 7000);
        sheet.setColumnWidth(11, 5000);
        sheet.setColumnWidth(12, 7000);
    }

    private CellStyle createThinBorderStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createTitleStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 24);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createHeaderStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createCenteredStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
}
