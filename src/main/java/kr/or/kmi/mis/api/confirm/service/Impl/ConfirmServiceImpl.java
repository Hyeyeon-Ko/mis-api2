package kr.or.kmi.mis.api.confirm.service.Impl;

import jakarta.servlet.http.HttpServletRequest;
import kr.or.kmi.mis.api.bcd.model.entity.BcdDetail;
import kr.or.kmi.mis.api.bcd.model.entity.BcdMaster;
import kr.or.kmi.mis.api.bcd.model.response.BcdDetailResponseDTO;
import kr.or.kmi.mis.api.bcd.repository.BcdDetailRepository;
import kr.or.kmi.mis.api.bcd.repository.BcdMasterRepository;
import kr.or.kmi.mis.api.confirm.model.request.ApproveRequestDTO;
import kr.or.kmi.mis.api.confirm.model.request.DisapproveRequestDTO;
import kr.or.kmi.mis.api.confirm.service.ConfirmService;
import kr.or.kmi.mis.api.exception.EntityNotFoundException;
import kr.or.kmi.mis.api.user.service.InfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class ConfirmServiceImpl implements ConfirmService {

    private final BcdMasterRepository bcdMasterRepository;
    private final BcdDetailRepository bcdDetailRepository;
    private final InfoService infoService;

/*    @Override
    @Transactional(readOnly = true)
    public BcdDetailResponseDTO getBcdDetailInfo(Long id) {
        BcdMaster bcdMaster = bcdMasterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("BcdMaster not found"));
        String drafter = bcdMaster.getDrafter();
        BcdDetail bcdDetail = bcdDetailRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("BcdDetail not found for draft ID: " + id));

        return BcdDetailResponseDTO.of(bcdDetail, drafter);
    }*/

    /* 승인 */
    @Override
    public void approve(Long id) {
        BcdMaster bcdMaster = bcdMasterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("BcdMaster not found for draft ID: " + id));

        ApproveRequestDTO approveRequest = ApproveRequestDTO.builder()
                .approverId(infoService.getUserInfo().getUserId())
                .approver(infoService.getUserInfo().getUserName())
                .respondDate(new Timestamp(System.currentTimeMillis()))
                .status("B")
                .build();

        bcdMaster.updateApprove(approveRequest);
        bcdMasterRepository.save(bcdMaster);
    }

    /* 반려 */
    @Override
    public void disapprove(Long id, String rejectReason) {
        BcdMaster bcdMaster = bcdMasterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("BcdMaster not found for draft ID: " + id));

        DisapproveRequestDTO disapproveRequest = DisapproveRequestDTO.builder()
                .disapproverId(infoService.getUserInfo().getUserId())
                .disapprover(infoService.getUserInfo().getUserName())
                .rejectReason(rejectReason)
                .respondDate(new Timestamp(System.currentTimeMillis()))
                .status("C")
                .build();

        bcdMaster.updateDisapprove(disapproveRequest);
        bcdMasterRepository.save(bcdMaster);
    }
}
