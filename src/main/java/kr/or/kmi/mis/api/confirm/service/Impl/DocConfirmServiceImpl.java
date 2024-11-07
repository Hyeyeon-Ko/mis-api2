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
import kr.or.kmi.mis.api.user.service.EmailService;
import kr.or.kmi.mis.api.user.service.InfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DocConfirmServiceImpl implements DocConfirmService {

    private final InfoService infoService;
    private final EmailService emailService;
    private final NotificationSendService notificationSendService;
    private final DocMasterRepository docMasterRepository;
    private final DocDetailRepository docDetailRepository;
    private final AuthorityRepository authorityRepository;
    private final StdGroupRepository stdGroupRepository;
    private final StdDetailRepository stdDetailRepository;
    private final BcdMasterRepository bcdMasterRepository;

    /**
     * DocMaster 엔티티를 draftId로 조회하여 반환.
     * @param draftId 결재 문서 ID
     * @return DocMaster 에티티
     */
    private DocMaster getDocMaster(String draftId) {
        return docMasterRepository.findById(draftId)
                .orElseThrow(() -> new EntityNotFoundException("DocMaster not found for draft Id: " + draftId));
    }

    /**
     * DocDetail 엔티티를 draftId로 조회하여 반환.
     * @param draftId 결재 문서 ID
     * @return DocDetail 엔티티
     */
    private DocDetail getDocDetail(String draftId) {
        return docDetailRepository.findById(draftId)
                .orElseThrow(() -> new EntityNotFoundException("DocDetail not found for draft Id: " + draftId));
    }

    /**
     * 결재 승인 처리.
     * @param draftId 결재 문서 ID
     * @param userId 결재 요청 유저 ID
     */
    @Override
    @Transactional
    public void confirm(String draftId, String userId) {

        // 1. 문서수발신신청 승인
        DocMaster docMaster = getDocMaster(draftId);
        DocDetail docDetail = getDocDetail(draftId);
        validateApprover(docMaster, userId);
        boolean isLastApprover = checkLastApprover(docMaster);

        String approver = infoService.getUserInfoDetail(userId).getUserName();
        String drafterEmail = infoService.getUserInfoDetail(docMaster.getDrafterId()).getEmail();

        // 2. 팀장, 파트장, 본부장 -> ADMIN 권한 및 사이드바 권한 취소 여부 결정
        String instCd = infoService.getUserInfoDetail(docMaster.getCurrentApproverId()).getInstCd();
        if (existsInStdDetail(docMaster.getCurrentApproverId(), "B005", instCd)) {
            return;
        }

        // 권한 취소 여부 결정
        List<BcdMaster> bcdMasterList = bcdMasterRepository.findAllByStatusAndCurrentApproverIndex("A", 0)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        List<DocMaster> docMasterList = docMasterRepository.findAllByStatusAndCurrentApproverIndex("A", 0)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        boolean shouldCancelAdminByBcd = shouldCancelAdmin(bcdMasterList, userId);
        boolean shouldCancelAdminByDoc = shouldCancelAdmin(docMasterList, userId);

        if (shouldCancelAdminByBcd && shouldCancelAdminByDoc) {
            cancelAdminAndSidebarAuthorities(userId);
        }

        docMaster.confirm(isLastApprover ? "E" : "A", approver, userId);
        docMaster.updateCurrentApproverIndex(docMaster.getCurrentApproverIndex() + 1);

        // 문서번호 생성해, 업데이트
        if (isLastApprover) {
            DocDetail lastDocDetail = docDetailRepository
                    .findFirstByDocIdNotNullAndDivisionOrderByDocIdDesc(docDetail.getDivision()).orElse(null);

            String docId = "";
            if (lastDocDetail != null) {
                docId = createDocId(lastDocDetail.getDocId());
            } else {
                docId = createDocId("");
            }

            docDetail.updateDocId(docId);
        }

        // 3. 알림 및 메일 전송
        StdGroup stdGroup = stdGroupRepository.findByGroupCd("B003")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        StdDetail stdDetail = stdDetailRepository.findByGroupCdAndDetailCd(stdGroup, "003")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        String docType = Objects.equals(docDetail.getDivision(), "A") ? "수신문서" : "발신문서";

        String mailTitle = "[승인완료] 신청하신 " + docType + "이 접수되었습니다.";
        String mailContent = "[승인완료] 신청하신 " + docType + "이 접수되었습니다.\n담당 부서를 방문해 주시기 바랍니다.\n\n아래 링크에서 확인하실 수 있습니다.\nhttp://172.16.250.87/login";

        if(isLastApprover) {
            notificationSendService.sendDocApproval(docMaster.getDraftDate(), docMaster.getDrafterId(), docDetail.getDivision());
            emailService.sendEmailWithDynamicCredentials(
                    "smtp.sirteam.net",
                    465,
                    stdDetail.getEtcItem3(),
                    // TODO: 공용 발신자 비밀번호 수정하기.
                    stdDetail.getEtcItem3(),
                    stdDetail.getEtcItem3(),
                    drafterEmail,
                    mailTitle,
                    mailContent,
                    null,
                    null,
                    null
            );
        }
    }

    /**
     * 신청 삭제 처리.
     * @param draftId
     */
    @Override
    @Transactional
    public void delete(String draftId) {

        DocMaster docMaster = getDocMaster(draftId);

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

    /**
     * 결재자가 맞는지 검증
     * @param docMaster 결재 마스터 엔티티
     * @param userId 검증할 사용자 ID
     */
    private void validateApprover(DocMaster docMaster, String userId) {
        if (!docMaster.getCurrentApproverId().equals(userId)) {
            throw new IllegalArgumentException("현재 결재자가 아닙니다.");
        }
    }

    /**
     * 마지막 결재자인지 확인
     * @param docMaster 결재 마스터 엔티티
     * @return 마지막 결재자 여부
     */
    private boolean checkLastApprover(DocMaster docMaster) {
        return docMaster.getCurrentApproverIndex() == docMaster.getApproverChain().split(", ").length - 1;
    }

    /**
     * 결재자의 StdDetail 데이터가 존재하는지 확인
     * @param approverId 결재자 ID
     * @param groupCd 그룹 코드
     * @param instCd 기관 코드
     * @return 존재 여부
     */
    private boolean existsInStdDetail(String approverId, String groupCd, String instCd) {
        StdGroup stdGroup = stdGroupRepository.findByGroupCd(groupCd)
                .orElseThrow(() -> new IllegalArgumentException("StdGroup not found"));

        List<StdDetail> stdDetails = stdDetailRepository.findByGroupCdAndEtcItem1(stdGroup, instCd)
                .orElseThrow(() -> new IllegalArgumentException("StdDetail not found"));

        return stdDetails.stream()
                .anyMatch(detail -> approverId.equals(detail.getEtcItem2()) || approverId.equals(detail.getEtcItem3()));
    }

    /**
     * 권한 취소 여부 결정
     * @param masterList 결재 마스터 리스트
     * @param approverId 결재자 ID
     * @return 권한 취소 여부
     */
    private boolean shouldCancelAdmin(List<?> masterList, String approverId) {
        return masterList.stream().noneMatch(master -> {
            if (master instanceof BcdMaster) {
                return ((BcdMaster) master).getCurrentApproverId().equals(approverId);
            } else if (master instanceof DocMaster) {
                return ((DocMaster) master).getCurrentApproverId().equals(approverId);
            }
            return false;
        });
    }

    /**
     * ADMIN 및 사이드바 권한 취소
     * @param approverId 결재자 ID
     */
    private void cancelAdminAndSidebarAuthorities(String approverId) {

        // ADMIN 권한 취소
        Authority authority = authorityRepository.findByUserId(approverId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        authorityRepository.delete(authority);

        // 사이드바 권한 취소
        StdGroup stdGroup = stdGroupRepository.findByGroupCd("B002")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        StdDetail stdDetail = stdDetailRepository.findByGroupCdAndDetailCd(stdGroup, approverId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        stdDetailRepository.delete(stdDetail);
    }
}
