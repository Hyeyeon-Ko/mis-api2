package kr.or.kmi.mis.api.order.service.Impl;

import jakarta.servlet.http.HttpServletResponse;
import kr.or.kmi.mis.api.bcd.model.entity.BcdDetail;
import kr.or.kmi.mis.api.bcd.repository.BcdDetailRepository;
import kr.or.kmi.mis.api.order.service.ExcelService;
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
    public byte[] generateExcel(List<Long> draftIds) throws IOException {
        // draftIds 리스트 내의 draftId에 해당하는 각각의 정보 불러오기
        List<BcdDetail> bcdDetails = bcdDetailRepository.findAllByDraftIdIn(draftIds);
        return createExcelData(bcdDetails);
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

            // 앞면 정보
            createCell(row, 0, no.getAndIncrement(), thickBorderStyle, centeredStyle);
            // todo: 기준자료에서 해당 코드의 이름 불러오기
            createCell(row, 1, detail.getInstCd(), thinBorderStyle, centeredStyle);
            createCell(row, 2, detail.getTeamCd(), thinBorderStyle, centeredStyle);
            createCell(row, 3, detail.getGradeCd(), thinBorderStyle, centeredStyle);
            createCell(row, 4, detail.getKorNm(), thinBorderStyle, centeredStyle);
            createCell(row, 5, detail.getEngNm(), thinBorderStyle, centeredStyle);
            createCell(row, 6, formatPhoneNumber(detail.getExtTel(), false), thinBorderStyle, centeredStyle);
            createCell(row, 7, formatPhoneNumber(detail.getFaxTel(), false), thinBorderStyle, centeredStyle);
            createCell(row, 8, formatPhoneNumber(detail.getPhoneTel(), false), thinBorderStyle, centeredStyle);
            createCell(row, 9, detail.getEmail(), thinBorderStyle, centeredStyle);
            createCell(row, 10, detail.getQuantity(), thinBorderStyle, centeredStyle);
            createCell(row, 11, detail.getAddress(), thinBorderStyle, centeredStyle);

            // 병합할 행 조건 (짝수 행)
            if ((rowNum.get() - 1) % 2 == 0) {
                sheet.addMergedRegion(new CellRangeAddress(rowNum.get(), rowNum.get(), 1, 3));
            }

            // 뒷면 정보
            Row rowBack = sheet.createRow(rowNum.getAndIncrement());
            rowBack.setHeight((short) 600); // 세로 너비 설정

            createCell(rowBack, 0, "", thickBorderStyle, centeredStyle);
            // todo: 기준자료에서 해당 teamCd의 영어 이름 불러오기
            createCell(rowBack, 1, detail.getTeamCd(), thinBorderStyle, centeredStyle);
            createCell(rowBack, 2, "", thinBorderStyle, centeredStyle);
            createCell(rowBack, 3, "", thinBorderStyle, centeredStyle);
            createCell(rowBack, 4, detail.getEngNm(), thinBorderStyle, centeredStyle);
            createCell(rowBack, 5, "", thinBorderStyle, centeredStyle);
            createCell(rowBack, 6, formatPhoneNumber(detail.getExtTel(), true), thinBorderStyle, centeredStyle);
            createCell(rowBack, 7, formatPhoneNumber(detail.getFaxTel(), true), thinBorderStyle, centeredStyle);
            createCell(rowBack, 8, formatPhoneNumber(detail.getPhoneTel(), true), thinBorderStyle, centeredStyle);
            createCell(rowBack, 9, detail.getEmail(), thinBorderStyle, centeredStyle);
            createCell(rowBack, 10, "", thinBorderStyle, centeredStyle);
            createCell(rowBack, 11, detail.getEngAddress(), thinBorderStyle, centeredStyle);

            sheet.addMergedRegion(new CellRangeAddress(rowNum.get() - 2, rowNum.get() - 1, 0, 0));
            sheet.addMergedRegion(new CellRangeAddress(rowNum.get() - 2, rowNum.get() - 1, 10, 10));
            Cell quantityCell = row.getCell(10);
            quantityCell.setCellStyle(centeredStyle);
        });
        
        // 행 너비 조정
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
        sheet.setColumnWidth(10, 2300);
        sheet.setColumnWidth(11, 20000);

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
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 9));
    }

    private void createHeader(Sheet sheet, CellStyle style) {
        Row headerRow = sheet.createRow(1);
        headerRow.setHeight((short) 700); // 세로 너비 설정
        String[] headers = {"No", "센터", "소속", "직위", "성명", "영문", "TEL", "FAX", "H.P", "E-mail", "수량", "주소"};

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
