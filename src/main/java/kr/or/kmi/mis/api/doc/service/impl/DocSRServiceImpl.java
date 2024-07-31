package kr.or.kmi.mis.api.doc.service.impl;

import kr.or.kmi.mis.api.doc.model.entity.DocMaster;
import kr.or.kmi.mis.api.doc.model.response.docResponseDTO;
import kr.or.kmi.mis.api.doc.repository.DocDetailRepository;
import kr.or.kmi.mis.api.doc.repository.DocMasterRepository;
import kr.or.kmi.mis.api.doc.service.DocSRService;
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

    @Override
    @Transactional(readOnly = true)
    public List<docResponseDTO> getReceiveApplyList() {

        return docDetailRepository.findAllByDocIdNotNullAndDivision("A")
                .stream()
                .map(docDetail -> {
                    DocMaster docMaster = docMasterRepository.findById(docDetail.getDraftId()).orElse(null);
                    if(docMaster != null) {
                        return docResponseDTO.rOf(docDetail, docMaster);
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<docResponseDTO> getSendApplyList() {

        return docDetailRepository.findAllByDocIdNotNullAndDivision("B")
                .stream()
                .map(docDetail -> {
                    DocMaster docMaster = docMasterRepository.findById(docDetail.getDraftId()).orElse(null);
                    if(docMaster != null) {
                        return docResponseDTO.sOf(docDetail, docMaster);
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }
}