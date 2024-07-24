package kr.or.kmi.mis.api.std.service.impl;

import kr.or.kmi.mis.api.bcd.model.entity.BcdDetail;
import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import kr.or.kmi.mis.api.std.model.response.bcd.StdBcdResponseDTO;
import kr.or.kmi.mis.api.std.model.response.bcd.StdBcdDetailResponseDTO;
import kr.or.kmi.mis.api.std.model.response.bcd.StdStatusResponseDTO;
import kr.or.kmi.mis.api.std.repository.StdDetailRepository;
import kr.or.kmi.mis.api.std.repository.StdGroupRepository;
import kr.or.kmi.mis.api.std.service.StdBcdService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class StdBcdServiceImpl implements StdBcdService {

    private final StdGroupRepository stdGroupRepository;
    private final StdDetailRepository stdDetailRepository;

    private final StdGroup center;
    private final StdGroup dept;
    private final StdGroup team;
    private final StdGroup grade;

    @Autowired
    public StdBcdServiceImpl(StdGroupRepository stdGroupRepository, StdDetailRepository stdDetailRepository) {
        this.stdGroupRepository = stdGroupRepository;
        this.center = stdGroupRepository.findByGroupCd("A001").orElseThrow();
        this.dept = stdGroupRepository.findByGroupCd("A002").orElseThrow();
        this.team = stdGroupRepository.findByGroupCd("A003").orElseThrow();
        this.grade = stdGroupRepository.findByGroupCd("A004").orElseThrow();
        this.stdDetailRepository = stdDetailRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public StdBcdResponseDTO getAllBcdStd() {

        List<StdDetail> centerDetails = stdDetailRepository.findAllByUseAtAndGroupCd("Y", center).orElseThrow();
        List<StdDetail> deptDetails = stdDetailRepository.findAllByUseAtAndGroupCd("Y", dept).orElseThrow();
        List<StdDetail> teamDetails = stdDetailRepository.findAllByUseAtAndGroupCd("Y", team).orElseThrow();
        List<StdDetail> gradeDetails = stdDetailRepository.findAllByUseAtAndGroupCd("Y", grade).orElseThrow();

        return StdBcdResponseDTO.builder()
                .instInfo(StdBcdDetailResponseDTO.of(centerDetails))
                .deptInfo(StdBcdDetailResponseDTO.of(deptDetails))
                .teamInfo(StdBcdDetailResponseDTO.of(teamDetails))
                .gradeInfo(StdBcdDetailResponseDTO.of(gradeDetails))
                .build();
    }

    @Override
    public String getInstNm(String instCd) {
        return stdDetailRepository.findByGroupCdAndDetailCd(center, instCd).get().getDetailNm();
    }

    @Override
    public String getDeptNm(String deptCd) {
        return stdDetailRepository.findByGroupCdAndDetailCd(dept, deptCd).get().getDetailNm();
    }

    @Override
    public List<String> getTeamNm(String teamCd) {
        List<String> teamNms = new ArrayList<>();
        StdDetail stdDetail = stdDetailRepository.findByGroupCdAndDetailCd(team, teamCd).get();
        teamNms.add(stdDetail.getDetailNm());  // 팀명
        teamNms.add(stdDetail.getEtcItem1());  // 영문팀명
        return teamNms;
    }

    @Override
    public List<String> getGradeNm(String gradeCd) {
        List<String> gradeNms = new ArrayList<>();
        StdDetail stdDetail = stdDetailRepository.findByGroupCdAndDetailCd(grade, gradeCd).get();
        gradeNms.add(stdDetail.getDetailNm());  // 직급/직책명
        gradeNms.add(stdDetail.getEtcItem1());  // 영문 직급/직책명
        return gradeNms;
    }

    @Override
    public List<StdStatusResponseDTO> getApplyStatus() {

        StdGroup applyStatus = stdGroupRepository.findByGroupCd("A005").orElseThrow();
        List<StdDetail> stdDetails = stdDetailRepository.findByGroupCd(applyStatus).orElseThrow();

        return StdStatusResponseDTO.of(stdDetails);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getBcdStdNames(BcdDetail bcdDetail) {

        List<String> names = new ArrayList<>();
        names.add(this.getInstNm(bcdDetail.getInstCd()));
        names.add(this.getDeptNm(bcdDetail.getDeptCd()));
        names.add(this.getTeamNm(bcdDetail.getTeamCd()).getFirst() + " | " + this.getTeamNm(bcdDetail.getTeamCd()).getLast());
        names.add(this.getGradeNm(bcdDetail.getGradeCd()).getFirst() + "| " + this.getGradeNm(bcdDetail.getGradeCd()).getLast());
        Map<String, String> groupCodeMap = new HashMap<>();

        return names;
    }

}
