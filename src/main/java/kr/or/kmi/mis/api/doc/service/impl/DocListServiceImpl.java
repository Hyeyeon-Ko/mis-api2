package kr.or.kmi.mis.api.doc.service.impl;

import kr.or.kmi.mis.api.apply.service.Impl.ApplyServiceImpl;
import kr.or.kmi.mis.api.doc.model.entity.DocDetail;
import kr.or.kmi.mis.api.doc.model.entity.DocMaster;
import kr.or.kmi.mis.api.doc.model.response.DocResponseDTO;
import kr.or.kmi.mis.api.doc.repository.DocDetailRepository;
import kr.or.kmi.mis.api.doc.repository.DocMasterRepository;
import kr.or.kmi.mis.api.doc.service.DocListService;
import kr.or.kmi.mis.api.docstorage.domain.response.DeptResponseDTO;
import kr.or.kmi.mis.api.file.model.entity.FileDetail;
import kr.or.kmi.mis.api.file.model.entity.FileHistory;
import kr.or.kmi.mis.api.file.repository.FileDetailRepository;
import kr.or.kmi.mis.api.file.repository.FileHistoryRepository;
import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import kr.or.kmi.mis.api.std.repository.StdDetailRepository;
import kr.or.kmi.mis.api.std.repository.StdGroupRepository;
import kr.or.kmi.mis.api.std.service.StdBcdService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DocListServiceImpl implements DocListService {

    private final DocMasterRepository docMasterRepository;
    private final DocDetailRepository docDetailRepository;
    private final StdBcdService stdBcdService;
    private final StdDetailRepository stdDetailRepository;
    private final StdGroupRepository stdGroupRepository;
    private final FileDetailRepository fileDetailRepository;
    private final FileHistoryRepository fileHistoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DocResponseDTO> getReceiveApplyList(LocalDateTime startDate, LocalDateTime endDate, String searchType, String keyword, String instCd) {

//        Timestamp[] timestamps = getDateIntoTimestamp(startDate, endDate);

        List<DocDetail> docDetails = docDetailRepository.findAllByDocIdNotNullAndDivision("A");
        if (docDetails == null) {
            return Collections.emptyList();
        }

        return docDetails.stream()
                .filter(docDetail -> {
                    DocMaster docMaster = docMasterRepository.findByDraftIdAndInstCdAndDraftDateBetweenOrderByDraftDateDesc(docDetail.getDraftId(), instCd, startDate, endDate).orElse(null);
                    if (docMaster == null) {
                        return false;
                    }

                    boolean matchesSearchType = true;
                    if (searchType != null) {
                        matchesSearchType = switch (searchType) {
                            case "전체" ->  docMaster.getTitle().contains(keyword) || docDetail.getSender().contains(keyword) ||
                                    docMaster.getDrafter().contains(keyword);

                            case "발신처" -> docDetail.getSender().contains(keyword);
                            case "제목" -> docDetail.getDocTitle().contains(keyword);
                            case "접수인" -> docMaster.getDrafter().contains(keyword);
                            default -> true;
                        };
                    }

                    return matchesSearchType;
                })
                .map(docDetail -> {
                    DocMaster docMaster = docMasterRepository.findByDraftIdAndInstCd(docDetail.getDraftId(), instCd)
                            .orElseThrow(() -> new IllegalArgumentException("Not Found"));
                    FileDetail fileDetail = fileDetailRepository.findByDraftId(docMaster.getDraftId())
                            .orElse(null);
                    FileHistory fileHistory = null;
                    if (fileDetail != null) {
                        fileHistory = fileHistoryRepository.findTopByAttachIdOrderBySeqIdDesc(fileDetail.getAttachId())
                                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
                    }
                    DocResponseDTO docResponseDTO = DocResponseDTO.rOf(docDetail, docMaster, fileHistory);
                    docResponseDTO.setStatus(stdBcdService.getApplyStatusNm(docMaster.getStatus()));
                    return docResponseDTO;
                }).toList();
    }

    @Override
    public List<DocResponseDTO> getDeptReceiveApplyList(String deptCd) {

        List<DocMaster> docMasters = docMasterRepository.findAllByDeptCd(deptCd);
        if (docMasters == null) {
            return Collections.emptyList();
        }

        return docMasters.stream()
                .map(docMaster -> {
                    DocDetail docDetail = docDetailRepository.findByDraftIdAndDivision(docMaster.getDraftId(), "A")
                            .orElseThrow(() -> new IllegalArgumentException("Not Found"));
                    FileDetail fileDetail = fileDetailRepository.findByDraftId(docMaster.getDraftId())
                            .orElse(null);
                    FileHistory fileHistory = null;
                    if (fileDetail != null) {
                        fileHistory = fileHistoryRepository.findTopByAttachIdOrderBySeqIdDesc(fileDetail.getAttachId())
                                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
                    }
                    DocResponseDTO docResponseDTO = DocResponseDTO.rOf(docDetail, docMaster, fileHistory);
                    docResponseDTO.setStatus(stdBcdService.getApplyStatusNm(docMaster.getStatus()));
                    return docResponseDTO;
                }).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocResponseDTO> getSendApplyList(LocalDateTime startDate, LocalDateTime endDate, String searchType, String keyword, String instCd) {

//        Timestamp[] timestamps = getDateIntoTimestamp(startDate, endDate);

        List<DocDetail> docDetails = docDetailRepository.findAllByDocIdNotNullAndDivision("B");
        if (docDetails == null) {
            return Collections.emptyList();
        }

        return docDetails.stream()
                .filter(docDetail -> {
                    DocMaster docMaster = docMasterRepository.findByDraftIdAndInstCdAndDraftDateBetweenOrderByDraftDateDesc(docDetail.getDraftId(), instCd, startDate, endDate).orElse(null);
                    if (docMaster == null) {
                        return false;
                    }

                    boolean matchesSearchType = true;
                    if (searchType != null && keyword != null) {
                        matchesSearchType = switch (searchType) {
                            case "전체" -> docDetail.getReceiver().contains(keyword) || docMaster.getTitle().contains(keyword)
                                    || docMaster.getDrafter().contains(keyword);
                            case "수신처" -> docDetail.getReceiver().contains(keyword);
                            case "제목" -> docDetail.getDocTitle().contains(keyword);
                            case "접수인" -> docMaster.getDrafter().contains(keyword);
                            default -> true;
                        };
                    }

                    return matchesSearchType;
                })
                .map(docDetail -> {
                    DocMaster docMaster = docMasterRepository.findByDraftIdAndInstCd(docDetail.getDraftId(), instCd)
                            .orElseThrow(() -> new IllegalArgumentException("Not Found"));
                    FileDetail fileDetail = fileDetailRepository.findByDraftId(docMaster.getDraftId())
                            .orElse(null);
                    FileHistory fileHistory = null;
                    if (fileDetail != null) {
                        fileHistory = fileHistoryRepository.findTopByAttachIdOrderBySeqIdDesc(fileDetail.getAttachId())
                                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
                    }
                    DocResponseDTO docResponseDTO = DocResponseDTO.sOf(docDetail, docMaster, fileHistory);
                    docResponseDTO.setStatus(stdBcdService.getApplyStatusNm(docMaster.getStatus()));
                    return docResponseDTO;
                }).toList();
    }

    @Override
    public List<DeptResponseDTO> getDeptList(String instCd) {
        StdGroup stdGroup = stdGroupRepository.findByGroupCd("A002")
                .orElseThrow(() -> new IllegalArgumentException("Standard Group not found for code: " + "A002"));
        List<StdDetail> stdDetailList = stdDetailRepository.findByGroupCdAndEtcItem1(stdGroup, instCd)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        return stdDetailList.stream()
                .map(stdDetail -> DeptResponseDTO.builder()
                        .detailCd(stdDetail.getDetailCd())
                        .detailNm(stdDetail.getDetailNm())
                        .build())
                .toList();
    }

//    public static Timestamp[] getDateIntoTimestamp(LocalDateTime startDate, LocalDateTime endDate) {
//        return ApplyServiceImpl.getDateIntoTimestamp(startDate, endDate);
//    }
}