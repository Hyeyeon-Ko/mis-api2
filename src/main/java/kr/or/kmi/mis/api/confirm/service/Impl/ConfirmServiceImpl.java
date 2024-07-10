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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class ConfirmServiceImpl implements ConfirmService {

    private final BcdMasterRepository bcdMasterRepository;
    private final BcdDetailRepository bcdDetailRepository;
    private final HttpServletRequest request;

    @Override
    @Transactional(readOnly = true)
    public BcdDetailResponseDTO getBcdDetailInfo(Long id) {
        String drafter = bcdMasterRepository.findDrafterByDraftId(id)
                .orElseThrow(() -> new EntityNotFoundException("Drafter not found for draft ID: " + id));
        BcdDetail bcdDetail = bcdDetailRepository.findByDraftId(id)
                .orElseThrow(() -> new EntityNotFoundException("BcdDetail not found for draft ID: " + id));

        return BcdDetailResponseDTO.builder()
                .drafter(drafter)
                .userId(bcdDetail.getUserId())
                .korNm(bcdDetail.getKorNm())
                .engNm(bcdDetail.getEngNm())
                .instNm(bcdDetail.getInstNm())
                .deptNm(bcdDetail.getDeptNm())
                .teamNm(bcdDetail.getTeamNm())
                .grade(bcdDetail.getGrade())
                .extTel(bcdDetail.getExtTel())
                .faxTel(bcdDetail.getFaxTel())
                .phoneTel(bcdDetail.getPhoneTel())
                .email(bcdDetail.getEmail())
                .address(bcdDetail.getAddress())
                .division(bcdDetail.getDivision())
                .build();
    }

    /* 승인 */
    @Override
    public void approve(Long id) {
        BcdMaster bcdMaster = bcdMasterRepository.findByDraftId(id);

        // 세션에서 현재 로그인된 사용자 정보(사번, 이름) 가져오기
        String currentUserId = (String) request.getSession().getAttribute("userId");
        String currentUser = (String) request.getSession().getAttribute("hngnm") ;

        ApproveRequestDTO approveRequest = ApproveRequestDTO.builder()
                .approverId(currentUserId)
                .approver(currentUser)
                .respondDate(new Timestamp(System.currentTimeMillis()))
                .status("B")
                .build();

        bcdMaster.updateApprove(approveRequest);
    }

    /* 반려 */
    @Override
    public void disapprove(Long id, String rejectReason) {
        BcdMaster bcdMaster = bcdMasterRepository.findByDraftId(id);

        // 세션에서 현재 로그인된 사용자 정보(사번, 이름) 가져오기
        String currentUserId = (String) request.getSession().getAttribute("userId");
        String currentUser = (String) request.getSession().getAttribute("hngnm");

        DisapproveRequestDTO disapproveRequest = DisapproveRequestDTO.builder()
                .disapproverId(currentUserId)
                .disapprover(currentUser)
                .rejectReason(rejectReason)
                .respondDate(new Timestamp(System.currentTimeMillis()))
                .status("C")
                .build();

        bcdMaster.updateDisapprove(disapproveRequest);
    }
}
