package kr.or.kmi.mis.api.apply.service.Impl;

import kr.or.kmi.mis.api.apply.model.request.ApplyRequestDTO;
import kr.or.kmi.mis.api.apply.model.response.*;
import kr.or.kmi.mis.api.apply.service.ApplyService;
import kr.or.kmi.mis.api.bcd.model.response.BcdMasterResponseDTO;
import kr.or.kmi.mis.api.bcd.model.response.BcdMyResponseDTO;
import kr.or.kmi.mis.api.bcd.model.response.BcdPendingResponseDTO;
import kr.or.kmi.mis.api.bcd.repository.BcdPendingQueryRepository;
import kr.or.kmi.mis.api.bcd.service.BcdService;
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
import kr.or.kmi.mis.api.doc.service.DocService;
import kr.or.kmi.mis.api.docstorage.service.DocstorageListService;
import kr.or.kmi.mis.api.order.service.OrderService;
import kr.or.kmi.mis.api.seal.model.response.SealMasterResponseDTO;
import kr.or.kmi.mis.api.seal.model.response.SealMyResponseDTO;
import kr.or.kmi.mis.api.seal.model.response.SealPendingResponseDTO;
import kr.or.kmi.mis.api.seal.repository.SealPendingQueryRepository;
import kr.or.kmi.mis.api.seal.service.SealListService;
import kr.or.kmi.mis.api.toner.model.response.TonerMasterResponseDTO;
import kr.or.kmi.mis.api.toner.model.response.TonerMyResponseDTO;
import kr.or.kmi.mis.api.toner.model.response.TonerPendingListResponseDTO;
import kr.or.kmi.mis.api.toner.service.TonerOrderService;
import kr.or.kmi.mis.api.toner.service.TonerPendingService;
import kr.or.kmi.mis.api.toner.service.TonerService;
import kr.or.kmi.mis.cmm.model.request.PostSearchRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
    private final DocstorageListService docstorageListService;
    private final TonerService tonerService;
    private final TonerPendingService tonerPendingService;
    private final TonerOrderService tonerOrderService;

    private final BcdPendingQueryRepository bcdPendingQueryRepository;
    private final DocPendingQueryRepository docPendingQueryRepository;
    private final CorpDocPendingQueryRepository corpDocPendingQueryRepository;
    private final SealPendingQueryRepository sealPendingQueryRepository;

    @Override
    @Transactional(readOnly = true)
    public ApplyListResponseDTO getAllApplyList(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO) {
        List<BcdMasterResponseDTO> bcdApplyLists = new ArrayList<>();
        List<DocMasterResponseDTO> docApplyLists = new ArrayList<>();

        if (applyRequestDTO.getDocumentType().equals("B")) {
            docApplyLists = docService.getDocApply(applyRequestDTO, postSearchRequestDTO);
        } else {
            bcdApplyLists = bcdService.getBcdApply(applyRequestDTO, postSearchRequestDTO);
        }
        return ApplyListResponseDTO.of(bcdApplyLists, docApplyLists);
    }


    /**
     * 관리자 전체 신청 내역 조회
     * @param applyRequestDTO applyRequestDTO
     * @param postSearchRequestDTO postSearchRequestDTO
     * @param pageable pageable
     * @return ApplyResponseDTO
     */
    @Override
    public ApplyResponseDTO getAllApplyList2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable pageable) {
        Page<BcdMasterResponseDTO> bcdApplyLists = null;
        Page<DocMasterResponseDTO> docApplyLists = null;
        Page<CorpDocMasterResponseDTO> corpDocApplyLists = null;
        Page<SealMasterResponseDTO> sealApplyLists = null;
        Page<TonerMasterResponseDTO> tonerApplyLists = null;

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
            case "E":
                tonerApplyLists = tonerService.getTonerApply2(applyRequestDTO, postSearchRequestDTO, pageable);
                break;
            default:
                bcdApplyLists = bcdService.getBcdApply2(applyRequestDTO, postSearchRequestDTO, pageable);
                break;
        }

        return ApplyResponseDTO.of(bcdApplyLists, docApplyLists, corpDocApplyLists, sealApplyLists, tonerApplyLists);
    }

    /**
     * 내 전체 신청내역
     * @param applyRequestDTO applyRequestDTO
     * @param postSearchRequestDTO postSearchRequestDTO
     * @param pageable pageable
     * @return MyApplyResponseDTO
     */
    @Override
    public MyApplyResponseDTO getAllMyApplyList2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable pageable) {

        Page<BcdMyResponseDTO> myBcdApplyList = null;
        Page<DocMyResponseDTO> myDocApplyList = null;
        Page<CorpDocMyResponseDTO> myCorpDocApplyList = null;
        Page<SealMyResponseDTO> mySealApplyList = null;
        Page<TonerMyResponseDTO> myTonerApplyList = null;

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
                case "E":
                    myTonerApplyList = tonerService.getMyTonerApply2(applyRequestDTO, postSearchRequestDTO, pageable);
                    break;
                default:
                    myBcdApplyList = bcdService.getMyBcdApply2(applyRequestDTO, postSearchRequestDTO, pageable);
                    break;
            }
            return MyApplyResponseDTO.of(myBcdApplyList, myDocApplyList, myCorpDocApplyList, mySealApplyList, myTonerApplyList);
        } else {
            // 전체 신청 목록 리스트 조회
            List<BcdMyResponseDTO> myBcdApplyList2 = bcdService.getMyBcdApply(applyRequestDTO, postSearchRequestDTO);
            List<DocMyResponseDTO> myDocApplyList2 = docService.getMyDocApply(applyRequestDTO, postSearchRequestDTO);
            List<CorpDocMyResponseDTO> myCorpDocApplyList2 = corpDocService.getMyCorpDocApply(applyRequestDTO, postSearchRequestDTO);
            List<SealMyResponseDTO> mySealApplyList2 = sealListService.getMySealApply(applyRequestDTO, postSearchRequestDTO);
            List<TonerMyResponseDTO> myTonerApplyList2 = tonerService.getMyTonerApply(applyRequestDTO, postSearchRequestDTO);

            List<Object> combinedList = Stream.of(myBcdApplyList2, myDocApplyList2, myCorpDocApplyList2, mySealApplyList2, myTonerApplyList2)
                    .flatMap(List::stream)
                    .sorted(Comparator.comparing(this::extractDraftDate).reversed())
                    .collect(Collectors.toList());

            Page<Object> pagedResult = returnPageResult(pageable, combinedList);

            return MyApplyResponseDTO.of(pagedResult);
        }
    }

    private LocalDateTime extractDraftDate(Object obj) {
        if (obj instanceof BcdMyResponseDTO) {
            return ((BcdMyResponseDTO) obj).getDraftDate();
        } else if (obj instanceof DocMyResponseDTO) {
            return ((DocMyResponseDTO) obj).getDraftDate();
        } else if (obj instanceof CorpDocMyResponseDTO) {
            return ((CorpDocMyResponseDTO) obj).getDraftDate();
        } else if (obj instanceof SealMyResponseDTO) {
            return ((SealMyResponseDTO) obj).getDraftDate();
        } else if (obj instanceof TonerMyResponseDTO) {
            return ((TonerMyResponseDTO) obj).getDraftDate();
        }
        return LocalDateTime.now();
    }

    @Override
    public PendingResponseDTO getPendingListByType2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page) {
        Page<BcdPendingResponseDTO> bcdApplyLists = null;
        Page<DocPendingResponseDTO> docApplyLists = null;
        Page<CorpDocPendingResponseDTO> corpDocApplyLists = null;
        Page<SealPendingResponseDTO> sealApplyLists = null;
        Page<TonerPendingListResponseDTO> tonerApplyLists = null;

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

        return PendingResponseDTO.of(bcdApplyLists, docApplyLists, corpDocApplyLists, sealApplyLists, null);
    }

    @Override
    @Transactional(readOnly = true)
    public PendingCountResponseDTO getPendingCountList(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO) {

        int bcdPendingCount = Math.toIntExact(bcdPendingQueryRepository.getBcdPendingCount(applyRequestDTO, postSearchRequestDTO));
        int docPendingCount = Math.toIntExact(docPendingQueryRepository.getDocPendingCount(applyRequestDTO, postSearchRequestDTO));
        int corpDocPendingCount = Math.toIntExact(corpDocPendingQueryRepository.getCorpDocPendingCount(applyRequestDTO, postSearchRequestDTO));
        int sealPendingCount = Math.toIntExact(sealPendingQueryRepository.getSealPendingCount(applyRequestDTO, postSearchRequestDTO));
        int corpDocIssuePendingCount = corpDocListService.getCorpDocIssuePendingListCount();
        int orderPendingCount = orderService.getOrderList(applyRequestDTO.getInstCd()).size();
        int docStoragePendingCount = docstorageListService.getDocstoragePendingList(applyRequestDTO.getInstCd()).size();
        int tonerPendingCount = tonerPendingService.getTonerPendingList(applyRequestDTO.getInstCd()).size();
        int tonerOrderPendingCount = tonerOrderService.getTonerOrderList(applyRequestDTO.getInstCd()).size();

        return PendingCountResponseDTO.of(bcdPendingCount, docPendingCount, corpDocPendingCount, sealPendingCount,
                corpDocIssuePendingCount, orderPendingCount, docStoragePendingCount, tonerPendingCount, tonerOrderPendingCount);
    }

    @Override
    @Transactional(readOnly = true)
    public PendingResponseDTO getMyPendingList2(ApplyRequestDTO applyRequestDTO, Pageable pageable) {

        List<BcdPendingResponseDTO> myBcdPendingList2 = bcdService.getMyPendingList(applyRequestDTO);
        List<DocPendingResponseDTO> myDocPendingList2 = docService.getMyDocPendingList(applyRequestDTO);
        List<CorpDocPendingResponseDTO> myCorpDocPendingList2 = corpDocService.getMyPendingList(applyRequestDTO);
        List<SealPendingResponseDTO> mySealPendingList2 = sealListService.getMySealPendingList(applyRequestDTO);
        List<TonerPendingListResponseDTO> myTonerPendingList2 = tonerService.getMyTonerPendingList(applyRequestDTO);

        List<Object> combinedList = Stream.concat(
                Stream.concat(
                        Stream.concat(myBcdPendingList2.stream(), myDocPendingList2.stream()),
                        Stream.concat(myCorpDocPendingList2.stream(), mySealPendingList2.stream())
                ),
                myTonerPendingList2.stream()
        ).collect(Collectors.toList());

        Page<Object> pagedResult = returnPageResult(pageable, combinedList);

        return PendingResponseDTO.of(pagedResult);
    }

    private Page<Object> returnPageResult(Pageable pageable, List<Object> combinedList) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), combinedList.size());
        List<Object> pagedList = combinedList.subList(start, end);

        long totalCount = combinedList.size();

        return new PageImpl<>(pagedList, pageable, totalCount);
    }
}

