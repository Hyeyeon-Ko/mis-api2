package kr.or.kmi.mis.api.doc.service.impl;

import kr.or.kmi.mis.api.doc.model.entity.DocDetail;
import kr.or.kmi.mis.api.doc.model.entity.DocMaster;
import kr.or.kmi.mis.api.doc.model.request.DocRequestDTO;
import kr.or.kmi.mis.api.doc.model.request.DocUpdateRequestDTO;
import kr.or.kmi.mis.api.doc.model.response.DocDetailResponseDTO;
import kr.or.kmi.mis.api.doc.model.response.DocMyResponseDTO;
import kr.or.kmi.mis.api.doc.model.response.DocMyPendingResponseDTO;
import kr.or.kmi.mis.api.doc.repository.DocDetailRepository;
import kr.or.kmi.mis.api.doc.repository.DocMasterRepository;
import kr.or.kmi.mis.api.doc.service.DocHistoryService;
import kr.or.kmi.mis.api.doc.service.DocService;
import kr.or.kmi.mis.api.user.service.InfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocServiceImpl implements DocService {


    private final DocMasterRepository docMasterRepository;
    private final DocDetailRepository docDetailRepository;
    private final DocHistoryService docHistoryService;
    private final InfoService infoService;

    @Override
    @Transactional
    public void applyDoc(DocRequestDTO docRequestDTO) {

        // 문서수발신
        DocMaster docMaster = docRequestDTO.toMasterEntity();
        docMaster = docMasterRepository.save(docMaster);

        // 문서수발신 상세
        Long draftId = docMaster.getDraftId();
        DocDetail docDetail = docRequestDTO.toDetailEntity(draftId);
        docDetailRepository.save(docDetail);
    }

    @Override
    @Transactional
    public void updateDocApply(Long draftId, DocUpdateRequestDTO docUpdateRequestDTO) {

        DocMaster docMaster = docMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        // 문서수발신 상세 조회
        DocDetail docDetailInfo = docDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        // 문서수발신 히스토리 저장
        docHistoryService.createDocHistory(docDetailInfo);

        // 문서수발신 수정사항 저장
        docDetailInfo.update(docUpdateRequestDTO);
        docDetailRepository.save(docDetailInfo);

        docMaster.setUpdtDt(new Timestamp(System.currentTimeMillis()));
        docMaster.setUpdtrId(docMaster.getDrafterId());
        docDetailInfo.setUpdtDt(new Timestamp(System.currentTimeMillis()));
    }

    @Override
    @Transactional
    public void cancelDocApply(Long draftId) {

        DocMaster docMaster = docMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        docMaster.updateStatus("F");
    }

    @Override
    @Transactional(readOnly = true)
    public DocDetailResponseDTO getDoc(Long draftId) {

        DocMaster docMaster = docMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        DocDetail docDetail = docDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        return DocDetailResponseDTO.of(docMaster, docDetail);
    }

    @Override
    @Transactional
    public List<DocMyResponseDTO> getMyDocApplyByDateRange(Timestamp startDate, Timestamp endDate) {

        String userId = infoService.getUserInfo().getUserId();

        return new ArrayList<>(this.getMyDocMasterList(userId, startDate, endDate));
    }

    public List<DocMyResponseDTO> getMyDocMasterList(String userId, Timestamp startDate, Timestamp endDate) {

        List<DocMaster> docMasterList = docMasterRepository.findByDrafterIdAndDraftDateBetween(userId, startDate, endDate)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        return docMasterList.stream()
                .map(DocMyResponseDTO::of).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocMyPendingResponseDTO> getMyDocPendingList() {

        String userId = infoService.getUserInfo().getUserId();

        return new ArrayList<>(this.getMyDocPendingMasterList(userId));
    }

    public List<DocMyPendingResponseDTO> getMyDocPendingMasterList(String userId) {
        List<DocMaster> docMasterList = docMasterRepository.findByDrafterIdAndStatus(userId, "A")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        return docMasterList.stream()
                .map(DocMyPendingResponseDTO::of).toList();
    }
}
