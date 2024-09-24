package kr.or.kmi.mis.api.doc.model.response;

import kr.or.kmi.mis.api.apply.model.response.ApprovalLineResponseDTO;
import kr.or.kmi.mis.api.doc.model.entity.DocMaster;
import kr.or.kmi.mis.api.user.model.response.InfoDetailResponseDTO;
import kr.or.kmi.mis.api.user.service.InfoService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class DocMyResponseDTO {

    private Long draftId;
    private String title;
    private Timestamp draftDate;
    private Timestamp respondDate;
    private String drafter;
    private String approver;
    private String applyStatus;
    private String docType;
    private List<ApprovalLineResponseDTO> approvalLineResponses;

    public static DocMyResponseDTO of(DocMaster docMaster, String division, InfoService infoService) {

        List<ApprovalLineResponseDTO> approvalLineResponses = new ArrayList<>();

        if (docMaster.getApproverChain() != null && !docMaster.getApproverChain().isEmpty()) {
            approvalLineResponses = new ArrayList<>();
            String[] approverIds = docMaster.getApproverChain().split(", ");

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
                        .currentApproverIndex(docMaster.getCurrentApproverIndex())
                        .build());
            }
        }

        String docType = "A".equals(division) ? "문서수신" : "문서발신";

        return DocMyResponseDTO.builder()
                .draftId(docMaster.getDraftId())
                .title(docMaster.getTitle())
                .draftDate(docMaster.getDraftDate())
                .respondDate(docMaster.getRespondDate())
                .drafter(docMaster.getDrafter())
                .approver(docMaster.getApprover())
                .applyStatus(docMaster.getStatus())
                .docType(docType)
                .approvalLineResponses(approvalLineResponses)
                .build();
    }
}