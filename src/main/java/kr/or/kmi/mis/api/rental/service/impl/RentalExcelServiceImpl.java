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
import java.time.LocalDateTime;
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

            if (dto.getCategory() == null || dto.getCategory().isEmpty()) {
                throw new IllegalArgumentException("카테고리가 비어 있습니다: " + dto);
            }
            if (dto.getCompanyNm() == null || dto.getCompanyNm().isEmpty()) {
                throw new IllegalArgumentException("업체명이 비어 있습니다: " + dto);
            }
            if (dto.getContractNum() == null || dto.getContractNum().isEmpty()) {
                throw new IllegalArgumentException("계약번호가 비어 있습니다: " + dto);
            }
            if (dto.getModelNm() == null || dto.getModelNm().isEmpty()) {
                throw new IllegalArgumentException("모델명이 비어 있습니다: " + dto);
            }
            if (dto.getInstallDate() == null) {
                throw new IllegalArgumentException("설치일자가 비어 있습니다: " + dto);
            }
            if (dto.getExpiryDate() == null) {
                throw new IllegalArgumentException("만료일자가 비어 있습니다: " + dto);
            }
            if (dto.getRentalFee() == null) {
                throw new IllegalArgumentException("렌탈료가 비어 있습니다: " + dto);
            }
            if (dto.getLocation() == null || dto.getLocation().isEmpty()) {
                throw new IllegalArgumentException("위치분류가 비어 있습니다: " + dto);
            }
            if (dto.getInstallationSite() == null || dto.getInstallationSite().isEmpty()) {
                throw new IllegalArgumentException("설치위치가 비어 있습니다: " + dto);
            }

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
            existingDetail.setUpdtDt(LocalDateTime.now());

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
            createCell(row, 1, detail.getCompanyNm(), thinBorderStyle, centeredStyle);
            createCell(row, 2, detail.getCategory(), thinBorderStyle, centeredStyle);
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

            // 세 자리마다 콤마 추가하여 문자열로 포맷
            String formattedRentalFee = String.format("%,.0f", rentalFee);

            createCell(row, 7, formattedRentalFee, thinBorderStyle, centeredStyle);
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

    @Override
    public byte[] generateTotalExcel() throws IOException {
        Workbook wb = new XSSFWorkbook();
        try {
            // 1. 전국센터 시트 생성
            createSummarySheet(wb);

            // 2. 각 센터별 시트 생성
            createCenterSheets(wb);

            // 엑셀 파일을 ByteArrayOutputStream에 쓰기
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            wb.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new IOException("Error generating Excel file", e);
        } finally {
            wb.close();
        }
    }

    private void createSummarySheet(Workbook wb) {
        Sheet sheet = wb.createSheet("전국센터");

        // 스타일 설정
        CellStyle headerStyle = createHeaderStyle(wb);
        CellStyle thinBorderStyle = createThinBorderStyle(wb);
        CellStyle titleStyle = createTitleStyle(wb);
        CellStyle centeredStyle = createCenteredStyle(wb);
        CellStyle centerNameStyle = createCenterNameStyle(wb);

        // 제목 생성
        createSumTitle(sheet, titleStyle);

        // 병합된 헤더 생성
        createSummaryHeader(sheet, headerStyle);

        // 센터명 배열
        String[] centers = {"재단본부", "본원센터", "광화문", "강남센터", "여의도센터", "수원센터", "대구센터", "부산센터", "광주센터", "제주센터"};

        // 데이터 채우기
        int waterPurifierTotal = 0;
        int airPurifierTotal = 0;
        int bidetTotal = 0;
        double rentalFeeTotal = 0.0;

        for (int i = 0; i < centers.length; i++) {
            Row row = sheet.createRow(5 + i);
            row.setHeight((short) 600);
            createCell(row, 1, centers[i], centerNameStyle, centeredStyle);

            // 각 센터의 데이터를 불러오기 (정수기, 공기청정기, 비데의 수 및 월 렌탈금액)
            int[] data = fetchRentalDataForSummary(centers[i]);
            waterPurifierTotal += data[0];
            airPurifierTotal += data[1];
            bidetTotal += data[2];
            rentalFeeTotal += data[3];

            createCell(row, 2, data[0], thinBorderStyle, centeredStyle);
            createCell(row, 3, data[1], thinBorderStyle, centeredStyle);
            createCell(row, 4, data[2], thinBorderStyle, centeredStyle);
            createCell(row, 5, String.format("%,.0f", (double) data[3]), thinBorderStyle, centeredStyle);
        }

        Row totalRow = sheet.createRow(5 + centers.length);
        totalRow.setHeight((short) 600);
        createCell(totalRow, 1, "합계", centerNameStyle, centeredStyle);
        createCell(totalRow, 2, waterPurifierTotal, thinBorderStyle, centeredStyle);
        createCell(totalRow, 3, airPurifierTotal, thinBorderStyle, centeredStyle);
        createCell(totalRow, 4, bidetTotal, thinBorderStyle, centeredStyle);
        createCell(totalRow, 5, String.format("%,.0f", rentalFeeTotal), thinBorderStyle, centeredStyle);

        adjustSummaryColumnWidths(sheet);
    }

    private void createCenterSheets(Workbook wb) {
        String[] centers = {"재단본부", "본원센터", "광화문", "강남센터", "여의도센터", "수원센터", "대구센터", "부산센터", "광주센터", "제주센터"};
        for (String center : centers) {
            List<RentalDetail> rentalDetails = fetchRentalDetailsByCenter(center);
            createCenterSheet(wb, center, rentalDetails);
        }
    }

    private void createCenterSheet(Workbook wb, String sheetName, List<RentalDetail> rentalDetails) {
        Sheet sheet = wb.createSheet(sheetName);

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
            createCell(row, 1, detail.getCompanyNm(), thinBorderStyle, centeredStyle);
            createCell(row, 2, detail.getCategory(), thinBorderStyle, centeredStyle);
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

            // 세 자리마다 콤마 추가하여 문자열로 포맷
            String formattedRentalFee = String.format("%,.0f", rentalFee);

            createCell(row, 7, formattedRentalFee, thinBorderStyle, centeredStyle);
            createCell(row, 8, detail.getLocation(), thinBorderStyle, centeredStyle);
            createCell(row, 9, detail.getInstallationSite(), thinBorderStyle, centeredStyle);
            createCell(row, 10, detail.getSpecialNote(), thinBorderStyle, centeredStyle);
        });

        // 열 너비 조정
        adjustColumnWidths(sheet);
    }

    private int[] fetchRentalDataForSummary(String centerName) {
        // 각 센터별 데이터를 불러와서 [정수기 수, 공기청정기 수, 비데 수, 월 렌탈금액] 배열로 반환
        List<RentalDetail> rentalDetails = fetchRentalDetailsByCenter(centerName);
        int waterPurifierCount = 0;
        int airPurifierCount = 0;
        int bidetCount = 0;
        double totalRentalFee = 0.0;

        for (RentalDetail detail : rentalDetails) {
            switch (detail.getCategory()) {
                case "정수기":
                    waterPurifierCount++;
                    break;
                case "공기청정기":
                    airPurifierCount++;
                    break;
                case "비데":
                    bidetCount++;
                    break;
            }
            try {
                totalRentalFee += Double.parseDouble(detail.getRentalFee().replace(",", ""));
            } catch (NumberFormatException e) {
                totalRentalFee += 0.0;
            }
        }

        return new int[]{waterPurifierCount, airPurifierCount, bidetCount, (int) totalRentalFee};
    }

    private List<RentalDetail> fetchRentalDetailsByCenter(String centerName) {
        String instCd = convertCenterNameToInstCd(centerName);
        return rentalDetailRepository.findByInstCdAndStatus(instCd, "E")
                .orElseThrow(() -> new IllegalArgumentException("No rental data found for center: " + centerName));
    }

    private String convertCenterNameToInstCd(String centerName) {
        switch (centerName) {
            case "재단본부":
                return "100";
            case "본원센터":
                return "111";
            case "광화문":
                return "119";
            case "강남센터":
                return "113";
            case "여의도센터":
                return "112";
            case "수원센터":
                return "211";
            case "대구센터":
                return "611";
            case "부산센터":
                return "612";
            case "광주센터":
                return "711";
            case "제주센터":
                return "811";
            default:
                throw new IllegalArgumentException("Unknown center name: " + centerName);
        }
    }

    private void createSummaryHeader(Sheet sheet, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(4);
        headerRow.setHeight((short) 600);

        createCell(headerRow, 1, "센터명", headerStyle, null);
        createCell(headerRow, 2, "정수기", headerStyle, null);
        createCell(headerRow, 3, "공기청정기", headerStyle, null);
        createCell(headerRow, 4, "비데", headerStyle, null);
        createCell(headerRow, 5, "월 렌탈금액", headerStyle, null);
    }

    private void adjustSummaryColumnWidths(Sheet sheet) {
        sheet.setColumnWidth(1, 5000);
        sheet.setColumnWidth(2, 5000);
        sheet.setColumnWidth(3, 5000);
        sheet.setColumnWidth(4, 5000);
        sheet.setColumnWidth(5, 5000);
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

    private void createSumTitle(Sheet sheet, CellStyle titleStyle) {
        Row titleRow = sheet.createRow(1);
        titleRow.setHeight((short) 700);

        sheet.addMergedRegion(new CellRangeAddress(1, 1, 1, 5));

        for (int i = 1; i <= 5; i++) {
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

        createCell(headerRow, 1, "업체명", headerStyle, null);
        createCell(headerRow, 2, "제품군", headerStyle, null);
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

    private CellStyle createCenterNameStyle(Workbook wb) {
        XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle();
        XSSFColor color = new XSSFColor(new java.awt.Color(217, 225, 242), null);
        style.setFillForegroundColor(color);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        return style;
    }
}
