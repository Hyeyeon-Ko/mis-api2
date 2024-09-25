package kr.or.kmi.mis.api.seal.service.impl;

import kr.or.kmi.mis.api.bcd.model.entity.BcdDetail;
import kr.or.kmi.mis.api.seal.model.entity.SealExportDetail;
import kr.or.kmi.mis.api.seal.model.entity.SealImprintDetail;
import kr.or.kmi.mis.api.seal.model.entity.SealMaster;
import kr.or.kmi.mis.api.seal.model.response.*;
import kr.or.kmi.mis.api.seal.repository.SealExportDetailRepository;
import kr.or.kmi.mis.api.seal.repository.SealImprintDetailRepository;
import kr.or.kmi.mis.api.seal.repository.SealMasterRepository;
import kr.or.kmi.mis.api.seal.repository.SealRegisterDetailRepository;
import kr.or.kmi.mis.api.seal.service.SealListService;
import kr.or.kmi.mis.api.std.service.StdBcdService;
import kr.or.kmi.mis.api.user.service.InfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static kr.or.kmi.mis.api.doc.service.impl.DocListServiceImpl.dateSet;

@Service
@RequiredArgsConstructor
public class SealListServiceImpl implements SealListService {

    private final SealMasterRepository sealMasterRepository;
    private final SealImprintDetailRepository sealImprintDetailRepository;
    private final SealExportDetailRepository sealExportDetailRepository;
    private final SealRegisterDetailRepository sealRegisterDetailRepository;
    private final StdBcdService stdBcdService;

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
    public List<ExportListResponseDTO> getSealExportList(String searchType, String keyword, String instCd) {

        List<SealMaster> sealMasters = sealMasterRepository.findAllByStatusAndDivisionAndInstCd("E", "B", instCd).orElse(Collections.emptyList());

        sealMasters = sealMasters.stream()
                .filter(sealMaster -> {
                    SealExportDetail sealExportDetail = sealExportDetailRepository.findById(sealMaster.getDraftId())
                            .orElseThrow(() -> new IllegalArgumentException("SealExportDetail not found for draftId: " + sealMaster.getDraftId()));

                    boolean matchesSearchType = true;

                    if (searchType != null && keyword != null && !keyword.isEmpty()) {
                        matchesSearchType = switch (searchType) {
                            case "전체" -> sealExportDetail.getExpDate().contains(keyword) ||
                                    sealExportDetail.getReturnDate().contains(keyword) ||
                                    sealExportDetail.getPurpose().contains(keyword);
                            case "반출일자" -> sealExportDetail.getExpDate().contains(keyword);
                            case "반납일자" -> sealExportDetail.getReturnDate().contains(keyword);
                            case "사용목적" -> sealExportDetail.getPurpose().contains(keyword);
                            default -> true;
                        };
                    }
                    return matchesSearchType;
                })
                .toList();

        return sealMasters.stream()
                .map(sealMaster -> {
                    SealExportDetail sealExportDetail = sealExportDetailRepository.findById(sealMaster.getDraftId())
                            .orElseThrow(() -> new IllegalArgumentException("SealExportDetail not found for draftId: " + sealMaster.getDraftId()));

                    return ExportListResponseDTO.of(sealExportDetail, sealMaster.getDrafter());
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistrationListResponseDTO> getSealRegistrationList(String instCd) {
        return sealRegisterDetailRepository.findAllByInstCd(instCd)
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
    public List<TotalRegistrationListResponseDTO> getTotalSealRegistrationList() {
        return sealRegisterDetailRepository.findAll()
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
    public List<SealMasterResponseDTO> getSealApply(Timestamp startDate, Timestamp endDate, String searchType, String keyword, String instCd) {
        List<SealMaster> sealMasters = sealMasterRepository
                .findAllByStatusNotAndInstCdAndDraftDateBetweenOrderByDraftDateDesc("F", instCd, startDate, endDate);

        if (sealMasters == null) {
            sealMasters = new ArrayList<>();
        }

        return sealMasters.stream()
                .filter(sealMaster -> {
                    if (searchType != null && keyword != null) {
                        return switch (searchType) {
                            case "전체" ->
                                    sealMaster.getTitle().contains(keyword) || sealMaster.getDrafter().contains(keyword);
                            case "제목" -> sealMaster.getTitle().contains(keyword);
                            case "신청자" -> sealMaster.getDrafter().contains(keyword);
                            default -> true;
                        };
                    }
                    return true;
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
    public List<SealPendingResponseDTO> getSealPendingList(Timestamp startDate, Timestamp endDate, String instCd) {
        List<SealMaster> sealMasters = sealMasterRepository
                .findAllByStatusAndInstCdAndDraftDateBetweenOrderByDraftDateDesc("A", instCd, startDate, endDate);

        return sealMasters.stream()
                .map(SealPendingResponseDTO::of).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SealMyResponseDTO> getMySealApply(Timestamp startDate, Timestamp endDate, String userId) {
        return new ArrayList<>(this.getMySealMasterList(userId, startDate, endDate));
    }

    public List<SealMyResponseDTO> getMySealMasterList(String userId, Timestamp startDate, Timestamp endDate) {
        List<SealMaster> sealMasterList = sealMasterRepository.findByDrafterIdAndDraftDateBetween(userId, startDate, endDate)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        return sealMasterList.stream()
                .map(SealMyResponseDTO::of).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SealPendingResponseDTO> getMySealPendingList(String userId) {
        return new ArrayList<>(this.getMySealPendingMasterList(userId));
    }

    public List<SealPendingResponseDTO> getMySealPendingMasterList(String userId) {
        List<SealMaster> sealMasterList = sealMasterRepository.findByDrafterIdAndStatus(userId, "A")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        return sealMasterList.stream()
                .map(SealPendingResponseDTO::of).toList();
    }

}
