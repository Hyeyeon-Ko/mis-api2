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
    public ApplyResponseDTO getAllApplyList(String documentType, String instCd, String userId) {
        List<BcdMasterResponseDTO> bcdApplyLists = new ArrayList<>();
        List<DocMasterResponseDTO> docApplyLists = new ArrayList<>();
        List<CorpDocMasterResponseDTO> corpDocApplyLists = new ArrayList<>();
        List<SealMasterResponseDTO> sealApplyLists = new ArrayList<>();

        switch (documentType) {
            case "명함신청":
                bcdApplyLists = bcdService.getBcdApplyByInstCd(instCd, userId);
                break;
            case "문서수발신":
                docApplyLists = docService.getDocApplyByInstCd(instCd, userId);
                break;
            case "법인서류":
                corpDocApplyLists = corpDocService.getCorpDocApply();
                break;
            case "인장신청":
                sealApplyLists = sealListService.getSealApplyByInstCd(instCd);
                break;
            default:
                break;
        }

        return ApplyResponseDTO.of(bcdApplyLists, docApplyLists, corpDocApplyLists, sealApplyLists);
    }

    @Override
    @Transactional(readOnly = true)
    public MyApplyResponseDTO getAllMyApplyList(String userId) {

        List<BcdMyResponseDTO> myBcdApplyList = bcdService.getMyBcdApply(userId);
        List<DocMyResponseDTO> myDocApplyList = docService.getMyDocApply(userId);
        List<CorpDocMyResponseDTO> myCorpDocApplyList = corpDocService.getMyCorpDocApply(userId);
        List<SealMyResponseDTO> mySealApplyList = sealListService.getMySealApply(userId);

        return MyApplyResponseDTO.of(myBcdApplyList, myDocApplyList, myCorpDocApplyList, mySealApplyList);
    }

    @Override
    @Transactional(readOnly = true)
    public PendingResponseDTO getPendingListByType(String documentType, String instCd, String userId) {

        return switch (documentType) {
            case "명함신청" -> PendingResponseDTO.of(bcdService.getPendingList(instCd, userId), null, null, null);
            case "문서수발신" -> PendingResponseDTO.of(null, docService.getDocPendingList(instCd, userId), null, null);
            case "법인서류" -> PendingResponseDTO.of(null, null, corpDocService.getPendingList(), null);
            case "인장신청" -> PendingResponseDTO.of(null, null, null, sealListService.getSealPendingList(instCd));
            default -> throw new IllegalArgumentException("Invalid document type: " + documentType);
        };
    }


    @Override
    @Transactional(readOnly = true)
    public PendingCountResponseDTO getPendingCountList(String instCd, String userId) {

        int bcdPendingCount = bcdService.getPendingList(instCd, userId).size();
        int docPendingCount = docService.getDocPendingList(instCd, userId).size();
        int corpDocPendingCount = corpDocService.getPendingList().size();
        int sealPendingCount = sealListService.getSealPendingList(instCd).size();
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
}
