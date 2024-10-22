package kr.or.kmi.mis.api.toner.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import kr.or.kmi.mis.api.exception.EntityNotFoundException;
import kr.or.kmi.mis.api.toner.model.entity.TonerDetail;
import kr.or.kmi.mis.api.toner.model.entity.TonerMaster;
import kr.or.kmi.mis.api.toner.model.response.TonerPendingResponseDTO;
import kr.or.kmi.mis.api.toner.repository.TonerDetailRepository;
import kr.or.kmi.mis.api.toner.repository.TonerMasterRepository;
import kr.or.kmi.mis.api.toner.service.TonerPendingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TonerPendingServiceImpl implements TonerPendingService {

    private final TonerMasterRepository tonerMasterRepository;
    private final TonerDetailRepository tonerDetailRepository;

    /**
     * 승인 대기 상태의 항목들을 TonerPendingResponseDTO 리스트로 반환합니다.
     * @param instCd 기관 코드 (instCd)로 해당 기관의 TonerMaster 및 관련 TonerDetail 항목을 조회합니다.
     * @return TonerPendingResponseDTO의 리스트. 각 TonerMaster와 연결된 TonerDetail 항목을 기반으로 한 DTO 리스트.
     */
    @Override
    @Transactional(readOnly = true)
    public List<TonerPendingResponseDTO> getTonerPendingList(String instCd) {

        List<TonerMaster> tonerMasterList = tonerMasterRepository.findAllByStatus("A")
                .orElseThrow(() -> new EntityNotFoundException("TonerMaster"));

        return tonerMasterList.stream()
                .flatMap(tonerMaster -> {
                    List<TonerDetail> tonerDetails = tonerDetailRepository.findAllByDraftId(tonerMaster.getDraftId());
                    return tonerDetails.stream().map(tonerDetail -> TonerPendingResponseDTO.builder()
                            .draftId(tonerMaster.getDraftId())
                            .drafter(tonerMaster.getDrafter())
                            .draftDate(tonerMaster.getDraftDate().toLocalDate().toString())
                            .mngNum(tonerDetail.getMngNum())
                            .teamNm(tonerDetail.getTeamNm())
                            .location(tonerDetail.getLocation())
                            .printNm(tonerDetail.getPrintNm())
                            .tonerNm(tonerDetail.getTonerNm())
                            .quantity(String.valueOf(tonerDetail.getQuantity()))
                            .totalPrice(tonerDetail.getTotalPrice())
                            .build());
                })
                .collect(Collectors.toList());
    }

    // TODO: 기안상신용 파일 형식 받은 후 구현
    @Override
    public void downloadExcel(HttpServletResponse response, List<String> draftIds) throws IOException {
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
}
