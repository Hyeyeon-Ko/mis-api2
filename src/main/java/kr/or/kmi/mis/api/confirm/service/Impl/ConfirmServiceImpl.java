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
import kr.or.kmi.mis.api.std.service.StdBcdService;
import kr.or.kmi.mis.api.user.service.InfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConfirmServiceImpl implements ConfirmService {

    private final BcdMasterRepository bcdMasterRepository;
    private final BcdDetailRepository bcdDetailRepository;
    private final StdBcdService stdBcdService;
    private final InfoService infoService;

    @Override
    @Transactional(readOnly = true)
    public BcdDetailResponseDTO getBcdDetailInfo(Long id) {
        BcdMaster bcdMaster = bcdMasterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("BcdMaster not found"));
        BcdDetail bcdDetail = bcdDetailRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("BcdDetail not found for draft ID: " + id));

        String drafter = bcdMaster.getDrafter();
        // 기준자료에서 각 기준자료 코드에 해당하는 명칭 불러오기
        List<String> names = stdBcdService.getBcdStdNames(bcdDetail);

        return BcdDetailResponseDTO.of(bcdDetail, drafter, names);
    }

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
