package kr.or.kmi.mis.api.confirm.service.Impl;

import kr.or.kmi.mis.api.confirm.model.response.SealHistoryResponseDTO;
import kr.or.kmi.mis.api.confirm.service.SealConfirmService;
import kr.or.kmi.mis.api.seal.model.response.ExportDetailResponseDTO;
import kr.or.kmi.mis.api.seal.model.response.ImprintDetailResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SealConfirmServiceImpl implements SealConfirmService {

    @Override
    public ImprintDetailResponseDTO getImprintDetailInfo(Long draftId) {
        return null;
    }

    @Override
    public ExportDetailResponseDTO getExportDetailInfo(Long draftId) {
        return null;
    }

    @Override
    public void approve(Long draftId) {

    }

    @Override
    public void disapprove(Long draftId, String rejectReason) {

    }

    @Override
    public List<SealHistoryResponseDTO> getSealApplicationHistory(Long userId) {
        return List.of();
    }
}
