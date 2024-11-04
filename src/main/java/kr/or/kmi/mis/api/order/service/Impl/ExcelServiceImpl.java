package kr.or.kmi.mis.api.order.service.Impl;

import jakarta.servlet.http.HttpServletResponse;
import kr.or.kmi.mis.api.bcd.model.entity.BcdDetail;
import kr.or.kmi.mis.api.bcd.repository.BcdDetailRepository;
import kr.or.kmi.mis.api.order.service.ExcelService;
import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import kr.or.kmi.mis.api.std.repository.StdDetailRepository;
import kr.or.kmi.mis.api.std.repository.StdGroupRepository;
import kr.or.kmi.mis.api.std.service.StdBcdService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionMode;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExcelServiceImpl implements ExcelService {

    private final BcdDetailRepository bcdDetailRepository;
    private final StdBcdService stdBcdService;
    private final StdGroupRepository stdGroupRepository;
    private final StdDetailRepository stdDetailRepository;

    @Override
    public void downloadExcel(HttpServletResponse response, List<String> draftIds) throws IOException {
        byte[] excelData = generateExcel(draftIds);

        sendFileToResponse(response, excelData, "order_details.xlsx");
    }

    @Override
    public void downloadOrderExcel(HttpServletResponse response, List<String> draftIds, String instCd) throws IOException {
        byte[] excelData = generateOrderExcel(draftIds, instCd);

        sendFileToResponse(response, excelData, "order_details.xlsx");
    }

    private void sendFileToResponse(HttpServletResponse response, byte[] fileData, String fileName) throws IOException {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            response.setContentLength(fileData.length);
            response.getOutputStream().write(fileData);
            response.getOutputStream().flush();
            response.getOutputStream().close();
        } catch (Exception e) {
            throw new IOException("Failed to send file: " + fileName, e);
        }
    }

    public byte[] getEncryptedExcelBytes(byte[] excelData, String password) throws IOException, GeneralSecurityException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        POIFSFileSystem fs = new POIFSFileSystem();
        EncryptionInfo info = new EncryptionInfo(EncryptionMode.standard);
        Encryptor encryptor = info.getEncryptor();
        encryptor.confirmPassword(password);

        try (ByteArrayInputStream bais = new ByteArrayInputStream(excelData);
             Workbook wb = new XSSFWorkbook(bais);
             OutputStream os = encryptor.getDataStream(fs)) {
            wb.write(os);
        }

        fs.writeFilesystem(bos);
        return bos.toByteArray();
    }

    @Override
    public byte[] generateExcel(List<String> draftIds) throws IOException {
        List<BcdDetail> bcdDetails = bcdDetailRepository.findAllByDraftIdIn(draftIds);
        return createExcelData(bcdDetails);
    }

    @Override
    public byte[] generateOrderExcel(List<String> draftIds, String instCd) throws IOException {
        List<BcdDetail> bcdDetails = bcdDetailRepository.findAllByDraftIdIn(draftIds);
        return createOrderExcelData(bcdDetails, instCd);
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
            row.setHeight((short) 600);

            // Division 값에 따른 조건 처리
            String divisionValue = "기타";
            if ("A".equals(detail.getDivision())) {
                divisionValue = "1안";
            } else if ("B".equals(detail.getDivision())) {
                divisionValue = "2안";
            }

            String teamNm = "000".equals(detail.getTeamCd())
                    ? detail.getTeamNm()
                    : stdBcdService.getTeamNm(detail.getTeamCd()).getFirst();

            String gradeNm = "000".equals(detail.getGradeCd())
                    ?detail.getGradeNm()
                    : stdBcdService.getGradeNm(detail.getGradeCd()).getFirst();

            // 앞면 정보
            createCell(row, 0, no.getAndIncrement(), thickBorderStyle, centeredStyle);
            createCell(row, 1, divisionValue, thinBorderStyle, centeredStyle);
            createCell(row, 2, stdBcdService.getInstNm(detail.getInstCd()), thinBorderStyle, centeredStyle);
            createCell(row, 3, teamNm, thinBorderStyle, centeredStyle);
            createCell(row, 4, gradeNm, thinBorderStyle, centeredStyle);
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

            String engTeamNm = "000".equals(detail.getTeamCd())
                    ? detail.getEngteamNm()
                    : stdBcdService.getTeamNm(detail.getTeamCd()).getLast();

            String enGradeNm = "000".equals(detail.getGradeCd())
                    ?detail.getEngradeNm()
                    : stdBcdService.getTeamNm(detail.getTeamCd()).getLast();

            createCell(rowBack, 0, "", thickBorderStyle, centeredStyle);
            createCell(rowBack, 1, "", thinBorderStyle, centeredStyle);
            if ("000".equals(detail.getGradeCd()) && "000".equals(detail.getTeamCd())) {
                createCell(rowBack, 2, enGradeNm + " - " + engTeamNm, thinBorderStyle, centeredStyle);
            }
            else if ("000".equals(detail.getGradeCd())) {
                createCell(rowBack, 2, enGradeNm + " - " + stdBcdService.getTeamNm(detail.getTeamCd()).getLast(), thinBorderStyle, centeredStyle);
            }
            else if ("000".equals(detail.getTeamCd())) {
                createCell(rowBack, 2, stdBcdService.getGradeNm(detail.getGradeCd()).getLast() + " - " + engTeamNm, thinBorderStyle, centeredStyle);
            }
            else {
                createCell(rowBack, 2, stdBcdService.getGradeNm(detail.getGradeCd()).getLast() + " - " + stdBcdService.getTeamNm(detail.getTeamCd()).getLast(), thinBorderStyle, centeredStyle);
            }
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
        sheet.setColumnWidth(2, 4500);
        sheet.setColumnWidth(3, 5500);
        sheet.setColumnWidth(4, 5500);
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
        String[] headers = {"No", "시안", "센터", "소속", "직위", "성명", "영문", "TEL", "FAX", "H.P", "E-mail", "수량", "주소"};

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

    private byte[] createOrderExcelData(List<BcdDetail> bcdDetails, String instCd) throws IOException {

        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("명함 완료내역");

        // 스타일 설정
        CellStyle headerStyle = createHeaderStyle(wb);
        CellStyle thinBorderStyle = createThinBorderStyle(wb);
        CellStyle titleStyle = createTitleStyle(wb);
        CellStyle centeredStyle = createCenteredStyle(wb);
        CellStyle sumCenterStyle = createSumCenterStyle(wb);

        // 첫 번째 테이블: 명함 완료내역
        createOrderTitle(sheet, titleStyle);
        createOrderHeader(sheet, headerStyle);

        AtomicInteger rowNum = new AtomicInteger(2);
        AtomicInteger no = new AtomicInteger(1);

        // 필터링된 데이터로 테이블 생성
        bcdDetails.forEach(detail -> {
            Row row = sheet.createRow(rowNum.getAndIncrement());
            row.setHeight((short) 600);

            String teamNm = "000".equals(detail.getTeamCd())
                    ? detail.getTeamNm()
                    : stdBcdService.getTeamNm(detail.getTeamCd()).getFirst();

            String gradeNm = "000".equals(detail.getGradeCd())
                    ? detail.getGradeNm()
                    : stdBcdService.getGradeNm(detail.getGradeCd()).getFirst();

            createOrderCell(row, 0, no.getAndIncrement(), thinBorderStyle, centeredStyle);
            createOrderCell(row, 1, stdBcdService.getInstNm(detail.getInstCd()), thinBorderStyle, centeredStyle);
            createOrderCell(row, 2, teamNm, thinBorderStyle, centeredStyle);
            createOrderCell(row, 3, gradeNm, thinBorderStyle, centeredStyle);
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

        // 합계 행 추가
        int newTableStartRow = rowNum.get();

        Row mergedRow = sheet.createRow(newTableStartRow+1);
        Cell mergedCell = mergedRow.createCell(0);
        mergedCell.setCellValue("발주 건수 및 수량");
        mergedCell.setCellStyle(sumCenterStyle);

        // 센터별 합계 계산
        int count = 0;
        int quantity = 0;
        int amount = 0;

        for (BcdDetail detail : bcdDetails) {
            count++;
            quantity += detail.getQuantity();
            amount += detail.getQuantity() * 13000;
        }

        // 합계 행 추가
        Row sumRow = sheet.createRow(++newTableStartRow);
        sumRow.setHeight((short) 500);

        StdGroup stdGroup = stdGroupRepository.findByGroupCd("A001")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        StdDetail stdDetail = stdDetailRepository.findByGroupCdAndDetailCd(stdGroup, instCd)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        createSumCell(sumRow, 1, stdDetail.getDetailNm(), thinBorderStyle, sumCenterStyle);
        createSumCell(sumRow, 2, count + "건", thinBorderStyle, sumCenterStyle);
        createSumCell(sumRow, 3, quantity + "통", thinBorderStyle, sumCenterStyle);
        createSumCell(sumRow, 4, amount + "원", thinBorderStyle, sumCenterStyle);

        sheet.setColumnWidth(1, 4000);
        sheet.setColumnWidth(2, 5000);
        sheet.setColumnWidth(3, 5000);
        sheet.setColumnWidth(4, 5000);

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
        cell.setCellValue("명함 완료내역");
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
