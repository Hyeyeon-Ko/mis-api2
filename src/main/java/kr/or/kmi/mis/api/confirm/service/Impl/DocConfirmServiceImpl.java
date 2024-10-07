package kr.or.kmi.mis.api.confirm.service.Impl;

import kr.or.kmi.mis.api.authority.model.entity.Authority;
import kr.or.kmi.mis.api.authority.repository.AuthorityRepository;
import kr.or.kmi.mis.api.bcd.model.entity.BcdMaster;
import kr.or.kmi.mis.api.bcd.repository.BcdMasterRepository;
import kr.or.kmi.mis.api.confirm.service.DocConfirmService;
import kr.or.kmi.mis.api.doc.model.entity.DocDetail;
import kr.or.kmi.mis.api.doc.model.entity.DocMaster;
import kr.or.kmi.mis.api.doc.repository.DocDetailRepository;
import kr.or.kmi.mis.api.doc.repository.DocMasterRepository;
import kr.or.kmi.mis.api.exception.EntityNotFoundException;
import kr.or.kmi.mis.api.noti.service.NotificationSendService;
import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import kr.or.kmi.mis.api.std.repository.StdDetailRepository;
import kr.or.kmi.mis.api.std.repository.StdGroupRepository;
import kr.or.kmi.mis.api.user.service.InfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DocConfirmServiceImpl implements DocConfirmService {

    private final InfoService infoService;
    private final NotificationSendService notificationSendService;
    private final DocMasterRepository docMasterRepository;
    private final DocDetailRepository docDetailRepository;
    private final AuthorityRepository authorityRepository;
    private final StdGroupRepository stdGroupRepository;
    private final StdDetailRepository stdDetailRepository;
    private final BcdMasterRepository bcdMasterRepository;

    @Override
    @Transactional
    public void confirm(String draftId, String userId) {

        // 1. 문서수발신신청 승인
        DocMaster docMaster = docMasterRepository.findById(draftId)
                .orElseThrow(() -> new EntityNotFoundException("docMaster not found: " + draftId));
        DocDetail docDetail = docDetailRepository.findById(draftId)
                .orElseThrow(() -> new EntityNotFoundException("docDetail not found: " + draftId));

        if (!docMaster.getCurrentApproverId().equals(userId)) {
            throw new IllegalArgumentException("현재 결재자가 아닙니다.");
        }
        boolean isLastApprover = docMaster.getCurrentApproverIndex() == docMaster.getApproverChain().split(", ").length - 1;

        // 승인 상태 변경
        String approverId = userId;
        String approver = infoService.getUserInfoDetail(approverId).getUserName();

        docMaster.confirm(isLastApprover ? "E" : "A", approver, approverId);

        docMaster.updateCurrentApproverIndex(docMaster.getCurrentApproverIndex() + 1);

        // 문서번호 생성해, 업데이트
        DocDetail lastDocDetail = docDetailRepository
                .findFirstByDocIdNotNullAndDivisionOrderByDocIdDesc(docDetail.getDivision()).orElse(null);
        String docId = "";
        if (lastDocDetail != null) {
            docId = createDocId(lastDocDetail.getDocId());
        } else {
            docId = createDocId("");
        }

        docDetail.updateDocId(docId);

        // 2. 알림 전송
        if(isLastApprover) {
            notificationSendService.sendDocApproval(docMaster.getDraftDate(), docMaster.getDrafterId(), docDetail.getDivision());
        }

        // 3. 팀장, 파트장, 본부장 -> ADMIN 권한 및 사이드바 권한 취소 여부 결정
        StdGroup stdGroup1 = stdGroupRepository.findByGroupCd("C002")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        String instCd = infoService.getUserInfoDetail(userId).getInstCd();
        List<StdDetail> stdDetail1 = stdDetailRepository.findByGroupCdAndEtcItem1(stdGroup1, instCd)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        boolean userIdExistsInStdDetail = stdDetail1.stream()
                .anyMatch(detail -> userId.equals(detail.getEtcItem2()) || userId.equals(detail.getEtcItem3()));

        if (userIdExistsInStdDetail) {
            return;
        }

        // 권한 취소 여부 결정
        List<BcdMaster> bcdMasterList = bcdMasterRepository.findAllByStatusAndCurrentApproverIndex("A", 0)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        List<DocMaster> docMasterList = docMasterRepository.findAllByStatusAndCurrentApproverIndex("A", 0)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        boolean shouldCancelAdminByBcd = true;
        boolean shouldCancelAdminByDoc = true;

        for (BcdMaster master : bcdMasterList) {
            if (master.getCurrentApproverId().equals(userId)) {
                shouldCancelAdminByBcd = false;
                break;
            }
        }

        for (DocMaster master : docMasterList) {
            if (master.getCurrentApproverId().equals(userId)) {
                shouldCancelAdminByDoc = false;
                break;
            }
        }

        if (shouldCancelAdminByBcd && shouldCancelAdminByDoc) {
            Authority authority = authorityRepository.findByUserIdAndDeletedtIsNull(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Not Found2"));

            // ADMIN 권한 취소
            authority.deleteAdmin(LocalDateTime.now());
            authorityRepository.save(authority);

            // 사이드바 권한 취소
            StdGroup stdGroup = stdGroupRepository.findByGroupCd("B002")
                    .orElseThrow(() -> new IllegalArgumentException("Not Found3"));
            StdDetail stdDetail = stdDetailRepository.findByGroupCdAndDetailCd(stdGroup, userId)
                    .orElseThrow(() -> new IllegalArgumentException("Not Found4"));

            stdDetailRepository.delete(stdDetail);
        }

    }

    @Override
    @Transactional
    public void delete(String draftId) {

        DocMaster docMaster = docMasterRepository.findById(draftId)
                .orElseThrow(() -> new EntityNotFoundException("docMaster not found: " + draftId));

        // 신청 취소 상태로 변경, 취소일시로 update
        docMaster.delete("F");

        docMasterRepository.save(docMaster);
    }

    public static String createDocId(String lastDocId) {

        int num = 1;
        String nowYear = String.valueOf(LocalDate.now().getYear()).substring(2);

        if (!Objects.equals(lastDocId, "")) {
            String[] parts = lastDocId.split("-");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid code format");
            }

            String year = parts[0];
            num = Integer.parseInt(parts[1]);

            if (nowYear.equals(year)) {
                num++;
            } else {
                num = 1;
            }
        }

        return nowYear + "-" + String.format("%03d", num);
    }


}
