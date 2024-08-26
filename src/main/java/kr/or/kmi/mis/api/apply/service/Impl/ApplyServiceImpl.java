package kr.or.kmi.mis.api.apply.service.Impl;

import kr.or.kmi.mis.api.apply.model.response.MyApplyResponseDTO;
import kr.or.kmi.mis.api.apply.service.ApplyService;
import kr.or.kmi.mis.api.bcd.model.response.BcdMasterResponseDTO;
import kr.or.kmi.mis.api.bcd.model.response.BcdMyResponseDTO;
import kr.or.kmi.mis.api.bcd.service.BcdService;
import kr.or.kmi.mis.api.apply.model.response.ApplyResponseDTO;
import kr.or.kmi.mis.api.apply.model.response.PendingResponseDTO;
import kr.or.kmi.mis.api.doc.model.response.DocMasterResponseDTO;
import kr.or.kmi.mis.api.doc.model.response.DocMyResponseDTO;
import kr.or.kmi.mis.api.doc.service.DocService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ApplyServiceImpl implements ApplyService {

    private final BcdService bcdService;
    private final DocService docService;

    @Override
    @Transactional(readOnly = true)
    public ApplyResponseDTO getAllApplyList(String documentType, LocalDate startDate, LocalDate endDate, String instCd) {

        List<BcdMasterResponseDTO> bcdApplyLists = new ArrayList<>();
        List<DocMasterResponseDTO> docApplyLists = new ArrayList<>();
        Timestamp[] timestamps = getDateIntoTimestamp(startDate, endDate);

        // 특정 유형(ex.명함신청)만 조회합니다.
        if (documentType != null && !documentType.isEmpty()) {
            switch (documentType) {
                case "명함신청":
                    bcdApplyLists = bcdService.getBcdApplyByDateRangeAndInstCd(timestamps[0], timestamps[1], instCd);
                    break;
                case "문서수발신":
                    docApplyLists = docService.getDocApplyByDateRangeAndInstCd(timestamps[0], timestamps[1], instCd);
                    break;
                default:
                    break;
            }
        } else {
            // 전체 신청 목록을 조회합니다.
            bcdApplyLists = bcdService.getBcdApplyByDateRangeAndInstCd(timestamps[0], timestamps[1], instCd);
            docApplyLists = docService.getDocApplyByDateRangeAndInstCd(timestamps[0], timestamps[1], instCd);
        }

        return ApplyResponseDTO.of(bcdApplyLists, docApplyLists);
    }

    @Override
    @Transactional(readOnly = true)
    public MyApplyResponseDTO getAllMyApplyList(String documentType, LocalDate startDate, LocalDate endDate) {

        List<BcdMyResponseDTO> myBcdApplyLists = new ArrayList<>();
        List<DocMyResponseDTO> myDocApplyLists = new ArrayList<>();
        Timestamp[] timestamps = getDateIntoTimestamp(startDate, endDate);

        // 특정 유형(ex.명함신청)만 조회합니다.
        if (documentType != null) {
            switch (documentType) {
                case "명함신청":
                    myBcdApplyLists = bcdService.getMyBcdApplyByDateRange(timestamps[0], timestamps[1]);
                    break;
                case "문서수발신":
                    myDocApplyLists = docService.getMyDocApplyByDateRange(timestamps[0], timestamps[1]);
                default:
                    break;
            }
        } else {
            // 전체 신청 목록을 조회합니다.
            myBcdApplyLists = bcdService.getMyBcdApplyByDateRange(timestamps[0], timestamps[1]);
            myDocApplyLists = docService.getMyDocApplyByDateRange(timestamps[0], timestamps[1]);
        }

        return MyApplyResponseDTO.of(myBcdApplyLists, myDocApplyLists);
    }

    @Override
    @Transactional(readOnly = true)
    public PendingResponseDTO getPendingListByType(String documentType, String instCd) {
        switch (documentType) {
            case "명함신청":
                return PendingResponseDTO.of(bcdService.getPendingList(instCd), null);
            case "문서수발신":
                return PendingResponseDTO.of(null, docService.getDocPendingList(instCd));
            default:
                throw new IllegalArgumentException("Invalid document type: " + documentType);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PendingResponseDTO getMyPendingList() {

        /**
         * 다른 유형의 승인대기 신청목록 추가될 수 있음
         * */
        return PendingResponseDTO.of(bcdService.getMyPendingList(),
                docService.getMyDocPendingList());
    }

    public static Timestamp[] getDateIntoTimestamp(LocalDate startDate, LocalDate endDate) {

        /**
         * 관리자 페이지 > 전체 신청 목록 탭, 최초 진입 -> 기안일자 범위 null 값 전달됨
         * 기안 일자 default 설정
         *    - endDate: 현재 일자
         *    - startDate: 현재 일자로부터 한 달 전 일자
         * */

        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        Timestamp startTimestamp = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp endTimestamp = Timestamp.valueOf(endDate.atTime(LocalTime.MAX));

        return new Timestamp[]{startTimestamp, endTimestamp};
    }
}
