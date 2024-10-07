package kr.or.kmi.mis.api.apply.service.Impl;

import kr.or.kmi.mis.api.apply.model.request.ApplyRequestDTO;
import kr.or.kmi.mis.api.apply.model.response.MyApplyResponseDTO;
import kr.or.kmi.mis.api.apply.model.response.PendingCountResponseDTO;
import kr.or.kmi.mis.api.apply.service.ApplyService;
import kr.or.kmi.mis.api.bcd.model.response.BcdMasterResponseDTO;
import kr.or.kmi.mis.api.bcd.model.response.BcdMyResponseDTO;
import kr.or.kmi.mis.api.bcd.model.response.BcdPendingResponseDTO;
import kr.or.kmi.mis.api.bcd.repository.BcdPendingQueryRepository;
import kr.or.kmi.mis.api.bcd.service.BcdService;
import kr.or.kmi.mis.api.apply.model.response.ApplyResponseDTO;
import kr.or.kmi.mis.api.apply.model.response.PendingResponseDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocMasterResponseDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocMyResponseDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocPendingResponseDTO;
import kr.or.kmi.mis.api.corpdoc.repository.CorpDocPendingQueryRepository;
import kr.or.kmi.mis.api.corpdoc.service.CorpDocListService;
import kr.or.kmi.mis.api.corpdoc.service.CorpDocService;
import kr.or.kmi.mis.api.doc.model.response.DocMasterResponseDTO;
import kr.or.kmi.mis.api.doc.model.response.DocMyResponseDTO;
import kr.or.kmi.mis.api.doc.model.response.DocPendingResponseDTO;
import kr.or.kmi.mis.api.doc.repository.DocPendingQueryRepository;
import kr.or.kmi.mis.api.doc.repository.impl.DocPendingQueryRepositoryImpl;
import kr.or.kmi.mis.api.doc.service.DocService;
import kr.or.kmi.mis.api.order.service.OrderService;
import kr.or.kmi.mis.api.seal.model.response.SealMasterResponseDTO;
import kr.or.kmi.mis.api.seal.model.response.SealMyResponseDTO;
import kr.or.kmi.mis.api.seal.model.response.SealPendingResponseDTO;
import kr.or.kmi.mis.api.seal.repository.SealPendingQueryRepository;
import kr.or.kmi.mis.api.seal.service.SealListService;
import kr.or.kmi.mis.cmm.model.request.PostSearchRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
public class ApplyServiceImpl implements ApplyService {

    private final BcdService bcdService;
    private final DocService docService;
    private final CorpDocService corpDocService;
    private final SealListService sealListService;
    private final CorpDocListService corpDocListService;
    private final OrderService orderService;

    private final BcdPendingQueryRepository bcdPendingQueryRepository;
    private final DocPendingQueryRepository docPendingQueryRepository;
    private final CorpDocPendingQueryRepository corpDocPendingQueryRepository;
    private final SealPendingQueryRepository sealPendingQueryRepository;

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


    /**
     * 관리자 전체 신청 내역 조회
     * @param applyRequestDTO
     * @param postSearchRequestDTO
     * @param pageable
     * @return
     */
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

