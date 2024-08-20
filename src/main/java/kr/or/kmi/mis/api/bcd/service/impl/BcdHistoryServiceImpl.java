package kr.or.kmi.mis.api.bcd.service.impl;

import jakarta.transaction.Transactional;
import kr.or.kmi.mis.api.bcd.model.entity.BcdDetail;
import kr.or.kmi.mis.api.bcd.model.entity.BcdHistory;
import kr.or.kmi.mis.api.bcd.repository.BcdHistoryRepository;
import kr.or.kmi.mis.api.bcd.service.BcdHistoryService;
import kr.or.kmi.mis.api.std.service.StdBcdService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BcdHistoryServiceImpl implements BcdHistoryService {

    private final BcdHistoryRepository bcdHistoryRepository;
    private final StdBcdService stdBcdService;

    @Override
    @Transactional
    public void createBcdHistory(BcdDetail bcdDetail) {

        // 기준자료에서 각각의 명칭 담아 저장
        String instNm = stdBcdService.getInstNm(bcdDetail.getInstCd());
        String deptNm = stdBcdService.getDeptNm(bcdDetail.getDeptCd());
        String teamNm = "";
        String engTeamnm = "";
        if (bcdDetail.getEngteamNm() == null) {
            // 기준자료 내 팀 정보
            teamNm = stdBcdService.getTeamNm(bcdDetail.getTeamCd()).getFirst();
            engTeamnm = stdBcdService.getTeamNm(bcdDetail.getTeamCd()).getLast();
        } else {
            // 직접 입력받은 팀명
            teamNm = bcdDetail.getTeamNm();
            engTeamnm = bcdDetail.getTeamNm();
        }
        String grade = "";
        String engGrade = "";
        if (bcdDetail.getGradeNm() == null) {
            // 기준자료 내 직급/직책 정보
            grade = stdBcdService.getGradeNm(bcdDetail.getGradeCd()).getFirst();
            engGrade = stdBcdService.getGradeNm(bcdDetail.getGradeCd()).getLast();
        } else {
            // 직접 입력받은 직급/직책 정보
            grade = bcdDetail.getGradeNm();
            engGrade = bcdDetail.getEngradeNm();
        }

        // seqId 설정
        Long maxSeqId = bcdHistoryRepository.findTopByDraftIdOrderBySeqIdDesc(bcdDetail.getDraftId())
                .map(BcdHistory::getSeqId).orElse(0L);

        // BcdHistory 객체 생성
        BcdHistory bcdHistory = BcdHistory.builder()
                .bcdDetail(bcdDetail)
                .seqId(maxSeqId + 1)
                .instNm(instNm)
                .deptNm(deptNm)
                .teamNm(teamNm)
                .engTeamnm(engTeamnm)
                .grade(grade)
                .engGrade(engGrade)
                .build();

        // BcdHistory 객체를 저장
        bcdHistoryRepository.save(bcdHistory);
    }
}
