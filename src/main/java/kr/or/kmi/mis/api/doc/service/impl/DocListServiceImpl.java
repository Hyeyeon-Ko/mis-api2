package kr.or.kmi.mis.api.doc.service.impl;

import kr.or.kmi.mis.api.doc.model.entity.DocMaster;
import kr.or.kmi.mis.api.doc.model.response.DocResponseDTO;
import kr.or.kmi.mis.api.doc.repository.DocDetailRepository;
import kr.or.kmi.mis.api.doc.repository.DocMasterRepository;
import kr.or.kmi.mis.api.doc.service.DocListService;
import kr.or.kmi.mis.api.std.service.StdBcdService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DocListServiceImpl implements DocListService {

    private final DocMasterRepository docMasterRepository;
    private final DocDetailRepository docDetailRepository;
    private final StdBcdService stdBcdService;

    @Override
    @Transactional(readOnly = true)
    public List<DocResponseDTO> getReceiveApplyList(LocalDate startDate, LocalDate endDate) {

        LocalDate[] localDates = dateSet(startDate, endDate);

        return docDetailRepository.findAllByDocIdNotNullAndDivision("A")
                .stream()
                .map(docDetail -> {
                    DocMaster docMaster = docMasterRepository.findById(docDetail.getDraftId()).orElse(null);
                    if(docMaster != null) {
                        LocalDate draftDate = docMaster.getDraftDate().toLocalDateTime().toLocalDate();
                        if(!draftDate.isBefore(localDates[0]) && !draftDate.isAfter(localDates[1])){
                            DocResponseDTO docResponseDTO = DocResponseDTO.rOf(docDetail, docMaster);
                            docResponseDTO.setStatus(stdBcdService.getApplyStatusNm(docMaster.getStatus()));
                            return docResponseDTO;
                        }
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocResponseDTO> getSendApplyList(LocalDate startDate, LocalDate endDate) {

        LocalDate[] localDates = dateSet(startDate, endDate);

        return docDetailRepository.findAllByDocIdNotNullAndDivision("B")
                .stream()
                .map(docDetail -> {
                    DocMaster docMaster = docMasterRepository.findById(docDetail.getDraftId()).orElse(null);
                    if(docMaster != null) {
                        LocalDate draftDate = docMaster.getDraftDate().toLocalDateTime().toLocalDate();
                        if(!draftDate.isBefore(localDates[0]) && !draftDate.isAfter(localDates[1])) {
                            DocResponseDTO docResponseDTO = DocResponseDTO.sOf(docDetail, docMaster);
                            docResponseDTO.setStatus(stdBcdService.getApplyStatusNm(docMaster.getStatus()));
                            return docResponseDTO;
                        }
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList();
    }

    public static LocalDate[] dateSet(LocalDate startDate, LocalDate endDate) {

        if(Objects.isNull(endDate)) {
            endDate = LocalDate.now();
        }
        if(Objects.isNull(startDate)) {
            startDate = endDate.minusMonths(1);
        }

        return new LocalDate[]{startDate, endDate};
    }
}