package kr.or.kmi.mis.api.docstorage.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import kr.or.kmi.mis.api.docstorage.domain.entity.DocStorageDetail;
import kr.or.kmi.mis.api.docstorage.repository.DocStorageDetailRepository;
import kr.or.kmi.mis.api.docstorage.service.DocstorageExcelService;
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
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DocstorageExcelServiceImpl implements DocstorageExcelService {

    private final DocStorageDetailRepository docStorageDetailRepository;

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
        return createExcelData(docStorageDetails);
    }

    private byte[] createExcelData(List<DocStorageDetail> docStorageDetails) throws IOException {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("문서박스 목록표");

        // 스타일 설정
        CellStyle headerStyle = createHeaderStyle(wb);
        CellStyle thinBorderStyle = createThinBorderStyle(wb);
        CellStyle titleStyle = createTitleStyle(wb);
        CellStyle centeredStyle = createCenteredStyle(wb);
        CellStyle thickBorderStyle = createThickBorderStyle(wb);

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

            createCell(row, 0, no.getAndIncrement(), thickBorderStyle, centeredStyle);
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
        wb.close();

        return baos.toByteArray();
    }

    private void createTitle(Sheet sheet, CellStyle style) {
        Row titleRow = sheet.createRow(0);
        titleRow.setHeight((short) 800);
        Cell cell = titleRow.createCell(0);
        cell.setCellValue("문서박스 목록표");
        cell.setCellStyle(style);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 12));
    }

    private void createMergedHeader(Sheet sheet, CellStyle style) {
        Row headerRow = sheet.createRow(2);
        headerRow.setHeight((short) 700);
        String[] headers = {"No", "팀명", "문서관리번호\n(신규생성)", "입고위치\n(대호물류창고)", "문서명", "관리자(정)", "관리자(부)", "보존연한", "생성일자", "이관일자", "기안번호", "폐기일자", "기안번호"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
    }

    private void createCell(Row row, int column, Object value, CellStyle borderStyle, CellStyle centeredStyle) {
        Cell cell = row.createCell(column);

        // null 값을 체크하여 빈 문자열로 처리
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
        sheet.setColumnWidth(3, 6000);
        sheet.setColumnWidth(4, 20000);
        sheet.setColumnWidth(5, 3000);
        sheet.setColumnWidth(6, 3000);
        sheet.setColumnWidth(7, 4000);
        sheet.setColumnWidth(8, 4000);
        sheet.setColumnWidth(9, 6000);
        sheet.setColumnWidth(10, 8000);
        sheet.setColumnWidth(11, 6000);
        sheet.setColumnWidth(12, 8000);
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
        font.setFontHeightInPoints((short) 16);
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

    private CellStyle createThickBorderStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.MEDIUM);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
}
