package kr.or.kmi.mis.api.confirm.service;

import kr.or.kmi.mis.api.bcd.model.entity.BcdDetail;
import kr.or.kmi.mis.api.bcd.model.response.BcdDetailResponseDTO;
import kr.or.kmi.mis.api.bcd.repository.BcdDetailRepository;
import kr.or.kmi.mis.api.bcd.repository.BcdMasterRepository;
import kr.or.kmi.mis.api.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ConfirmService {

    private BcdMasterRepository bcdMasterRepository;
    private BcdDetailRepository bcdDetailRepository;

    /* 신청 상세 정보 불러오기 */
    @Transactional(readOnly = true)
    public BcdDetailResponseDTO getBcdDetailInfo(Long id) {

        String drafter = bcdMasterRepository.findDrafterByDraftId(id)
                .orElseThrow(() -> new EntityNotFoundException("Drafter not found for draft ID: " + id));
        BcdDetail bcdDetail = bcdDetailRepository.findByDraftId(id)
                .orElseThrow(() -> new EntityNotFoundException("BcdDetail not found for draft ID: " + id));

        BcdDetailResponseDTO bcdDetailResponseDTO = BcdDetailResponseDTO.builder()
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

        return bcdDetailResponseDTO;
    }

//    /* 로그인 관련 개발 후 구현 */
//      /* 승인 */
//    public void approve(Long id) {
//
//        BcdMaster bcdMaster = bcdMasterRepository.findByDraftId(id);
//
//        ApproveRequest approveRequest = ApproveRequest.builder()
//                // approver == 승인하는 관리자 이름
//                .approverId()
//                .approver()
//                .respondDate(new Timestamp(System.currentTimeMillis()))
//                .status("B")
//                .build();
//
//        bcdMaster.updateApprove(approveRequest);
//   }

//    /* 반려 */
//    public void reject(Long id, String rejectReason) {
//
//        BcdMaster bcdMaster = bcdMasterRepository.findByDraftId(id);
//
//        DisapproveRequest disapproveRequest = DisapproveRequest.builder()
//                // disapprover == 반려하는 관리자 이름
//                .disapproverId()
//                .disapprover()
//               .rejectReason(rejectReason)
//              .respondDate(new Timestamp(System.currentTimeMillis()))
//                .status("C")
//                .build();
//
 //       bcdMaster.updateDisapprove(disapproveRequest);
 ///   }
}
