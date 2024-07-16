package kr.or.kmi.mis.api.bcd.service.impl;

import jakarta.transaction.Transactional;
import kr.or.kmi.mis.api.bcd.model.entity.BcdDetail;
import kr.or.kmi.mis.api.bcd.model.entity.BcdHistory;
import kr.or.kmi.mis.api.bcd.repository.BcdHistoryRepository;
import kr.or.kmi.mis.api.bcd.service.BcdHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BcdHistoryServiceImpl implements BcdHistoryService {

    private final BcdHistoryRepository bcdHistoryRepository;

    @Override
    @Transactional
    public void createBcdHistory(BcdDetail bcdDetail) {

        // todo: 기준자료 테이블 참고, bcdDetail로부터 전달된 코드값으로 명칭 찾아옴
        String instNm = "재단본부";
        String deptNm = "디지털혁신실";
        String teamNm = "디지털헬스케어팀";
        String engTeamNm = "Digital HealthCare Team";
        String grade = "선임";
        String engGrade = "Assistant Manager";

        // BcdHistory 객체 생성
        BcdHistory bcdHistory = BcdHistory.builder()
                .bcdDetail(bcdDetail)
                .instNm(instNm)
                .deptNm(deptNm)
                .teamNm(teamNm)
                .engTeamnm(engTeamNm)
                .grade(grade)
                .engGrade(engGrade)
                .build();

        // BcdHistory 객체를 저장
        bcdHistoryRepository.save(bcdHistory);
    }
}
