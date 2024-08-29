package kr.or.kmi.mis.api.confirm.service.Impl;

import kr.or.kmi.mis.api.seal.model.entity.SealExportDetail;
import kr.or.kmi.mis.api.seal.model.entity.SealImprintDetail;
import kr.or.kmi.mis.api.seal.model.entity.SealMaster;
import kr.or.kmi.mis.api.seal.model.response.ExportDetailResponseDTO;
import kr.or.kmi.mis.api.seal.model.response.ImprintDetailResponseDTO;
import kr.or.kmi.mis.api.seal.model.response.SealHistoryResponseDTO;
import kr.or.kmi.mis.api.seal.repository.SealExportDetailRepository;
import kr.or.kmi.mis.api.seal.repository.SealImprintDetailRepository;
import kr.or.kmi.mis.api.seal.repository.SealMasterRepository;
import kr.or.kmi.mis.api.confirm.service.SealConfirmService;
import kr.or.kmi.mis.api.user.service.InfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SealConfirmServiceImpl implements SealConfirmService {

    private final SealMasterRepository sealMasterRepository;
    private final SealImprintDetailRepository sealImprintDetailRepository;
    private final SealExportDetailRepository sealExportDetailRepository;
    private final InfoService infoService;

    @Override
    public ImprintDetailResponseDTO getImprintDetailInfo(Long draftId) {

        SealMaster sealMaster = sealMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        SealImprintDetail sealImprintDetail = sealImprintDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        return ImprintDetailResponseDTO.of(sealMaster, sealImprintDetail);
    }

    @Override
    public ExportDetailResponseDTO getExportDetailInfo(Long draftId) {

        SealMaster sealMaster = sealMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        SealExportDetail sealExportDetail = sealExportDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        return ExportDetailResponseDTO.of(sealMaster, sealExportDetail);
    }

    @Override
    public void approve(Long draftId) {

        SealMaster sealMaster = sealMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        String approver = infoService.getUserInfo().getUserName();
        String approverId = infoService.getUserInfo().getUserId();

        sealMaster.confirm("E", approver, approverId);
        sealMasterRepository.save(sealMaster);
    }

    @Override
    public void disapprove(Long draftId, String rejectReason) {

        SealMaster sealMaster = sealMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        String disapprover = infoService.getUserInfo().getUserName();
        String disapproverId = infoService.getUserInfo().getUserId();


        sealMaster.reject("C", disapprover, disapproverId, rejectReason);
        sealMasterRepository.save(sealMaster);
    }
}
