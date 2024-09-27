package kr.or.kmi.mis.api.apply.service.Impl;

import kr.or.kmi.mis.api.apply.model.response.MyApplyResponseDTO;
import kr.or.kmi.mis.api.apply.model.response.PendingCountResponseDTO;
import kr.or.kmi.mis.api.apply.service.ApplyService;
import kr.or.kmi.mis.api.bcd.model.response.BcdMasterResponseDTO;
import kr.or.kmi.mis.api.bcd.model.response.BcdMyResponseDTO;
import kr.or.kmi.mis.api.bcd.service.BcdService;
import kr.or.kmi.mis.api.apply.model.response.ApplyResponseDTO;
import kr.or.kmi.mis.api.apply.model.response.PendingResponseDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocMasterResponseDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocMyResponseDTO;
import kr.or.kmi.mis.api.corpdoc.service.CorpDocListService;
import kr.or.kmi.mis.api.corpdoc.service.CorpDocService;
import kr.or.kmi.mis.api.doc.model.response.DocMasterResponseDTO;
import kr.or.kmi.mis.api.doc.model.response.DocMyResponseDTO;
import kr.or.kmi.mis.api.doc.service.DocService;
import kr.or.kmi.mis.api.order.service.OrderService;
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
    private final CorpDocListService corpDocListService;
    private final OrderService orderService;

    @Override
    @Transactional(readOnly = true)
    public ApplyResponseDTO getAllApplyList(String documentType, LocalDate startDate, LocalDate endDate, String searchType, String keyword, String instCd, String userId) {
        List<BcdMasterResponseDTO> bcdApplyLists = new ArrayList<>();
        List<DocMasterResponseDTO> docApplyLists = new ArrayList<>();
        List<CorpDocMasterResponseDTO> corpDocApplyLists = new ArrayList<>();
        List<SealMasterResponseDTO> sealApplyLists = new ArrayList<>();

        Timestamp[] timestamps = getDateIntoTimestamp(startDate, endDate);

        switch (documentType) {
            case "A":
                bcdApplyLists = bcdService.getBcdApply(timestamps[0], timestamps[1], searchType, keyword, instCd, userId);
                break;
            case "B":
                docApplyLists = docService.getDocApply(timestamps[0], timestamps[1], searchType, keyword, instCd, userId);
                break;
            case "C":
                corpDocApplyLists = corpDocService.getCorpDocApply(timestamps[0], timestamps[1], searchType, keyword);
                break;
            case "D":
                sealApplyLists = sealListService.getSealApply(timestamps[0], timestamps[1], searchType, keyword, instCd);
                break;
            default:
                break;
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

        if (documentType != null) {
            switch (documentType) {
                case "A":
                    myBcdApplyList = bcdService.getMyBcdApply(timestamps[0], timestamps[1], userId);
                    break;
                case "B":
                    myDocApplyList = docService.getMyDocApply(timestamps[0], timestamps[1], userId);
                    break;
                case "C":
                    myCorpDocApplyList = corpDocService.getMyCorpDocApply(timestamps[0], timestamps[1], userId);
                    break;
                case "D":
                    mySealApplyList = sealListService.getMySealApply(timestamps[0], timestamps[1], userId);
                    break;
                default:
                    break;
            }
        } else {
            // 전체 신청 목록을 조회합니다.
            myBcdApplyList = bcdService.getMyBcdApply(timestamps[0], timestamps[1], userId);
            myDocApplyList = docService.getMyDocApply(timestamps[0], timestamps[1], userId);
            myCorpDocApplyList = corpDocService.getMyCorpDocApply(timestamps[0], timestamps[1], userId);
            mySealApplyList = sealListService.getMySealApply(timestamps[0], timestamps[1], userId);
        }

        return MyApplyResponseDTO.of(myBcdApplyList, myDocApplyList, myCorpDocApplyList, mySealApplyList);
    }

    @Override
    @Transactional(readOnly = true)
    public PendingResponseDTO getPendingListByType(String documentType, LocalDate startDate, LocalDate endDate, String instCd, String userId) {

        Timestamp[] timestamps = getDateIntoTimestamp(startDate, endDate);

        return switch (documentType) {
            case "A" -> PendingResponseDTO.of(bcdService.getPendingList(timestamps[0], timestamps[1], instCd, userId), null, null, null);
            case "B" -> PendingResponseDTO.of(null, docService.getDocPendingList(timestamps[0], timestamps[1], instCd, userId), null, null);
            case "C" -> PendingResponseDTO.of(null, null, corpDocService.getPendingList(timestamps[0], timestamps[1]), null);
            case "D" -> PendingResponseDTO.of(null, null, null, sealListService.getSealPendingList(timestamps[0], timestamps[1], instCd));
            default -> throw new IllegalArgumentException("Invalid document type: " + documentType);
        };
    }


    @Override
    @Transactional(readOnly = true)
    public PendingCountResponseDTO getPendingCountList(String documentType, LocalDate startDate, LocalDate endDate, String instCd, String userId) {

        Timestamp[] timestamps = getDateIntoTimestamp(startDate, endDate);

        int bcdPendingCount = bcdService.getPendingList(timestamps[0], timestamps[1], instCd, userId).size();
        int docPendingCount = docService.getDocPendingList(timestamps[0], timestamps[1], instCd, userId).size();
        int corpDocPendingCount = corpDocService.getPendingList(timestamps[0], timestamps[1]).size();
        int sealPendingCount = sealListService.getSealPendingList(timestamps[0], timestamps[1], instCd).size();
        int corpDocIssuePendingCount = corpDocListService.getCorpDocIssuePendingList();
        int orderPendingCount = orderService.getOrderList(instCd).size();

        return PendingCountResponseDTO.of(bcdPendingCount, docPendingCount, corpDocPendingCount, sealPendingCount, corpDocIssuePendingCount, orderPendingCount);
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
