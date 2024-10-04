package kr.or.kmi.mis.api.seal.service.impl;

import kr.or.kmi.mis.api.docstorage.domain.entity.DocStorageMaster;
import kr.or.kmi.mis.api.seal.model.entity.SealImprintDetail;
import kr.or.kmi.mis.api.seal.model.entity.SealMaster;
import kr.or.kmi.mis.api.seal.model.request.ImprintRequestDTO;
import kr.or.kmi.mis.api.seal.model.request.ImprintUpdateRequestDTO;
import kr.or.kmi.mis.api.seal.model.response.SealImprintDetailResponseDTO;
import kr.or.kmi.mis.api.seal.repository.SealImprintDetailRepository;
import kr.or.kmi.mis.api.seal.repository.SealMasterRepository;
import kr.or.kmi.mis.api.seal.service.SealImprintHistoryService;
import kr.or.kmi.mis.api.seal.service.SealImprintService;
import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import kr.or.kmi.mis.api.std.repository.StdDetailRepository;
import kr.or.kmi.mis.api.std.repository.StdGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Optional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SealImprintServiceImpl implements SealImprintService {

    private final SealMasterRepository sealMasterRepository;
    private final SealImprintDetailRepository sealImprintDetailRepository;
    private final SealImprintHistoryService sealImprintHistoryService;
    private final StdGroupRepository stdGroupRepository;
    private final StdDetailRepository stdDetailRepository;

    @Override
    @Transactional
    public void applyImprint(ImprintRequestDTO imprintRequestDTO) {
        String draftId = generateDraftId();

        SealMaster sealMaster = imprintRequestDTO.toMasterEntity(draftId);
        sealMaster.setRgstrId(imprintRequestDTO.getDrafterId());
        sealMaster.setRgstDt(LocalDateTime.now());
        sealMaster = sealMasterRepository.save(sealMaster);

        SealImprintDetail sealImprintDetail = imprintRequestDTO.toDetailEntity(sealMaster.getDraftId());
        sealImprintDetail.setRgstrId(imprintRequestDTO.getDrafterId());
        sealImprintDetail.setRgstDt(LocalDateTime.now());
        sealImprintDetailRepository.save(sealImprintDetail);
    }

    private String generateDraftId() {
        Optional<SealMaster> lastSealMasterOpt = sealMasterRepository.findTopByOrderByDraftIdDesc();

        StdGroup stdGroup = stdGroupRepository.findByGroupCd("A007")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        StdDetail stdDetail = stdDetailRepository.findByGroupCdAndDetailCd(stdGroup, "D")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        if (lastSealMasterOpt.isPresent()) {
            String lastDraftId = lastSealMasterOpt.get().getDraftId();
            int lastIdNum = Integer.parseInt(lastDraftId.substring(2));
            return stdDetail.getEtcItem1() + String.format("%010d", lastIdNum + 1);
        } else {
            return stdDetail.getEtcItem1() + "0000000001";
        }
    }

    @Override
    @Transactional
    public void updateImprint(String draftId, ImprintUpdateRequestDTO imprintUpdateRequestDTO) {

        SealMaster sealMaster = sealMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        // 날인신청 상세 조회
        SealImprintDetail sealImprintDetailInfo = sealImprintDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        // 날인신청 히스토리 저장
        sealImprintHistoryService.createSealImprintHistory(sealImprintDetailInfo);

        // 날인신청 수정사항 저장
        sealImprintDetailInfo.update(imprintUpdateRequestDTO);
        sealImprintDetailInfo.setUpdtrId(sealMaster.getDrafterId());
        sealImprintDetailInfo.setUpdtDt(LocalDateTime.now());
        sealImprintDetailRepository.save(sealImprintDetailInfo);

        sealMaster.setUpdtrId(sealMaster.getDrafterId());
        sealMaster.setUpdtDt(LocalDateTime.now());
        sealMasterRepository.save(sealMaster);
    }

    @Override
    @Transactional
    public void cancelImprint(String draftId) {

        SealMaster sealMaster = sealMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        sealMaster.updateStatus("F");
        sealMasterRepository.save(sealMaster);
    }

    @Override
    public SealImprintDetailResponseDTO getSealImprintDetail(String draftId) {
        SealImprintDetail sealImprintDetail = sealImprintDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        return SealImprintDetailResponseDTO.of(sealImprintDetail);
    }
}
