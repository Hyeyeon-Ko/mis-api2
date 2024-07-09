package kr.or.kmi.mis.api.order.service.Impl;

import jakarta.servlet.http.HttpServletResponse;
import kr.or.kmi.mis.api.bcd.model.entity.BcdDetail;
import kr.or.kmi.mis.api.bcd.repository.BcdDetailRepository;
import kr.or.kmi.mis.api.order.service.ExcelService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
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

        AtomicInteger rowNum = new AtomicInteger(0);
        Row row = null;

        // Header
        int cellNum = 0;
        row = sheet.createRow(rowNum.getAndIncrement());
        row.createCell(cellNum++).setCellValue("명함구분");
        row.createCell(cellNum++).setCellValue("이름");
        row.createCell(cellNum++).setCellValue("영문이름");
        row.createCell(cellNum++).setCellValue("센터");
        row.createCell(cellNum++).setCellValue("부서");
        row.createCell(cellNum++).setCellValue("팀");
        row.createCell(cellNum++).setCellValue("직위/직책");
        row.createCell(cellNum++).setCellValue("내선번호");
        row.createCell(cellNum++).setCellValue("팩스번호");
        row.createCell(cellNum++).setCellValue("휴대폰번호");
        row.createCell(cellNum++).setCellValue("이메일");
        row.createCell(cellNum++).setCellValue("주소");
        row.createCell(cellNum++).setCellValue("수량");

        // Body
        bcdDetails.forEach(detail -> {
            Row dataRow = sheet.createRow(rowNum.getAndIncrement());
            int dataCellNum = 0;
            dataRow.createCell(dataCellNum++).setCellValue(detail.getDivision());
            dataRow.createCell(dataCellNum++).setCellValue(detail.getKorNm());
            dataRow.createCell(dataCellNum++).setCellValue(detail.getEngNm());
            dataRow.createCell(dataCellNum++).setCellValue(detail.getInstNm());
            dataRow.createCell(dataCellNum++).setCellValue(detail.getDeptNm());
            dataRow.createCell(dataCellNum++).setCellValue(detail.getTeamNm());
            dataRow.createCell(dataCellNum++).setCellValue(detail.getGrade());
            dataRow.createCell(dataCellNum++).setCellValue(detail.getExtTel());
            dataRow.createCell(dataCellNum++).setCellValue(detail.getFaxTel());
            dataRow.createCell(dataCellNum++).setCellValue(detail.getPhoneTel());
            dataRow.createCell(dataCellNum++).setCellValue(detail.getEmail());
            dataRow.createCell(dataCellNum++).setCellValue(detail.getAddress());
            dataRow.createCell(dataCellNum++).setCellValue(detail.getQuantity());
        });

        // 엑셀 파일을 ByteArrayOutputStream에 쓰기
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        wb.write(baos);
        wb.close();

        return baos.toByteArray();
    }
}
