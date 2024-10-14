package kr.or.kmi.mis.api.bcd.model.response;

import kr.or.kmi.mis.api.apply.model.response.ApprovalLineResponseDTO;
import kr.or.kmi.mis.api.bcd.model.entity.BcdMaster;
import kr.or.kmi.mis.api.user.model.response.InfoDetailResponseDTO;
import kr.or.kmi.mis.api.user.service.InfoService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class BcdMyResponseDTO {

    private String draftId;
    private String title;
    private LocalDateTime draftDate;
    private LocalDateTime respondDate;
    private String drafter;
    private String approver;
    private String disapprover;
    private String applyStatus;
    private String rejectReason;
    private String docType;
    private List<ApprovalLineResponseDTO> approvalLineResponses;

    public static BcdMyResponseDTO of(BcdMaster bcdMaster, InfoService infoService) {
        List<ApprovalLineResponseDTO> approvalLineResponses = new ArrayList<>();

        if (bcdMaster.getApproverChain() != null && !bcdMaster.getApproverChain().isEmpty()) {
            approvalLineResponses = new ArrayList<>();
            String[] approverIds = bcdMaster.getApproverChain().split(", ");

            for (String approverId : approverIds) {
                if (approverId == null || approverId.trim().isEmpty()) {
                    continue;
                }

                InfoDetailResponseDTO userInfo = infoService.getUserInfoDetail(approverId);
                approvalLineResponses.add(ApprovalLineResponseDTO.builder()
                        .userId(approverId)
                        .userName(userInfo.getUserName())
                        .roleNm(userInfo.getRoleNm())
                        .positionNm(userInfo.getPositionNm())
                        .currentApproverIndex(bcdMaster.getCurrentApproverIndex())
                        .build());
            }
        }

        return BcdMyResponseDTO.builder()
                .draftId(bcdMaster.getDraftId())
                .title(bcdMaster.getTitle())
                .draftDate(bcdMaster.getDraftDate())
                .respondDate(bcdMaster.getRespondDate())
                .drafter(bcdMaster.getDrafter())
                .approver(bcdMaster.getApprover())
                .disapprover(bcdMaster.getDisapprover())
                .applyStatus(bcdMaster.getStatus())
                .rejectReason(bcdMaster.getRejectReason())
                .docType("명함신청")
                .approvalLineResponses(approvalLineResponses)
                .build();
    }

}
