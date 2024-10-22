package kr.or.kmi.mis.api.toner.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import kr.or.kmi.mis.api.toner.repository.TonerDetailRepository;
import kr.or.kmi.mis.api.toner.repository.TonerInfoRepository;
import kr.or.kmi.mis.api.toner.service.TonerExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TonerExcelServiceImpl implements TonerExcelService {

    private final TonerInfoRepository tonerInfoRepository;
    private final TonerDetailRepository tonerDetailRepository;

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

    // TODO: 기안상신용 파일 형식 받은 후 구현
    @Override
    public void downloadPendingExcel(HttpServletResponse response, List<String> draftIds) throws IOException {
//        byte[] excelData = generateExcel(draftIds);
//
//        try {
//            // HTTP 응답에 엑셀 파일 첨부
//            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//            response.setHeader("Content-Disposition", "attachment; filename=order_details.xlsx");
//            response.setContentLength(excelData.length);
//            response.getOutputStream().write(excelData);
//            response.getOutputStream().flush();
//            response.getOutputStream().close();
//        } catch (Exception e) {
//            throw new IOException("Failed to send Excel file", e);
//        }
    }

//    @Override
//    public byte[] generateExcel(List<String> draftIds) throws IOException {
//        List<TonerDetail> tonerDetails = tonerDetailRepository.findAllByDraftIdIn(draftIds);
//        return createExcelData(tonerDetails);
//    }
//
//    private byte[] createExcelData(List<TonerDetail> tonerDetails) throws IOException {
//    }


    // TODO: 발주용 파일
    @Override
    public void downloadOrderExcel(HttpServletResponse response, List<String> draftIds) {

    }

//    @Override
//    public byte[] generateOrderExcel(List<String> draftIds) throws IOException {
//        List<TonerDetail> tonerDetails = tonerDetailRepository.findAllByDraftIdIn(draftIds);
//        return createExcelData(tonerDetails);
//    }
//
//    // TODO: 발주용 엑셀 파일 생성
//    private byte[] createExcelData(List<TonerDetail> tonerDetails) throws IOException {
//
//    }

}
