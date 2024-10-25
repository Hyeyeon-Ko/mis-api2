package kr.or.kmi.mis.api.toner.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import kr.or.kmi.mis.api.std.repository.StdDetailRepository;
import kr.or.kmi.mis.api.std.repository.StdGroupRepository;
import kr.or.kmi.mis.api.toner.model.entity.TonerDetail;
import kr.or.kmi.mis.api.toner.model.entity.TonerInfo;
import kr.or.kmi.mis.api.toner.model.entity.TonerPrice;
import kr.or.kmi.mis.api.toner.model.request.TonerOrderRequestDTO;
import kr.or.kmi.mis.api.toner.repository.TonerDetailRepository;
import kr.or.kmi.mis.api.toner.repository.TonerInfoRepository;
import kr.or.kmi.mis.api.toner.repository.TonerPriceRepository;
import kr.or.kmi.mis.api.toner.service.TonerExcelService;
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
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TonerExcelServiceImpl implements TonerExcelService {

    private final TonerPriceRepository tonerPriceRepository;
    private final TonerInfoRepository tonerInfoRepository;
    private final TonerDetailRepository tonerDetailRepository;
    private final StdGroupRepository stdGroupRepository;
    private final StdDetailRepository stdDetailRepository;

//    @Override
//    @Transactional
//    public void saveTonerDetails(TonerExcelRequestDTO tonerExcelRequestDTO) {
//
//        List<TonerInfo> entities = tonerExcelRequestDTO.getDetails().stream()
//                .map(dto -> tonerInfoRepository.findByModelNmAndTonerNm(dto.getModelNm(), dto.getTonerNm())
//                        .map(tonerInfo -> TonerInfo.builder()
//                                .price(dto.getPrice())
//                                .build())
//                        .orElse(null))
//                .filter(Objects::nonNull)
//                .toList();
//
//        tonerInfoRepository.saveAll(entities);
//    }

    @Override
    public void downloadPriceExcel(HttpServletResponse response, List<String> tonerNms) throws IOException {
        byte[] excelData = generatePriceExcel(tonerNms);
        sendFileToResponse(response, excelData, "토너 단가표.xlsx");
    }

    @Override
    public byte[] generatePriceExcel(List<String> tonerNms) throws IOException {
        List<TonerPrice> tonerPrices = tonerPriceRepository.findAllByTonerNmIn(tonerNms);
        return createPriceExcelData(tonerPrices);
    }

    @Override
    public void downloadManageExcel(HttpServletResponse response, List<String> mngNums) throws IOException {
        byte[] excelData = generateManageExcel(mngNums);
        sendFileToResponse(response, excelData, "토너 관리표.xlsx");
    }

    @Override
    public byte[] generateManageExcel(List<String> mngNums) throws IOException {
        List<TonerInfo> tonerInfos = tonerInfoRepository.findAllByMngNumIn(mngNums);
        return createManageExcelData(tonerInfos);
    }

    // TODO: 기안상신용 파일 형식 받은 후 구현
    @Override
    public void downloadPendingExcel(HttpServletResponse response, List<String> draftIds) throws IOException {
//        byte[] excelData = generatePendingExcel(draftIds);
//
//        sendFileToResponse(response, excelData, "토너 대기내역.xlsx");
    }

//    @Override
//    public byte[] generateExcel(List<String> draftIds) throws IOException {
//        List<TonerDetail> tonerDetails = tonerDetailRepository.findAllByDraftIdIn(draftIds);
//        return createExcelData(tonerDetails);
//    }
//
//    private byte[] createExcelData(List<TonerDetail> tonerDetails) throws IOException {
//    }

    @Override
    public void downloadOrderExcel(HttpServletResponse response, TonerOrderRequestDTO tonerOrderRequestDTO) throws IOException{
        byte[] excelData = generateOrderExcel(tonerOrderRequestDTO.getDraftIds(), tonerOrderRequestDTO.getInstCd());
        sendFileToResponse(response, excelData, "토너 발주내역.xlsx");
    }

    @Override
    public byte[] generateOrderExcel(List<String> draftIds, String instCd) throws IOException {
        List<TonerDetail> tonerDetails = tonerDetailRepository.findAllByDraftIdIn(draftIds);
        return createOrderExcelData(tonerDetails, instCd);
    }

    private void sendFileToResponse(HttpServletResponse response, byte[] fileData, String fileName) throws IOException {
        try {
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);

            // HTTP 응답에 엑셀 파일 첨부
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);
            response.setContentLength(fileData.length);
            response.getOutputStream().write(fileData);
            response.getOutputStream().flush();
            response.getOutputStream().close();
        } catch (Exception e) {
            throw new IOException("Failed to send Excel file", e);
        }
    }

    private byte[] createPriceExcelData(List<TonerPrice> tonerPrices) throws IOException {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("토너 단가표");

        // 스타일 설정
        CellStyle headerStyle = createHeaderStyle(wb);
        CellStyle thinBorderStyle = createThinBorderStyle(wb);
        CellStyle titleStyle = createTitleStyle(wb);
        CellStyle centeredStyle = createCenteredStyle(wb);
        CellStyle thickBorderStyle = createThickBorderStyle(wb);

        // 제목 생성
        createPriceTitle(sheet, titleStyle, "토너 발주 내역");

        // 헤더 생성
        createPriceHeader(sheet, headerStyle);

        createThickTopBorderForRow(sheet, 4, thickBorderStyle);

        // 데이터 채우기
        AtomicInteger rowNum = new AtomicInteger(6);
        AtomicInteger no = new AtomicInteger(1);

        tonerPrices.forEach(price -> {

            StdGroup stdGroup = stdGroupRepository.findByGroupCd("A009")
                    .orElseThrow(() -> new IllegalArgumentException("Not Found"));

            StdDetail stdDetail = stdDetailRepository.findByGroupCdAndDetailCd(stdGroup, price.getDivision())
                    .orElseThrow(() -> new IllegalArgumentException("Not Found"));

            Row row = sheet.createRow(rowNum.getAndIncrement());
            row.setHeight((short) 350);

            createCell(row, 0, no.getAndIncrement(), thinBorderStyle, centeredStyle); // 번 호
            createCell(row, 1, price.getCompany(), thinBorderStyle, centeredStyle); // 제조사
            createCell(row, 2, price.getModelNm(), thinBorderStyle, centeredStyle); // 프린터명
            createCell(row, 3, price.getTonerNm(), thinBorderStyle, centeredStyle);   // 토너명
            createCell(row, 4, stdDetail.getDetailNm(), thinBorderStyle, centeredStyle);    // 구분
            createCell(row, 5, price.getPrice(), thinBorderStyle, centeredStyle);   // 단가
            createCell(row, 6, price.getSpecialNote(), thinBorderStyle, centeredStyle);     // 비고
        });

        // 열 너비 조정
        sheet.setColumnWidth(0, 3000);  // 번호
        sheet.setColumnWidth(1, 5000);  // 제조사
        sheet.setColumnWidth(2, 6000);  // 프린터명
        sheet.setColumnWidth(3, 6000);  // 토너/잉크/드럼명
        sheet.setColumnWidth(4, 3000);  // 구분
        sheet.setColumnWidth(5, 4000);  // 단가
        sheet.setColumnWidth(6, 5000);  // 비고

        // 엑셀 파일을 ByteArrayOutputStream에 쓰기
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        wb.write(baos);
        wb.close();

        return baos.toByteArray();
    }

    private byte[] createManageExcelData(List<TonerInfo> tonerInfos) throws IOException {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("토너 관리표");

        // 스타일 설정
        CellStyle headerStyle = createManageHeaderStyle(wb);
        CellStyle thinBorderStyle = createThinBorderStyle(wb);
        CellStyle titleStyle = createManageTitleStyle(wb);
        CellStyle centeredStyle = createManageCenteredStyle(wb);

        // 제목 생성
        createManageTitle(sheet, titleStyle, "■ 프린터 및 복합기 사용현황");

        // 헤더 생성
        createManageHeader(sheet, headerStyle);

        // 데이터 채우기
        AtomicInteger rowNum = new AtomicInteger(2);
        tonerInfos.forEach(info -> {
            TonerPrice tonerPrice = tonerPriceRepository.findByTonerNm(info.getTonerNm()).orElse(null);
            String price = (tonerPrice != null) ? tonerPrice.getPrice() : null;

            Row row = sheet.createRow(rowNum.getAndIncrement());
            row.setHeight((short) 550);

            createCell(row, 0, info.getMngNum(), thinBorderStyle, centeredStyle);  // 관리번호
            createCell(row, 1, info.getFloor(), thinBorderStyle, centeredStyle);   // 층
            createCell(row, 2, info.getTeamNm(), thinBorderStyle, centeredStyle); // 사용부서
            createCell(row, 3, info.getManager(), thinBorderStyle, centeredStyle); // 관리자(정)
            createCell(row, 4, info.getSubManager(), thinBorderStyle, centeredStyle); // 관리자(부)
            createCell(row, 5, info.getLocation(), thinBorderStyle, centeredStyle); // 위치
            createCell(row, 6, info.getProductNm(), thinBorderStyle, centeredStyle); // 품명
            createCell(row, 7, info.getModelNm(), thinBorderStyle, centeredStyle); // 모델명
            createCell(row, 8, info.getSn(), thinBorderStyle, centeredStyle); // S/N
            createCell(row, 9, info.getCompany(), thinBorderStyle, centeredStyle); // 제조사
            createCell(row, 10, info.getManuDate(), thinBorderStyle, centeredStyle); // 제조년월
            createCell(row, 11, info.getTonerNm(), thinBorderStyle, centeredStyle); // 토너(잉크)명
            createCell(row, 12, price, thinBorderStyle, centeredStyle); // 단가
        });

        // 열 너비 조정
        sheet.setColumnWidth(0, 2500);  // 관리번호
        sheet.setColumnWidth(1, 2000);  // 층
        sheet.setColumnWidth(2, 4500);  // 사용부서
        sheet.setColumnWidth(3, 2500);  // 관리자(정)
        sheet.setColumnWidth(4, 2500);  // 관리자(부)
        sheet.setColumnWidth(5, 5000);  // 위치
        sheet.setColumnWidth(6, 3500);  // 품명
        sheet.setColumnWidth(7, 6500);  // 모델명
        sheet.setColumnWidth(8, 7500);  // S/N
        sheet.setColumnWidth(9, 4000);  // 제조사
        sheet.setColumnWidth(10, 3500);  // 제조년월
        sheet.setColumnWidth(11, 7500);  // 토너(잉크)명
        sheet.setColumnWidth(12, 3500);  // 단가

        // 엑셀 파일을 ByteArrayOutputStream에 쓰기
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        wb.write(baos);
        wb.close();

        return baos.toByteArray();
    }

    private byte[] createOrderExcelData(List<TonerDetail> tonerDetails, String instCd) throws IOException {

        StdGroup stdGroup = stdGroupRepository.findByGroupCd("C003")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        StdDetail stdDetail = stdDetailRepository.findByGroupCdAndEtcItem1(stdGroup, instCd).get().getFirst();

        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("토너 발주내역");

        CellStyle headerStyle = createHeaderStyle(wb);
        CellStyle titleStyle = createOrderTitleStyle(wb);
        CellStyle subTitleStyle = createSubTitleStyle(wb);
        CellStyle subTitleStyle2 = createSubTitleStyle2(wb);
        CellStyle subTitleStyle3 = createSubTitleStyle3(wb);
        CellStyle subTitleStyle4 = createSubTitleStyle4(wb);
        CellStyle thinBorderStyle = createThinBorderStyle(wb);
        CellStyle centeredStyle = createCenteredStyle(wb);
        CellStyle thickBorderStyle = createThickBorderStyle(wb);

        Row titleRow = sheet.createRow(0);
        titleRow.setHeight((short) 1000);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("발 주 서");
        titleCell.setCellStyle(titleStyle);
        CellRangeAddress titleRegion = new CellRangeAddress(0, 1, 0, 6);
        sheet.addMergedRegion(titleRegion);
        applyBorderToMergedCells(sheet, titleRegion, titleStyle);

        Row orderNoRow = sheet.createRow(3);
        orderNoRow.setHeight((short) 400);

        Cell orderNoCell = orderNoRow.createCell(0);
        // TODO: 어떡하쥐?
        orderNoCell.setCellValue("    발주NO: 24-29");
        orderNoCell.setCellStyle(subTitleStyle);
        CellRangeAddress orderNoRegion = new CellRangeAddress(3, 3, 0, 2);
        sheet.addMergedRegion(orderNoRegion);
        applyBorderToMergedCells(sheet, orderNoRegion, subTitleStyle2);

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        String formattedDate = today.format(formatter);

        Cell dateCell = orderNoRow.createCell(4);
        dateCell.setCellValue(formattedDate);
        dateCell.setCellStyle(subTitleStyle);
        CellRangeAddress dateRegion = new CellRangeAddress(3, 3, 4, 6);
        sheet.addMergedRegion(dateRegion);
        applyBorderToMergedCells(sheet, dateRegion, subTitleStyle3);

        createThickTopBorderForRow2(sheet, 4, thickBorderStyle);

        Row blankRow = sheet.createRow(4);
        blankRow.setHeight((short) 80);

        Row receiverRow = sheet.createRow(5);
        receiverRow.setHeight((short) 400);

        Cell receiverCell = receiverRow.createCell(0);
        receiverCell.setCellValue("    수   신 : 프린텍솔루션");
        receiverCell.setCellStyle(subTitleStyle);
        CellRangeAddress receiverRegion = new CellRangeAddress(5, 5, 0, 1);
        sheet.addMergedRegion(receiverRegion);
        applyBorderToMergedCells(sheet, receiverRegion, subTitleStyle);

        Cell blankCell = receiverRow.createCell(2);
        blankCell.setCellValue("");
        blankCell.setCellStyle(subTitleStyle);

        Cell companyCell = receiverRow.createCell(3);
        companyCell.setCellValue("    KMI (재) 한국의학연구소 " + stdDetail.getDetailNm());
        companyCell.setCellStyle(subTitleStyle);
        CellRangeAddress companyRegion = new CellRangeAddress(5, 5, 3, 6);
        sheet.addMergedRegion(companyRegion);
        applyBorderToMergedCells(sheet, companyRegion, subTitleStyle);

        Row faxRow = sheet.createRow(6);
        faxRow.setHeight((short) 400);

        Cell faxCell = faxRow.createCell(0);
        faxCell.setCellValue("     F A X: 032-663-8601");
        faxCell.setCellStyle(subTitleStyle);
        CellRangeAddress faxRegion = new CellRangeAddress(6, 6, 0, 1);
        sheet.addMergedRegion(faxRegion);
        applyBorderToMergedCells(sheet, faxRegion, subTitleStyle);

        Cell blankCell2 = faxRow.createCell(2);
        blankCell2.setCellValue("");
        blankCell2.setCellStyle(subTitleStyle);

        Cell addressCell = faxRow.createCell(3);
        addressCell.setCellValue("   " + stdDetail.getEtcItem5());
        addressCell.setCellStyle(subTitleStyle);
        CellRangeAddress addressRegion = new CellRangeAddress(6, 6, 3, 6);
        sheet.addMergedRegion(addressRegion);
        applyBorderToMergedCells(sheet, addressRegion, subTitleStyle);

        Row orderTitleRow = sheet.createRow(7);
        orderTitleRow.setHeight((short) 400);

        Cell orderTitleCell = orderTitleRow.createCell(0);
        orderTitleCell.setCellValue("    제   목 : 잉크, 토너 발주");
        orderTitleCell.setCellStyle(subTitleStyle);
        CellRangeAddress orderTitleRegion = new CellRangeAddress(7, 7, 0, 2);
        sheet.addMergedRegion(orderTitleRegion);
        applyBorderToMergedCells(sheet, orderTitleRegion, subTitleStyle);

        Cell phoneNumCell = orderTitleRow.createCell(3);
        phoneNumCell.setCellValue("  ☎ " + stdDetail.getEtcItem6() + " / FAX : " + stdDetail.getEtcItem7());
        phoneNumCell.setCellStyle(subTitleStyle);
        CellRangeAddress phoneNumRegion = new CellRangeAddress(7, 7, 3, 6);
        sheet.addMergedRegion(phoneNumRegion);
        applyBorderToMergedCells(sheet, phoneNumRegion, subTitleStyle);

        Row deliveryRow = sheet.createRow(8);
        deliveryRow.setHeight((short) 400);

        Cell deliveryDateCell = deliveryRow.createCell(0);
        deliveryDateCell.setCellValue("    납 품 일 : 즉시");
        deliveryDateCell.setCellStyle(subTitleStyle);
        CellRangeAddress deliveryDateRegion = new CellRangeAddress(8, 8, 0, 2);
        sheet.addMergedRegion(deliveryDateRegion);
        applyBorderToMergedCells(sheet, deliveryDateRegion, subTitleStyle);

        Cell orderCell = deliveryRow.createCell(3);
        orderCell.setCellValue("발주자");
        orderCell.setCellStyle(subTitleStyle4);

        Cell orderNmCell = deliveryRow.createCell(4);
        orderNmCell.setCellValue(stdDetail.getEtcItem2() + "   " + stdDetail.getEtcItem4());
        orderNmCell.setCellStyle(subTitleStyle);
        CellRangeAddress orderNmRegion = new CellRangeAddress(8, 8, 4, 5);
        sheet.addMergedRegion(orderNmRegion);
        applyBorderToMergedCells(sheet, orderNmRegion, subTitleStyle4);

        Cell blankCell3 = deliveryRow.createCell(6);
        blankCell3.setCellValue("");
        blankCell3.setCellStyle(subTitleStyle);

        Row blankRow2 = sheet.createRow(9);
        blankRow2.setHeight((short) 80);

        Row headerRow = sheet.createRow(10);
        headerRow.setHeight((short) 400);

        String[] headers = {"번 호", "품 명", "수 량", "단 위", "단가", "가격", "비 고"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        AtomicInteger rowNum = new AtomicInteger(11);
        AtomicInteger no = new AtomicInteger(1);

        for (TonerDetail detail : tonerDetails) {
            Row row = sheet.createRow(rowNum.getAndIncrement());
            row.setHeight((short) 400);
            createCell(row, 0, no.getAndIncrement(), thinBorderStyle, centeredStyle);
            createCell(row, 1, detail.getTonerNm(), thinBorderStyle, centeredStyle);
            createCell(row, 2, detail.getQuantity(), thinBorderStyle, centeredStyle);
            createCell(row, 3, "EA", thinBorderStyle, centeredStyle);
            createCell(row, 4, detail.getUnitPrice(), thinBorderStyle, centeredStyle);
            createCell(row, 5, detail.getTotalPrice(), thinBorderStyle, centeredStyle);
            createCell(row, 6, detail.getMngNum(), thinBorderStyle, centeredStyle);
        }
//
        int lastRowNum = rowNum.getAndIncrement();
        Row totalRow = sheet.createRow(lastRowNum);
        totalRow.setHeight((short) 400);

        Cell totalLabelCell = totalRow.createCell(0);
        totalLabelCell.setCellValue("계");
        totalLabelCell.setCellStyle(centeredStyle);

        CellRangeAddress totalLabelRegion = new CellRangeAddress(lastRowNum, lastRowNum, 0, 4);
        sheet.addMergedRegion(totalLabelRegion);
        applyBorderToMergedCells(sheet, totalLabelRegion, centeredStyle);

        int totalPriceSum = tonerDetails.stream()
                .mapToInt(detail -> {
                    String priceStr = detail.getTotalPrice();
                    priceStr = priceStr.replace(",", "");
                    return Integer.parseInt(priceStr);
                })
                .sum();

        DecimalFormat formatter2 = new DecimalFormat("#,###");
        String formattedTotalPrice = formatter2.format(totalPriceSum);

        Cell totalPriceCell = totalRow.createCell(5);
        totalPriceCell.setCellValue(formattedTotalPrice);
        totalPriceCell.setCellStyle(centeredStyle);

        Cell blankCell4 = totalRow.createCell(6);
        blankCell4.setCellValue("");
        blankCell4.setCellStyle(centeredStyle);

        sheet.setColumnWidth(0, 3000);
        sheet.setColumnWidth(1, 8000);
        sheet.setColumnWidth(2, 2000);
        sheet.setColumnWidth(3, 2500);
        sheet.setColumnWidth(4, 4000);
        sheet.setColumnWidth(5, 4000);
        sheet.setColumnWidth(6, 5000);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        wb.write(baos);
        wb.close();

        return baos.toByteArray();
    }

    /* 토너 단가표 */
    private void createPriceTitle(Sheet sheet, CellStyle style, String title) {
        Row titleRow = sheet.createRow(0);
        titleRow.setHeight((short) 350);
        Cell cell = titleRow.createCell(0);
        cell.setCellValue(title);
        sheet.addMergedRegion(new CellRangeAddress(0, 4, 0, 6));
        cell.setCellStyle(style);
    }

    private void createPriceHeader(Sheet sheet, CellStyle style) {
        Row headerRow = sheet.createRow(5);
        headerRow.setHeight((short) 350);
        String[] headers = {"번 호", "제조사", "프린터명", "토너/잉크/드럼명", "구분", "단가", "비고"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
    }

    private CellStyle createHeaderStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFillForegroundColor(IndexedColors.WHITE1.getIndex());
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
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
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
        style.setBorderBottom(BorderStyle.MEDIUM);
        return style;
    }

    /* 토너 관리표 */
    private void createManageTitle(Sheet sheet, CellStyle style, String title) {
        Row titleRow = sheet.createRow(0);
        titleRow.setHeight((short) 800);
        Cell cell = titleRow.createCell(0);
        cell.setCellValue(title);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 12));
        cell.setCellStyle(style);
    }

    private void createManageHeader(Sheet sheet, CellStyle style) {
        Row headerRow = sheet.createRow(1);
        headerRow.setHeight((short) 550);
        String[] headers = {"관리번호", "층", "사용부서", "관리자(정)", "관리자(부)", "위치", "품명", "모델명", "S/N", "제조사", "제조년월", "토너(잉크)명", "단가"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
    }

    private CellStyle createManageHeaderStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFillForegroundColor(IndexedColors.WHITE1.getIndex());
        style.setBorderBottom(BorderStyle.DOUBLE);
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createManageCenteredStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createManageTitleStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 18);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.MEDIUM);
        return style;
    }

    /* 토너 발주용 */
    private CellStyle createOrderTitleStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 30);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createSubTitleStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createSubTitleStyle2(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createSubTitleStyle3(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createSubTitleStyle4(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private void applyBorderToMergedCells(Sheet sheet, CellRangeAddress region, CellStyle style) {
        for (int row = region.getFirstRow(); row <= region.getLastRow(); row++) {
            Row sheetRow = sheet.getRow(row);
            if (sheetRow == null) {
                sheetRow = sheet.createRow(row);
            }
            for (int col = region.getFirstColumn(); col <= region.getLastColumn(); col++) {
                Cell cell = sheetRow.getCell(col);
                if (cell == null) {
                    cell = sheetRow.createCell(col);
                }
                cell.setCellStyle(style);
            }
        }
    }

    /* 공통 사용 */
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

    private void createThickTopBorderForRow(Sheet sheet, int rowIndex, CellStyle thickBorderStyle) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }

        for (int i = 0; i < 7; i++) {
            Cell cell = row.getCell(i);
            if (cell == null) {
                cell = row.createCell(i);
            }
            CellStyle newStyle = sheet.getWorkbook().createCellStyle();
            newStyle.cloneStyleFrom(thickBorderStyle);

            newStyle.setBorderBottom(BorderStyle.MEDIUM);
            newStyle.setBorderTop(BorderStyle.NONE);
            newStyle.setBorderLeft(BorderStyle.NONE);
            newStyle.setBorderRight(BorderStyle.NONE);

            cell.setCellStyle(newStyle);
        }
    }

    private void createThickTopBorderForRow2(Sheet sheet, int rowIndex, CellStyle thickBorderStyle) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }

        for (int i = 0; i < 7; i++) {
            Cell cell = row.getCell(i);
            if (cell == null) {
                cell = row.createCell(i);
            }
            CellStyle newStyle = sheet.getWorkbook().createCellStyle();
            newStyle.cloneStyleFrom(thickBorderStyle);

            newStyle.setBorderBottom(BorderStyle.NONE);
            newStyle.setBorderTop(BorderStyle.MEDIUM);
            newStyle.setBorderLeft(BorderStyle.NONE);
            newStyle.setBorderRight(BorderStyle.NONE);

            cell.setCellStyle(newStyle);
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