        return null;
//        return MyApplyResponseDTO.of(myBcdApplyList, myDocApplyList, myCorpDocApplyList, mySealApplyList);
    }

    /**
     * 내 전체 신청내역
     * @param applyRequestDTO
     * @param postSearchRequestDTO
     * @param pageable
     * @return
     */
    @Override
    public MyApplyResponseDTO getAllMyApplyList2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable pageable) {
        Page<BcdMyResponseDTO> myBcdApplyList = null;
        Page<DocMyResponseDTO> myDocApplyList = null;
        Page<CorpDocMyResponseDTO> myCorpDocApplyList = null;
        Page<SealMyResponseDTO> mySealApplyList = null;

        List<BcdMyResponseDTO> myBcdApplyList2 = new ArrayList<>();
        List<DocMyResponseDTO> myDocApplyList2 = new ArrayList<>();
        List<CorpDocMyResponseDTO> myCorpDocApplyList2 = new ArrayList<>();
        List<SealMyResponseDTO> mySealApplyList2 = new ArrayList<>();

        if (applyRequestDTO.getDocumentType() != null) {
            // 각 신청 별 목록 페이징 조회
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
            // 전체 신청 목록 리스트 조회
            myBcdApplyList2 = bcdService.getMyBcdApply(applyRequestDTO, postSearchRequestDTO);
            myDocApplyList2 = docService.getMyDocApply(applyRequestDTO, postSearchRequestDTO);
            myCorpDocApplyList2 = corpDocService.getMyCorpDocApply(applyRequestDTO, postSearchRequestDTO);
            mySealApplyList2 = sealListService.getMySealApply(applyRequestDTO, postSearchRequestDTO);

            List<Object> combinedList = Stream.concat(
                    Stream.concat(myBcdApplyList2.stream(), myDocApplyList2.stream()),
                    Stream.concat(myCorpDocApplyList2.stream(), mySealApplyList2.stream())
            ).collect(Collectors.toList());

            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), combinedList.size());
            List<Object> pagedList = combinedList.subList(start, end);

            long totalCount = combinedList.size();

            Page<Object> pagedResult = new PageImpl<>(pagedList, pageable, totalCount);

            return MyApplyResponseDTO.of(pagedResult);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PendingResponseDTO getPendingListByType(String documentType, LocalDateTime startDate, LocalDateTime endDate, String instCd, String userId) {

        List<BcdPendingResponseDTO> bcdApplyLists = new ArrayList<>();
        List<DocPendingResponseDTO> docApplyLists = new ArrayList<>();
        List<CorpDocPendingResponseDTO> corpDocApplyLists = new ArrayList<>();
        List<SealPendingResponseDTO> sealApplyLists = new ArrayList<>();

//        Timestamp[] timestamps = getDateIntoTimestamp(startDate, endDate);

        switch (documentType) {
            case "A" -> bcdApplyLists = bcdService.getPendingList(startDate, endDate, instCd, userId);
            case "B" -> docApplyLists = docService.getDocPendingList(startDate, endDate, instCd, userId);
            case "C" -> corpDocApplyLists = corpDocService.getPendingList(startDate, endDate);
            case "D" -> sealApplyLists = sealListService.getSealPendingList(startDate, endDate, instCd);
            default -> throw new IllegalArgumentException("Invalid document type: " + documentType);
        };
        return null;
    }

    @Override
    public PendingResponseDTO getPendingListByType2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page) {
        Page<BcdPendingResponseDTO> bcdApplyLists = null;
        Page<DocPendingResponseDTO> docApplyLists = null;
        Page<CorpDocPendingResponseDTO> corpDocApplyLists = null;
        Page<SealPendingResponseDTO> sealApplyLists = null;

        switch (applyRequestDTO.getDocumentType()) {
            case "B":
                docApplyLists = docService.getDocPendingList2(applyRequestDTO, postSearchRequestDTO, page);
                break;
            case "C":
                corpDocApplyLists = corpDocService.getPendingList2(applyRequestDTO, postSearchRequestDTO, page);
                break;
            case "D":
                sealApplyLists = sealListService.getSealPendingList2(applyRequestDTO, postSearchRequestDTO, page);
                break;
            default:
                bcdApplyLists = bcdService.getPendingList2(applyRequestDTO, postSearchRequestDTO, page);
                break;
        }

        return PendingResponseDTO.of(bcdApplyLists, docApplyLists, corpDocApplyLists, sealApplyLists);
    }

    @Override
    @Transactional(readOnly = true)
    public PendingCountResponseDTO getPendingCountList(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO) {

//        Timestamp[] timestamps = getDateIntoTimestamp(startDate, endDate);

        int bcdPendingCount = Math.toIntExact(bcdPendingQueryRepository.getBcdPendingCount(applyRequestDTO, postSearchRequestDTO));
        int docPendingCount = Math.toIntExact(docPendingQueryRepository.getDocPendingCount(applyRequestDTO, postSearchRequestDTO));
        int corpDocPendingCount = Math.toIntExact(corpDocPendingQueryRepository.getCorpDocPendingCount(applyRequestDTO, postSearchRequestDTO));
        int sealPendingCount = Math.toIntExact(sealPendingQueryRepository.getSealPendingCount(applyRequestDTO, postSearchRequestDTO));
        int corpDocIssuePendingCount = corpDocListService.getCorpDocIssuePendingList();
        int orderPendingCount = orderService.getOrderList(applyRequestDTO.getInstCd()).size();

        return PendingCountResponseDTO.of(bcdPendingCount, docPendingCount, corpDocPendingCount, sealPendingCount, corpDocIssuePendingCount, orderPendingCount);
    }

    @Override
    @Transactional(readOnly = true)
    public PendingResponseDTO getMyPendingList(String userId) {

//        return PendingResponseDTO.of(
//                bcdService.getMyPendingList(userId),
//                docService.getMyDocPendingList(userId),
//                corpDocService.getMyPendingList(userId),
//                sealListService.getMySealPendingList(userId));
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public PendingResponseDTO getMyPendingList2(ApplyRequestDTO applyRequestDTO, Pageable page) {

        return PendingResponseDTO.of(
                bcdService.getMyPendingList2(applyRequestDTO, page),
                docService.getMyDocPendingList2(applyRequestDTO, page),
                corpDocService.getMyPendingList2(applyRequestDTO, page),
                sealListService.getMySealPendingList2(applyRequestDTO, page));
    }

}
