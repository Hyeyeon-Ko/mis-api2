package kr.or.kmi.mis.api.rental.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import kr.or.kmi.mis.api.rental.model.entity.RentalDetail;
import kr.or.kmi.mis.api.rental.model.response.RentalExcelResponseDTO;
import kr.or.kmi.mis.api.rental.repository.RentalDetailRepository;
import kr.or.kmi.mis.api.rental.service.RentalExcelService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RentalExcelServiceImpl implements RentalExcelService {

    private final RentalDetailRepository rentalDetailRepository;

    @Override
    public void downloadExcel(HttpServletResponse response, List<Long> detailIds) throws IOException {
        byte[] excelData = generateExcel(detailIds);

        try {
            String encodedFileName = URLEncoder.encode("렌탈현황 관리표.xlsx", StandardCharsets.UTF_8);

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
        List<RentalDetail> rentalDetails = rentalDetailRepository.findAllByDetailIdIn(detailIds);

        Workbook wb = new XSSFWorkbook();
        try {
            byte[] excelData = createExcelData(rentalDetails);
            return excelData;
        } catch (Exception e) {
            throw new IOException("Error generating Excel file", e);
        } finally {
            wb.close();
        }
    }

    @Override
    public void saveRentalDetails(List<RentalExcelResponseDTO> details) {

        List<RentalDetail> entities = details.stream().map(dto -> {
            if (rentalDetailRepository.existsByContractNum(dto.getContractNum())) {
                throw new IllegalArgumentException("계약번호가 중복됩니다: " + dto.getContractNum());
            }

            return RentalDetail.builder()
                    .category(dto.getCategory())
                    .companyNm(dto.getCompanyNm())
                    .contractNum(dto.getContractNum())
                    .modelNm(dto.getModelNm())
                    .installDate(dto.getInstallDate())
                    .expiryDate(dto.getExpiryDate())
                    .rentalFee(dto.getRentalFee())
                    .location(dto.getLocation())
                    .installationSite(dto.getInstallationSite())
                    .specialNote(dto.getSpecialNote())
                    .instCd(dto.getInstCd())
                    .build();
        }).collect(Collectors.toList());

        rentalDetailRepository.saveAll(entities);
    }

    @Override
    @Transactional
    public void updateRentalDetails(List<RentalExcelResponseDTO> details) {
        details.forEach(dto -> {
            RentalDetail existingDetail = rentalDetailRepository.findByContractNum(dto.getContractNum())
                    .orElseThrow(() -> new IllegalArgumentException("Not Found"));

            existingDetail.updateExcelData(dto);

            rentalDetailRepository.save(existingDetail);
        });
    }

    private byte[] createExcelData(List<RentalDetail> rentalDetails) throws IOException {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("렌탈현황 관리표");

        // 스타일 설정
        CellStyle headerStyle = createHeaderStyle(wb);
        CellStyle thinBorderStyle = createThinBorderStyle(wb);
        CellStyle titleStyle = createTitleStyle(wb);
        CellStyle centeredStyle = createCenteredStyle(wb);

        // 제목 생성
        createTitle(sheet, titleStyle);

        // 병합된 헤더 생성
        createHeader(sheet, headerStyle);

        // 데이터 채우기
        AtomicInteger rowNum = new AtomicInteger(5);
        AtomicInteger no = new AtomicInteger(1);

        rentalDetails.forEach(detail -> {
            Row row = sheet.createRow(rowNum.getAndIncrement());
            row.setHeight((short) 400);

            createCell(row, 0, no.getAndIncrement(), null, null);
            createCell(row, 1, detail.getCategory(), thinBorderStyle, centeredStyle);
            createCell(row, 2, detail.getCompanyNm(), thinBorderStyle, centeredStyle);
            createCell(row, 3, detail.getContractNum(), thinBorderStyle, centeredStyle);
            createCell(row, 4, detail.getModelNm(), thinBorderStyle, centeredStyle);
            createCell(row, 5, detail.getInstallDate(), thinBorderStyle, centeredStyle);
            createCell(row, 6, detail.getExpiryDate(), thinBorderStyle, centeredStyle);

            double rentalFee = 0.0;
            try {
                rentalFee = Double.parseDouble(detail.getRentalFee().replace(",", ""));
            } catch (NumberFormatException e) {
                rentalFee = 0.0;
            }

            createCell(row, 7, rentalFee, thinBorderStyle, centeredStyle);
            createCell(row, 8, detail.getLocation(), thinBorderStyle, centeredStyle);
            createCell(row, 9, detail.getInstallationSite(), thinBorderStyle, centeredStyle);
            createCell(row, 10, detail.getSpecialNote(), thinBorderStyle, centeredStyle);
        });

        // 열 너비 조정
        adjustColumnWidths(sheet);

        // 새로운 표 추가
        addSummaryTable(sheet, rentalDetails, rowNum.get(), headerStyle, thinBorderStyle, centeredStyle);

        // 엑셀 파일을 ByteArrayOutputStream에 쓰기
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        wb.write(baos);
        return baos.toByteArray();
    }

    private void addSummaryTable(Sheet sheet, List<RentalDetail> rentalDetails, int startRow, CellStyle headerStyle, CellStyle thinBorderStyle, CellStyle centeredStyle) {
        Row headerRow = sheet.createRow(startRow + 2);
        headerRow.setHeight((short) 400);

        createCell(headerRow, 1, "항목", headerStyle, null);
        createCell(headerRow, 2, "수량", headerStyle, null);
        createCell(headerRow, 3, "금액", headerStyle, null);

        Map<String, List<RentalDetail>> groupedByCategory = rentalDetails.stream()
                .collect(Collectors.groupingBy(RentalDetail::getCategory));

        AtomicInteger rowNum = new AtomicInteger(startRow + 3);

        groupedByCategory.forEach((category, details) -> {
            int quantity = details.size();
            double totalAmount = details.stream()
                    .mapToDouble(detail -> {
                        try {
                            return Double.parseDouble(detail.getRentalFee().replace(",", ""));
                        } catch (NumberFormatException e) {
                            return 0.0;
                        }
                    })
                    .sum();

            Row row = sheet.createRow(rowNum.getAndIncrement());
            row.setHeight((short) 400);

            createCell(row, 1, category, thinBorderStyle, centeredStyle);
            createCell(row, 2, quantity, thinBorderStyle, centeredStyle);

            String formattedAmount = String.format("%,.0f", totalAmount);
            createCell(row, 3, formattedAmount, thinBorderStyle, centeredStyle);
        });

        adjustSummaryTableColumnWidths(sheet);
    }

    private void adjustSummaryTableColumnWidths(Sheet sheet) {
        sheet.setColumnWidth(12, 5000);
        sheet.setColumnWidth(13, 4000);
        sheet.setColumnWidth(14, 6000);
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
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else {
            cell.setCellValue(value.toString());
        }

        cell.setCellStyle(centeredStyle);
        if (borderStyle != null) {
            cell.setCellStyle(borderStyle);
        }
    }

    private void createTitle(Sheet sheet, CellStyle titleStyle) {
        Row titleRow = sheet.createRow(1);
        titleRow.setHeight((short) 800);

        sheet.addMergedRegion(new CellRangeAddress(1, 1, 1, 10));

        for (int i = 1; i <= 10; i++) {
            Cell cell = titleRow.createCell(i);
            cell.setCellValue(i == 1 ? "재단본부 렌탈제품 현황" : "");
            CellStyle combinedStyle = sheet.getWorkbook().createCellStyle();
            combinedStyle.cloneStyleFrom(titleStyle);
            combinedStyle.setBorderBottom(BorderStyle.THIN);
            combinedStyle.setBorderTop(BorderStyle.THIN);
            cell.setCellStyle(combinedStyle);
        }
    }

    private void createHeader(Sheet sheet, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(4);
        headerRow.setHeight((short) 400);

        createCell(headerRow, 1, "제품군", headerStyle, null);
        createCell(headerRow, 2, "업체명", headerStyle, null);
        createCell(headerRow, 3, "계약번호", headerStyle, null);
        createCell(headerRow, 4, "모델명", headerStyle, null);
        createCell(headerRow, 5, "설치일자", headerStyle, null);
        createCell(headerRow, 6, "만료일자", headerStyle, null);
        createCell(headerRow, 7, "렌탈료", headerStyle, null);
        createCell(headerRow, 8, "위치분류", headerStyle, null);
        createCell(headerRow, 9, "설치위치", headerStyle, null);
        createCell(headerRow, 10, "특이사항", headerStyle, null);
    }

    private void adjustColumnWidths(Sheet sheet) {
        sheet.setColumnWidth(0, 1000);
        sheet.setColumnWidth(1, 5000);
        sheet.setColumnWidth(2, 5000);
        sheet.setColumnWidth(3, 5000);
        sheet.setColumnWidth(4, 7000);
        sheet.setColumnWidth(5, 5000);
        sheet.setColumnWidth(6, 5000);
        sheet.setColumnWidth(7, 5000);
        sheet.setColumnWidth(8, 5000);
        sheet.setColumnWidth(9, 15000);
        sheet.setColumnWidth(10, 10000);
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
        XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        XSSFColor color = new XSSFColor(new java.awt.Color(255, 242, 204), null);
        style.setFillForegroundColor(color);
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
