package kr.or.kmi.mis.api.order.service.Impl;

import jakarta.servlet.http.HttpServletResponse;
import kr.or.kmi.mis.api.bcd.model.entity.BcdDetail;
import kr.or.kmi.mis.api.bcd.repository.BcdDetailRepository;
import kr.or.kmi.mis.api.order.service.ExcelService;
import kr.or.kmi.mis.api.std.service.StdBcdService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExcelServiceImpl implements ExcelService {

    private final BcdDetailRepository bcdDetailRepository;
    private final StdBcdService stdBcdService;

    @Override
    public void downloadExcel(HttpServletResponse response, List<Long> draftIds) throws IOException {
        byte[] excelData = generateExcel(draftIds);

        // HTTP 응답에 엑셀 파일 첨부
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=order_details.xlsx");
        response.setContentLength(excelData.length);
        response.getOutputStream().write(excelData);
        response.getOutputStream().flush();
        response.getOutputStream().close();
    }

    @Override
    public void downloadOrderExcel(HttpServletResponse response, List<Long> draftIds) throws IOException {
        byte[] excelData = generateOrderExcel(draftIds);

        // HTTP 응답에 엑셀 파일 첨부
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=order_details.xlsx");
        response.setContentLength(excelData.length);
        response.getOutputStream().write(excelData);
        response.getOutputStream().flush();
        response.getOutputStream().close();
    }

    @Override
    public byte[] generateExcel(List<Long> draftIds) throws IOException {
        // draftIds 리스트 내의 draftId에 해당하는 각각의 정보 불러오기
        List<BcdDetail> bcdDetails = bcdDetailRepository.findAllByDraftIdIn(draftIds);
        return createExcelData(bcdDetails);
    }

    @Override
    public byte[] generateOrderExcel(List<Long> draftIds) throws IOException {
        // draftIds 리스트 내의 draftId에 해당하는 각각의 정보 불러오기
        List<BcdDetail> bcdDetails = bcdDetailRepository.findAllByDraftIdIn(draftIds);
        return createOrderExcelData(bcdDetails);
    }

    private byte[] createExcelData(List<BcdDetail> bcdDetails) throws IOException {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("명함 신청 상세정보");

        // 스타일 설정
        CellStyle headerStyle = createHeaderStyle(wb);
        CellStyle thinBorderStyle = createThinBorderStyle(wb);
        CellStyle titleStyle = createTitleStyle(wb);
        CellStyle centeredStyle = createCenteredStyle(wb);
        CellStyle thickBorderStyle = createThickBorderStyle(wb);

        // 제목 생성
        createTitle(sheet, titleStyle);

        // 헤더 생성
        createHeader(sheet, headerStyle);

        // 데이터 채우기
        AtomicInteger rowNum = new AtomicInteger(2);
        AtomicInteger no = new AtomicInteger(1);

        bcdDetails.forEach(detail -> {
            Row row = sheet.createRow(rowNum.getAndIncrement());
            row.setHeight((short) 600); // 세로 너비 설정

            // Division 값에 따른 조건 처리
            String divisionValue = "기타"; // 기본값 설정
            if ("A".equals(detail.getDivision())) {
                divisionValue = "1안";
            } else if ("B".equals(detail.getDivision())) {
                divisionValue = "2안";
            }

            // 앞면 정보
            createCell(row, 0, no.getAndIncrement(), thickBorderStyle, centeredStyle);
            createCell(row, 1, divisionValue, thinBorderStyle, centeredStyle);
            createCell(row, 2, stdBcdService.getInstNm(detail.getInstCd()), thinBorderStyle, centeredStyle);
            createCell(row, 3, stdBcdService.getTeamNm(detail.getTeamCd()).getFirst(), thinBorderStyle, centeredStyle);
            createCell(row, 4, stdBcdService.getGradeNm(detail.getGradeCd()).getFirst(), thinBorderStyle, centeredStyle);
            createCell(row, 5, detail.getKorNm(), thinBorderStyle, centeredStyle);
            createCell(row, 6, detail.getEngNm(), thinBorderStyle, centeredStyle);
            createCell(row, 7, formatPhoneNumber(detail.getExtTel(), false), thinBorderStyle, centeredStyle);
            createCell(row, 8, formatPhoneNumber(detail.getFaxTel(), false), thinBorderStyle, centeredStyle);
            createCell(row, 9, formatPhoneNumber(detail.getPhoneTel(), false), thinBorderStyle, centeredStyle);
            createCell(row, 10, detail.getEmail(), thinBorderStyle, centeredStyle);
            createCell(row, 11, detail.getQuantity(), thinBorderStyle, centeredStyle);
            createCell(row, 12, detail.getAddress(), thinBorderStyle, centeredStyle);

            // 병합할 행 조건 (짝수 행)
            if ((rowNum.get() - 1) % 2 == 0) {
                sheet.addMergedRegion(new CellRangeAddress(rowNum.get(), rowNum.get(), 2, 4));
            }

            // 뒷면 정보
            Row rowBack = sheet.createRow(rowNum.getAndIncrement());
            rowBack.setHeight((short) 600); // 세로 너비 설정

            createCell(rowBack, 0, "", thickBorderStyle, centeredStyle);
            createCell(rowBack, 1, "", thinBorderStyle, centeredStyle);
            createCell(rowBack, 2, stdBcdService.getGradeNm(
                            detail.getGradeCd()).getLast() + " - " + stdBcdService.getTeamNm(detail.getTeamCd()).getLast()
                    , thinBorderStyle, centeredStyle);
            createCell(rowBack, 3, "", thinBorderStyle, centeredStyle);
            createCell(rowBack, 4, "", thinBorderStyle, centeredStyle);
            createCell(rowBack, 5, detail.getEngNm(), thinBorderStyle, centeredStyle);
            createCell(rowBack, 6, "", thinBorderStyle, centeredStyle);
            createCell(rowBack, 7, formatPhoneNumber(detail.getExtTel(), true), thinBorderStyle, centeredStyle);
            createCell(rowBack, 8, formatPhoneNumber(detail.getFaxTel(), true), thinBorderStyle, centeredStyle);
            createCell(rowBack, 9, formatPhoneNumber(detail.getPhoneTel(), true), thinBorderStyle, centeredStyle);
            createCell(rowBack, 10, detail.getEmail(), thinBorderStyle, centeredStyle);
            createCell(rowBack, 11, "", thinBorderStyle, centeredStyle);
            createCell(rowBack, 12, detail.getEngAddress(), thinBorderStyle, centeredStyle);

            sheet.addMergedRegion(new CellRangeAddress(rowNum.get() - 2, rowNum.get() - 1, 0, 0));
            sheet.addMergedRegion(new CellRangeAddress(rowNum.get() - 2, rowNum.get() - 1, 1, 1));
            sheet.addMergedRegion(new CellRangeAddress(rowNum.get() - 2, rowNum.get() - 1, 11, 11));
        });

        // 열 너비 조정
        sheet.setColumnWidth(0, 1500);
        sheet.setColumnWidth(1, 2000);
        sheet.setColumnWidth(2, 4000);
        sheet.setColumnWidth(3, 5000);
        sheet.setColumnWidth(4, 4000);
        sheet.setColumnWidth(5, 5000);
        sheet.setColumnWidth(6, 5000);
        sheet.setColumnWidth(7, 5800);
        sheet.setColumnWidth(8, 5800);
        sheet.setColumnWidth(9, 5800);
        sheet.setColumnWidth(10, 7000);
        sheet.setColumnWidth(11, 2000);
        sheet.setColumnWidth(12, 20000);

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
        cell.setCellValue("명함신청");
        cell.setCellStyle(style);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));
    }

    private void createHeader(Sheet sheet, CellStyle style) {
        Row headerRow = sheet.createRow(1);
        headerRow.setHeight((short) 700);
        String[] headers = {"No", "사안", "센터", "소속", "직위", "성명", "영문", "TEL", "FAX", "H.P", "E-mail", "수량", "주소"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
    }

    private void createCell(Row row, int column, Object value, CellStyle borderStyle, CellStyle centeredStyle) {
        Cell cell = row.createCell(column);
        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        }
        cell.setCellStyle(centeredStyle);
        if (borderStyle != null) {
            cell.setCellStyle(borderStyle);
        }
    }

    private byte[] createOrderExcelData(List<BcdDetail> bcdDetails) throws IOException {

        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("명함 발주내역");

        // 스타일 설정
        CellStyle headerStyle = createHeaderStyle(wb);
        CellStyle thinBorderStyle = createThinBorderStyle(wb);
        CellStyle titleStyle = createTitleStyle(wb);
        CellStyle centeredStyle = createCenteredStyle(wb);
        CellStyle sumCenterStyle = createSumCenterStyle(wb);

        // 첫 번째 테이블: 명함 발주내역
        createOrderTitle(sheet, titleStyle);
        createOrderHeader(sheet, headerStyle);

        AtomicInteger rowNum = new AtomicInteger(2); // 첫 번째 데이터 행 번호
        AtomicInteger no = new AtomicInteger(1); // 순번 초기화

        bcdDetails.forEach(detail -> {
            Row row = sheet.createRow(rowNum.getAndIncrement());
            row.setHeight((short) 800);

            createOrderCell(row, 0, no.getAndIncrement(), thinBorderStyle, centeredStyle);
            createOrderCell(row, 1, stdBcdService.getInstNm(detail.getInstCd()), thinBorderStyle, centeredStyle);
            createOrderCell(row, 2, stdBcdService.getTeamNm(detail.getTeamCd()).getFirst(), thinBorderStyle, centeredStyle);
            createOrderCell(row, 3, stdBcdService.getGradeNm(detail.getGradeCd()).getFirst(), thinBorderStyle, centeredStyle);
            createOrderCell(row, 4, detail.getKorNm(), thinBorderStyle, centeredStyle);
            createOrderCell(row, 5, detail.getEngNm(), thinBorderStyle, centeredStyle);
            createOrderCell(row, 6, formatPhoneNumber(detail.getExtTel(), false), thinBorderStyle, centeredStyle);
            createOrderCell(row, 7, formatPhoneNumber(detail.getFaxTel(), false), thinBorderStyle, centeredStyle);
            createOrderCell(row, 8, formatPhoneNumber(detail.getPhoneTel(), false), thinBorderStyle, centeredStyle);
            createOrderCell(row, 9, detail.getEmail(), thinBorderStyle, centeredStyle);
            createOrderCell(row, 10, detail.getQuantity(), thinBorderStyle, centeredStyle);
        });

        // 열 너비 조정
        sheet.setColumnWidth(0, 1500);
        sheet.setColumnWidth(1, 4000);
        sheet.setColumnWidth(2, 5000);
        sheet.setColumnWidth(3, 4000);
        sheet.setColumnWidth(4, 5000);
        sheet.setColumnWidth(5, 5000);
        sheet.setColumnWidth(6, 5800);
        sheet.setColumnWidth(7, 5800);
        sheet.setColumnWidth(8, 5800);
        sheet.setColumnWidth(9, 7000);
        sheet.setColumnWidth(10, 2000);

        int newTableStartRow = rowNum.get();

        sheet.addMergedRegion(new CellRangeAddress(newTableStartRow, newTableStartRow + 11, 0, 2));
        Row mergedRow = sheet.createRow(newTableStartRow);
        Cell mergedCell = mergedRow.createCell(0);
        mergedCell.setCellValue("발주 건수 및 수량");
        mergedCell.setCellStyle(sumCenterStyle);

        String[] centers = {"재단본부", "광화문", "본원센터", "여의도센터", "강남센터", "수원센터", "대구센터", "부산센터", "광주센터", "제주센터", "합계"};
        int[] counts = new int[centers.length];
        int[] quantities = new int[centers.length];

        bcdDetails.forEach(detail -> {
            String instName = stdBcdService.getInstNm(detail.getInstCd());
            for (int i = 0; i < centers.length; i++) {
                if (instName.equals(centers[i])) {
                    counts[i]++;
                    quantities[i] += detail.getQuantity();
                }
            }
            counts[centers.length - 1]++;
            quantities[centers.length - 1] += detail.getQuantity();
        });

        for (int i = 0; i < centers.length; i++) {
            Row rowCount = sheet.createRow(++newTableStartRow);
            rowCount.setHeight((short) 500);
            createSumCell(rowCount, 3, centers[i], thinBorderStyle, sumCenterStyle); // 센터 이름
            createSumCell(rowCount, 4, counts[i] + "건", thinBorderStyle, sumCenterStyle); // 건수
            createSumCell(rowCount, 5, quantities[i] + "통", thinBorderStyle, sumCenterStyle); // 수량
        }

        sheet.setColumnWidth(3, 4000);
        sheet.setColumnWidth(4, 5000);
        sheet.setColumnWidth(5, 5000);

        // 엑셀 파일을 ByteArrayOutputStream에 쓰기
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        wb.write(baos);
        wb.close();

        return baos.toByteArray();
    }

    private void createSumCell(Row row, int column, Object value, CellStyle borderStyle, CellStyle centeredStyle) {
        Cell cell = row.createCell(column);
        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        }
        cell.setCellStyle(centeredStyle);
        if (borderStyle != null) {
            cell.setCellStyle(borderStyle);
        }
    }

    private void createOrderTitle(Sheet sheet, CellStyle style) {
        Row titleRow = sheet.createRow(0);
        titleRow.setHeight((short) 800);
        Cell cell = titleRow.createCell(0);
        cell.setCellValue("명함 발주내역");
        cell.setCellStyle(style);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));
    }

    private void createOrderHeader(Sheet sheet, CellStyle style) {
        Row headerRow = sheet.createRow(1);
        headerRow.setHeight((short) 700);
        String[] headers = {"No", "센터", "소속", "직위", "성명", "영문", "TEL", "FAX", "H.P", "E-mail", "수량"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
    }

    private void createOrderCell(Row row, int column, Object value, CellStyle borderStyle, CellStyle centeredStyle) {
        Cell cell = row.createCell(column);
        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        }
        cell.setCellStyle(centeredStyle);
        if (borderStyle != null) {
            cell.setCellStyle(borderStyle);
        }
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

    private CellStyle createSumCenterStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFont(font);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
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

    private CellStyle createSubTitleStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
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

    private String formatPhoneNumber(String phoneNumber, boolean isInternational) {
        if (phoneNumber == null) {
            return "";
        }
        if (isInternational) {
            return phoneNumber.replaceFirst("^0(\\d+)-(\\d+)-(\\d+)$", "+82.$1.$2.$3");
        } else {
            return phoneNumber.replaceFirst("^0(\\d+)-(\\d+)-(\\d+)$", "0$1-$2-$3");
        }
    }
}
