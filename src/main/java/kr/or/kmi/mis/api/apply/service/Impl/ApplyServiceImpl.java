package kr.or.kmi.mis.api.apply.service.Impl;

import kr.or.kmi.mis.api.apply.model.request.ApplyRequestDTO;
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
import kr.or.kmi.mis.cmm.model.request.PostSearchRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
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
    public ApplyResponseDTO getAllApplyList(String documentType, LocalDateTime startDate, LocalDateTime endDate, String searchType, String keyword, String instCd, String userId) {
        List<BcdMasterResponseDTO> bcdApplyLists = new ArrayList<>();
        List<DocMasterResponseDTO> docApplyLists = new ArrayList<>();
        List<CorpDocMasterResponseDTO> corpDocApplyLists = new ArrayList<>();
        List<SealMasterResponseDTO> sealApplyLists = new ArrayList<>();

//        Timestamp[] timestamps = getDateIntoTimestamp(startDate, endDate);

        switch (documentType) {
            case "A":
                bcdApplyLists = bcdService.getBcdApply(startDate, endDate, searchType, keyword, instCd, userId);
                break;
            case "B":
                docApplyLists = docService.getDocApply(startDate, endDate, searchType, keyword, instCd, userId);
                break;
            case "C":
                corpDocApplyLists = corpDocService.getCorpDocApply(startDate, endDate, searchType, keyword);
                break;
            case "D":
                sealApplyLists = sealListService.getSealApply(startDate, endDate, searchType, keyword, instCd);
                break;
            default:
                break;
        }
        return null;
//        return ApplyResponseDTO.of(bcdApplyLists, docApplyLists, corpDocApplyLists, sealApplyLists);
    }


    @Override
    public ApplyResponseDTO getAllApplyList2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable pageable) {
        Page<BcdMasterResponseDTO> bcdApplyLists = null;
        Page<DocMasterResponseDTO> docApplyLists = null;
        Page<CorpDocMasterResponseDTO> corpDocApplyLists = null;
        Page<SealMasterResponseDTO> sealApplyLists = null;

        switch (applyRequestDTO.getDocumentType()) {
            case "B":
                docApplyLists = docService.getDocApply2(applyRequestDTO, postSearchRequestDTO, pageable);
                break;
            case "C":
                corpDocApplyLists = corpDocService.getCorpDocApply2(applyRequestDTO, postSearchRequestDTO, pageable);
                break;
            case "D":
                sealApplyLists = sealListService.getSealApply2(applyRequestDTO, postSearchRequestDTO, pageable);
                break;
            default:
                bcdApplyLists = bcdService.getBcdApply2(applyRequestDTO, postSearchRequestDTO, pageable);
                break;
        }

        return ApplyResponseDTO.of(bcdApplyLists, docApplyLists, corpDocApplyLists, sealApplyLists);
    }

    @Override
    @Transactional(readOnly = true)
    public MyApplyResponseDTO getAllMyApplyList(String documentType, LocalDateTime startDate, LocalDateTime endDate, String userId) {

        List<BcdMyResponseDTO> myBcdApplyList = new ArrayList<>();
        List<DocMyResponseDTO> myDocApplyList = new ArrayList<>();
        List<CorpDocMyResponseDTO> myCorpDocApplyList = new ArrayList<>();
        List<SealMyResponseDTO> mySealApplyList = new ArrayList<>();

//        Timestamp[] timestamps = getDateIntoTimestamp(startDate, endDate);

        if (documentType != null) {
            switch (documentType) {
                case "A":
                    myBcdApplyList = bcdService.getMyBcdApply(startDate, endDate, userId);
                    break;
                case "B":
                    myDocApplyList = docService.getMyDocApply(startDate, endDate, userId);
                    break;
                case "C":
                    myCorpDocApplyList = corpDocService.getMyCorpDocApply(startDate, endDate, userId);
                    break;
                case "D":
                    mySealApplyList = sealListService.getMySealApply(startDate, endDate, userId);
                    break;
                default:
                    break;
            }
        } else {
            // 전체 신청 목록을 조회합니다.
            myBcdApplyList = bcdService.getMyBcdApply(startDate, endDate, userId);
            myDocApplyList = docService.getMyDocApply(startDate, endDate, userId);
            myCorpDocApplyList = corpDocService.getMyCorpDocApply(startDate, endDate, userId);
            mySealApplyList = sealListService.getMySealApply(startDate, endDate, userId);
        }

        return MyApplyResponseDTO.of(myBcdApplyList, myDocApplyList, myCorpDocApplyList, mySealApplyList);
    }

    @Override
    public MyApplyResponseDTO getAllMyApplyList2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable pageable) {
        Page<BcdMasterResponseDTO> myBcdApplyList = null;
        Page<DocMasterResponseDTO> myDocApplyList = null;
        Page<CorpDocMasterResponseDTO> myCorpDocApplyList = null;
        Page<SealMasterResponseDTO> mySealApplyList = null;

        List<BcdMasterResponseDTO> myBcdApplyList2 = null;
        List<DocMasterResponseDTO> myDocApplyList2 = null;
        List<CorpDocMasterResponseDTO> myCorpDocApplyList2 = null;
        List<SealMasterResponseDTO> mySealApplyList2 = null;



        if (applyRequestDTO.getDocumentType() != null) {
            switch (applyRequestDTO.getDocumentType()) {
                case "B":
                    myDocApplyList = docService.getMyDocApply2(applyRequestDTO, postSearchRequestDTO, pageable);
                    break;
                case "C":
                    myCorpDocApplyList = corpDocService.getMyCorpDocApply2(applyRequestDTO, postSearchRequestDTO, pageable);
                    break;
                case "D":
                    mySealApplyList = sealListService.getMySealApply2(applyRequestDTO, postSearchRequestDTO, pageable);
                    break;
                default:
                    myBcdApplyList = bcdService.getMyBcdApply2(applyRequestDTO, postSearchRequestDTO, pageable);
                    break;
            }
            return MyApplyResponseDTO.of(myBcdApplyList, myDocApplyList, myCorpDocApplyList, mySealApplyList);
        } else {
            // 전체 신청 목록을 조회합니다.
            myBcdApplyList2 = bcdService.getMyBcdApply(startDate, endDate, userId);
            mySealApplyList2 = docService.getMyDocApply(startDate, endDate, userId);
            myCorpDocApplyList2 = corpDocService.getMyCorpDocApply(startDate, endDate, userId);
            myDocApplyList2 = sealListService.getMySealApply(startDate, endDate, userId);

            return MyApplyResponseDTO.in(myBcdApplyList, myDocApplyList, myCorpDocApplyList, mySealApplyList);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PendingResponseDTO getPendingListByType(String documentType, LocalDateTime startDate, LocalDateTime endDate, String instCd, String userId) {

//        Timestamp[] timestamps = getDateIntoTimestamp(startDate, endDate);

        return switch (documentType) {
            case "A" -> PendingResponseDTO.of(bcdService.getPendingList(startDate, endDate, instCd, userId), null, null, null);
            case "B" -> PendingResponseDTO.of(null, docService.getDocPendingList(startDate, endDate, instCd, userId), null, null);
            case "C" -> PendingResponseDTO.of(null, null, corpDocService.getPendingList(startDate, endDate), null);
            case "D" -> PendingResponseDTO.of(null, null, null, sealListService.getSealPendingList(startDate, endDate, instCd));
            default -> throw new IllegalArgumentException("Invalid document type: " + documentType);
        };
    }


    @Override
    @Transactional(readOnly = true)
    public PendingCountResponseDTO getPendingCountList(String documentType, LocalDateTime startDate, LocalDateTime endDate, String instCd, String userId) {

//        Timestamp[] timestamps = getDateIntoTimestamp(startDate, endDate);

        int bcdPendingCount = bcdService.getPendingList(startDate, endDate, instCd, userId).size();
        int docPendingCount = docService.getDocPendingList(startDate, endDate, instCd, userId).size();
        int corpDocPendingCount = corpDocService.getPendingList(startDate, endDate).size();
        int sealPendingCount = sealListService.getSealPendingList(startDate, endDate, instCd).size();
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

//    public static Timestamp[] getDateIntoTimestamp(LocalDateTime startDate, LocalDateTime endDate) {
//
//        if (startDate == null) {
//            startDate = LocalDateTime.now().minusMonths(1);
//        }
//        if (endDate == null) {
//            endDate = LocalDateTime.now();
//        }
//
//        Timestamp startTimestamp = Timestamp.valueOf(startDate.atStartOfDay());
//        Timestamp endTimestamp = Timestamp.valueOf(endDate.atTime(LocalTime.MAX));
//
//        return new Timestamp[]{startTimestamp, endTimestamp};
//    }
}
