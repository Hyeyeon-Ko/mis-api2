package kr.or.kmi.mis.api.seal.service.impl;

import kr.or.kmi.mis.api.apply.model.request.ApplyRequestDTO;
import kr.or.kmi.mis.api.file.model.entity.FileDetail;
import kr.or.kmi.mis.api.file.model.entity.FileHistory;
import kr.or.kmi.mis.api.file.repository.FileDetailRepository;
import kr.or.kmi.mis.api.file.repository.FileHistoryRepository;
import kr.or.kmi.mis.api.seal.model.entity.SealExportDetail;
import kr.or.kmi.mis.api.seal.model.entity.SealImprintDetail;
import kr.or.kmi.mis.api.seal.model.entity.SealMaster;
import kr.or.kmi.mis.api.seal.model.response.*;
import kr.or.kmi.mis.api.seal.repository.*;
import kr.or.kmi.mis.api.seal.service.SealListService;
import kr.or.kmi.mis.api.std.service.StdBcdService;
import kr.or.kmi.mis.api.user.service.InfoService;
import kr.or.kmi.mis.cmm.model.request.PostSearchRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utils.SearchUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SealListServiceImpl implements SealListService {

    private final SealMasterRepository sealMasterRepository;
    private final SealImprintDetailRepository sealImprintDetailRepository;
    private final SealExportDetailRepository sealExportDetailRepository;
    private final SealRegisterDetailRepository sealRegisterDetailRepository;
    private final StdBcdService stdBcdService;
    private final InfoService infoService;
    private final FileDetailRepository fileDetailRepository;
    private final FileHistoryRepository fileHistoryRepository;

    private final SealApplyQueryRepository sealApplyQueryRepository;
    private final SealPendingQueryRepository sealPendingQueryRepository;
    private final SealListQueryRepository sealListQueryRepository;


    @Override
    public Page<SealMasterResponseDTO> getSealApply2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page) {
        return sealApplyQueryRepository.getSealApply2(applyRequestDTO, postSearchRequestDTO, page);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ManagementListResponseDTO> getSealManagementList(String searchType, String keyword, String instCd) {

        List<SealMaster> sealMasters = sealMasterRepository.findAllByStatusAndDivisionAndInstCd("E", "A", instCd).orElse(Collections.emptyList());

        sealMasters = sealMasters.stream()
                .filter(sealMaster -> {
                    SealImprintDetail sealImprintDetail = sealImprintDetailRepository.findById(sealMaster.getDraftId())
                            .orElseThrow(() -> new IllegalArgumentException("SealImprintDetail not found for draftId: " + sealMaster.getDraftId()));

                    boolean matchesSearchType = true;

                    if (searchType != null && keyword != null && !keyword.isEmpty()) {
                        matchesSearchType = switch (searchType) {
                            case "전체" -> sealImprintDetail.getUseDate().contains(keyword) ||
                                    sealImprintDetail.getSubmission().contains(keyword) ||
                                    sealImprintDetail.getPurpose().contains(keyword);
                            case "일자" -> sealImprintDetail.getUseDate().contains(keyword);
                            case "제출처" -> sealImprintDetail.getSubmission().contains(keyword);
                            case "사용목적" -> sealImprintDetail.getPurpose().contains(keyword);
                            default -> true;
                        };
                    }
                    return matchesSearchType;
                })
                .toList();

        return sealMasters.stream()
                .map(sealMaster -> {
                    SealImprintDetail sealImprintDetail = sealImprintDetailRepository.findById(sealMaster.getDraftId())
                            .orElseThrow(() -> new IllegalArgumentException("SealImprintDetail not found for draftId: " + sealMaster.getDraftId()));

                    return ManagementListResponseDTO.of(sealImprintDetail, sealMaster.getDrafter());
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ManagementListResponseDTO> getSealManagementList2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page) {
        return sealListQueryRepository.getSealManagementList(applyRequestDTO, postSearchRequestDTO, page);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExportListResponseDTO> getSealExportList(String searchType, String keyword, String instCd) {

        // 1. SealMaster 조회
        List<SealMaster> sealMasters = sealMasterRepository.findAllByStatusAndDivisionAndInstCd("E", "B", instCd)
                .orElse(Collections.emptyList());

        if (sealMasters.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> draftIds = sealMasters.stream()
                .map(SealMaster::getDraftId)
                .collect(Collectors.toList());

        // 2. SealExportDetail 일괄 조회
        List<SealExportDetail> sealExportDetails = sealExportDetailRepository.findAllById(draftIds);

        if (sealExportDetails.isEmpty()) {
            return Collections.emptyList();
        }

        // 3. 검색 필터 적용
        List<SealExportDetail> filteredSealExportDetails = sealExportDetails.stream()
                .filter(sealExportDetail -> {
                    if (searchType == null || keyword == null || keyword.isEmpty()) {
                        return true;
                    }
                    return switch (searchType) {
                        case "전체" -> containsIgnoreCase(sealExportDetail.getExpDate(), keyword) ||
                                containsIgnoreCase(sealExportDetail.getReturnDate(), keyword) ||
                                containsIgnoreCase(sealExportDetail.getPurpose(), keyword);
                        case "반출일자" -> containsIgnoreCase(sealExportDetail.getExpDate(), keyword);
                        case "반납일자" -> containsIgnoreCase(sealExportDetail.getReturnDate(), keyword);
                        case "사용목적" -> containsIgnoreCase(sealExportDetail.getPurpose(), keyword);
                        default -> true;
                    };
                })
                .toList();

        if (filteredSealExportDetails.isEmpty()) {
            return Collections.emptyList();
        }

        // 4. 검색조건에 해당하는 내역의 draftIds
        List<String> filteredDraftIds = filteredSealExportDetails.stream()
                .map(SealExportDetail::getDraftId)
                .collect(Collectors.toList());

        // 5. FileDetail 일괄 조회
        List<FileDetail> fileDetails = fileDetailRepository.findAllByDraftIdIn(filteredDraftIds);

        Map<String, FileDetail> fileDetailMap = fileDetails.stream()
                .collect(Collectors.toMap(FileDetail::getDraftId, Function.identity()));

        // 6. ExportListResponseDTO 리스트 생성
        return filteredSealExportDetails.stream()
                .map(sealExportDetail -> {
                    String draftId = sealExportDetail.getDraftId();
                    SealMaster sealMaster = sealMasters.stream()
                            .filter(sm -> sm.getDraftId().equals(draftId))
                            .findFirst()
                            .orElse(null);
                    if (sealMaster == null) {
                        return null;
                    }
                    String drafter = sealMaster.getDrafter();
                    FileDetail fileDetail = fileDetailMap.get(draftId);
                    FileHistory fileHistory = null;
                    if (fileDetail != null) {
                        fileHistory = fileHistoryRepository.findTopByAttachIdOrderBySeqIdDesc(fileDetail.getAttachId())
                                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
                    }
                    return ExportListResponseDTO.of(sealExportDetail, drafter, fileHistory);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExportListResponseDTO> getSealExportList2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page) {
        return sealListQueryRepository.getSealExportList(applyRequestDTO, postSearchRequestDTO, page);
    }

    private boolean containsIgnoreCase(String source, String target) {
        if (source == null || target == null) return false;
        return source.toLowerCase().contains(target.toLowerCase());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistrationListResponseDTO> getSealRegistrationList(String instCd) {
        return sealRegisterDetailRepository.findAllByInstCdAndDeletedtNull(instCd)
                .stream()
                .map(sealRegisterDetail -> {
                    if (sealRegisterDetail != null) {
                        return RegistrationListResponseDTO.of(sealRegisterDetail);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RegistrationListResponseDTO> getSealRegistrationList2(ApplyRequestDTO applyRequestDTO, Pageable page) {
        return sealListQueryRepository.getRegistrationList(applyRequestDTO, page);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TotalRegistrationListResponseDTO> getTotalSealRegistrationList() {
        return sealRegisterDetailRepository.findAllByDeletedtNull()
                .stream()
                .map(sealRegisterDetail -> {
                    if (sealRegisterDetail != null) {
                        return TotalRegistrationListResponseDTO.of(sealRegisterDetail);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TotalRegistrationListResponseDTO> getTotalSealRegistrationList2(Pageable page) {
        return sealListQueryRepository.getTotalSealRegistrationList(page);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SealMasterResponseDTO> getSealApply(LocalDateTime startDate, LocalDateTime endDate, String searchType, String keyword, String instCd) {
        List<SealMaster> sealMasters = sealMasterRepository
                .findAllByStatusNotAndInstCdAndDraftDateBetweenOrderByDraftDateDesc("F", instCd, startDate, endDate);

        if (sealMasters == null) {
            sealMasters = new ArrayList<>();
        }

        return sealMasters.stream()
                .filter(sealMaster -> {
                    return SearchUtils.matchesSearchCriteria(searchType,keyword, sealMaster.getTitle(), sealMaster.getDrafter());
                })
                .map(sealMaster -> {
                    SealMasterResponseDTO sealMasterResponseDTO = SealMasterResponseDTO.of(sealMaster);
                    String instNm = stdBcdService.getInstNm(sealMaster.getInstCd());
                    sealMasterResponseDTO.setInstNm(instNm);
                    return sealMasterResponseDTO;
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SealPendingResponseDTO> getSealPendingList(LocalDateTime startDate, LocalDateTime endDate, String instCd) {
        List<SealMaster> sealMasters = sealMasterRepository
                .findAllByStatusAndInstCdAndDraftDateBetweenOrderByDraftDateDesc("A", instCd, startDate, endDate);

        return sealMasters.stream()
                .map(sealMaster -> SealPendingResponseDTO.of(sealMaster, null))
                .toList();
    }

    @Override
    public Page<SealPendingResponseDTO> getSealPendingList2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page) {
        return sealPendingQueryRepository.getSealPending2(applyRequestDTO, postSearchRequestDTO, page);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SealMyResponseDTO> getMySealApply(LocalDateTime startDate, LocalDateTime endDate, String userId) {
        return new ArrayList<>(this.getMySealMasterList(userId, startDate, endDate));
    }

    @Override
    public List<SealMyResponseDTO> getMySealApply(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO) {
        return sealApplyQueryRepository.getMySealApply(applyRequestDTO, postSearchRequestDTO);
    }

    @Override
    public Page<SealMyResponseDTO> getMySealApply2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable pageable) {
        return sealApplyQueryRepository.getMySealApply2(applyRequestDTO, postSearchRequestDTO, pageable);
    }

    public List<SealMyResponseDTO> getMySealMasterList(String userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<SealMaster> sealMasterList = sealMasterRepository.findByDrafterIdAndDraftDateBetween(userId, startDate, endDate)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        return sealMasterList.stream()
                .map(SealMyResponseDTO::of).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SealPendingResponseDTO> getMySealPendingList(ApplyRequestDTO applyRequestDTO) {
        return new ArrayList<>(this.getMySealPendingMasterList(applyRequestDTO.getUserId()));
    }

    public List<SealPendingResponseDTO> getMySealPendingMasterList(String userId) {
        List<SealMaster> sealMasterList = sealMasterRepository.findByDrafterIdAndStatus(userId, "A")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        return sealMasterList.stream()
                .map(sealMaster -> {
                    String updaterId = sealMaster.getUpdtrId();
                    String updaterNm = updaterId != null ? infoService.getUserInfoDetail(updaterId).getUserName() : null;

                    return SealPendingResponseDTO.of(sealMaster, updaterNm);
                })
                .toList();
    }
}
