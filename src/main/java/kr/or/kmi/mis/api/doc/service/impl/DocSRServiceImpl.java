package kr.or.kmi.mis.api.doc.service.impl;

import kr.or.kmi.mis.api.doc.model.entity.DocMaster;
import kr.or.kmi.mis.api.doc.model.response.DocResponseDTO;
import kr.or.kmi.mis.api.doc.repository.DocDetailRepository;
import kr.or.kmi.mis.api.doc.repository.DocMasterRepository;
import kr.or.kmi.mis.api.doc.service.DocSRService;
import kr.or.kmi.mis.api.std.service.StdBcdService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DocSRServiceImpl implements DocSRService {

    private final DocMasterRepository docMasterRepository;
    private final DocDetailRepository docDetailRepository;
    private final StdBcdService stdBcdService;

    @Override
    @Transactional(readOnly = true)
    public List<DocResponseDTO> getReceiveApplyList() {

        return docDetailRepository.findAllByDocIdNotNullAndDivision("A")
                .stream()
                .map(docDetail -> {
                    DocMaster docMaster = docMasterRepository.findById(docDetail.getDraftId()).orElse(null);
                    if(docMaster != null) {
                        DocResponseDTO docResponseDTO = DocResponseDTO.rOf(docDetail, docMaster);
                        docResponseDTO.setStatus(stdBcdService.getApplyStatusNm(docMaster.getStatus()));
                        return docResponseDTO;
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocResponseDTO> getSendApplyList() {

        return docDetailRepository.findAllByDocIdNotNullAndDivision("B")
                .stream()
                .map(docDetail -> {
                    DocMaster docMaster = docMasterRepository.findById(docDetail.getDraftId()).orElse(null);
                    if(docMaster != null) {
                        return DocResponseDTO.sOf(docDetail, docMaster);
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }
}