package kr.or.kmi.mis.api.apply.service.Impl;

import kr.or.kmi.mis.api.apply.model.response.MyApplyResponseDTO;
import kr.or.kmi.mis.api.apply.service.ApplyService;
import kr.or.kmi.mis.api.bcd.model.response.BcdMasterResponseDTO;
import kr.or.kmi.mis.api.bcd.model.response.BcdMyResponseDTO;
import kr.or.kmi.mis.api.bcd.service.BcdService;
import kr.or.kmi.mis.api.apply.model.response.ApplyResponseDTO;
import kr.or.kmi.mis.api.apply.model.response.PendingResponseDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocMasterResponseDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocMyResponseDTO;
import kr.or.kmi.mis.api.corpdoc.service.CorpDocService;
import kr.or.kmi.mis.api.doc.model.response.DocMasterResponseDTO;
import kr.or.kmi.mis.api.doc.model.response.DocMyResponseDTO;
import kr.or.kmi.mis.api.doc.service.DocService;
import kr.or.kmi.mis.api.seal.model.response.SealMasterResponseDTO;
import kr.or.kmi.mis.api.seal.model.response.SealMyResponseDTO;
import kr.or.kmi.mis.api.seal.service.SealListService;
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
    private final CorpDocService corpDocService;
    private final SealListService sealListService;

    @Override
    @Transactional(readOnly = true)
    public ApplyResponseDTO getAllApplyList(String documentType, LocalDate startDate, LocalDate endDate, String searchType, String keyword, String instCd) {
        List<BcdMasterResponseDTO> bcdApplyLists = new ArrayList<>();
        List<DocMasterResponseDTO> docApplyLists = new ArrayList<>();
        List<CorpDocMasterResponseDTO> corpDocApplyLists = new ArrayList<>();
        List<SealMasterResponseDTO> sealApplyLists = new ArrayList<>();
        Timestamp[] timestamps = getDateIntoTimestamp(startDate, endDate);

        if (documentType != null && !documentType.isEmpty()) {
            switch (documentType) {
                case "명함신청":
                    bcdApplyLists = bcdService.getBcdApplyByDateRangeAndInstCdAndSearch(timestamps[0], timestamps[1], searchType, keyword, instCd);
                    break;
                case "문서수발신":
                    docApplyLists = docService.getDocApplyByDateRangeAndInstCdAndSearch(timestamps[0], timestamps[1], searchType, keyword, instCd);
                    break;
                case "법인서류":
                    corpDocApplyLists = corpDocService.getCorpDocApplyByDateRangeAndSearch(timestamps[0], timestamps[1], searchType, keyword);
                    break;
                case "인장신청":
                    sealApplyLists = sealListService.getSealApplyByDateRangeAndInstCdAndSearch(timestamps[0], timestamps[1], searchType, keyword, instCd);
                    break;
                default:
                    break;
            }
        } else {
            bcdApplyLists = bcdService.getBcdApplyByDateRangeAndInstCdAndSearch(timestamps[0], timestamps[1], instCd, searchType, keyword);
            docApplyLists = docService.getDocApplyByDateRangeAndInstCdAndSearch(timestamps[0], timestamps[1], instCd, searchType, keyword);
            corpDocApplyLists = corpDocService.getCorpDocApplyByDateRangeAndSearch(timestamps[0], timestamps[1], searchType, keyword);
            sealApplyLists = sealListService.getSealApplyByDateRangeAndInstCdAndSearch(timestamps[0], timestamps[1], instCd, searchType, keyword);
        }

        return ApplyResponseDTO.of(bcdApplyLists, docApplyLists, corpDocApplyLists, sealApplyLists);
    }

    @Override
    @Transactional(readOnly = true)
    public MyApplyResponseDTO getAllMyApplyList(String documentType, LocalDate startDate, LocalDate endDate, String userId) {

        List<BcdMyResponseDTO> myBcdApplyList = new ArrayList<>();
        List<DocMyResponseDTO> myDocApplyList = new ArrayList<>();
        List<CorpDocMyResponseDTO> myCorpDocApplyList = new ArrayList<>();
        List<SealMyResponseDTO> mySealApplyList = new ArrayList<>();

        Timestamp[] timestamps = getDateIntoTimestamp(startDate, endDate);

        // 특정 유형(ex.명함신청)만 조회합니다.
        if (documentType != null) {
            switch (documentType) {
                case "명함신청":
                    myBcdApplyList = bcdService.getMyBcdApplyByDateRange(timestamps[0], timestamps[1], userId);
                    break;
                case "문서수신":
                case "문서발신":
                    myDocApplyList = docService.getMyDocApplyByDateRange(timestamps[0], timestamps[1], userId);
                    break;
                case "법인서류":
                    myCorpDocApplyList = corpDocService.getMyCorpDocApplyByDateRange(timestamps[0], timestamps[1], userId);
                case "인장신청(날인)":
                case "인장신청(반출)":
                    mySealApplyList = sealListService.getMySealApplyByDateRange(timestamps[0], timestamps[1], userId);
                default:
                    break;
            }
        } else {
            // 전체 신청 목록을 조회합니다.
            myBcdApplyList = bcdService.getMyBcdApplyByDateRange(timestamps[0], timestamps[1], userId);
            myDocApplyList = docService.getMyDocApplyByDateRange(timestamps[0], timestamps[1], userId);
            myCorpDocApplyList = corpDocService.getMyCorpDocApplyByDateRange(timestamps[0], timestamps[1], userId);
            mySealApplyList = sealListService.getMySealApplyByDateRange(timestamps[0], timestamps[1], userId);
        }

        return MyApplyResponseDTO.of(myBcdApplyList, myDocApplyList, myCorpDocApplyList, mySealApplyList);
    }

    @Override
    @Transactional(readOnly = true)
    public PendingResponseDTO getPendingListByType(String documentType, LocalDate startDate, LocalDate endDate, String instCd) {
        Timestamp[] timestamps = getDateIntoTimestamp(startDate, endDate);

        return switch (documentType) {
            case "명함신청" -> PendingResponseDTO.of(bcdService.getPendingList(timestamps[0], timestamps[1], instCd), null, null, null);
            case "문서수발신" -> PendingResponseDTO.of(null, docService.getDocPendingList(timestamps[0], timestamps[1], instCd), null, null);
            case "법인서류" -> PendingResponseDTO.of(null, null, corpDocService.getPendingList(timestamps[0], timestamps[1]), null);
            case "인장신청" -> PendingResponseDTO.of(null, null, null, sealListService.getSealPendingList(timestamps[0], timestamps[1], instCd));
            default -> throw new IllegalArgumentException("Invalid document type: " + documentType);
        };
    }

    @Override
    @Transactional(readOnly = true)
    public PendingResponseDTO getMyPendingList(String userId) {

        return PendingResponseDTO.of(
                bcdService.getMyPendingList(userId),
                docService.getMyDocPendingList(userId),
                corpDocService.getMyPendingList(userId),
                sealListService.getMySealPendingList(userId));
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
