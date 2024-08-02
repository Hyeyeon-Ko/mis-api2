package kr.or.kmi.mis.api.std.service.impl;

import jakarta.annotation.PostConstruct;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class StdBcdServiceImpl implements StdBcdService {

    private final StdGroupRepository stdGroupRepository;
    private final StdDetailRepository stdDetailRepository;

    private StdGroup center;
    private StdGroup dept;
    private StdGroup team;
    private StdGroup grade;
    private StdGroup applyStatus;

    @PostConstruct
    public void init() {
        this.center = stdGroupRepository.findById("A001").orElseThrow(() -> new NoSuchElementException("Center StdGroup not found"));
        this.dept = stdGroupRepository.findById("A002").orElseThrow(() -> new NoSuchElementException("Dept StdGroup not found"));
        this.team = stdGroupRepository.findById("A003").orElseThrow(() -> new NoSuchElementException("Team StdGroup not found"));
        this.grade = stdGroupRepository.findById("A004").orElseThrow(() -> new NoSuchElementException("Grade StdGroup not found"));
        this.applyStatus = stdGroupRepository.findById("A005").orElseThrow(() -> new NoSuchElementException("ApplyStatus StdGroup not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public StdBcdResponseDTO getAllBcdStd() {

        List<StdDetail> centerDetails = stdDetailRepository.findAllByUseAtAndGroupCd("Y", center)
                .orElseThrow(() -> new NoSuchElementException("Center details not found"));
        List<StdDetail> deptDetails = stdDetailRepository.findAllByUseAtAndGroupCd("Y", dept)
                .orElseThrow(() -> new NoSuchElementException("Dept details not found"));
        List<StdDetail> teamDetails = stdDetailRepository.findAllByUseAtAndGroupCd("Y", team)
                .orElseThrow(() -> new NoSuchElementException("Team details not found"));
        List<StdDetail> gradeDetails = stdDetailRepository.findAllByUseAtAndGroupCd("Y", grade)
                .orElseThrow(() -> new NoSuchElementException("Grade details not found"));

        return StdBcdResponseDTO.builder()
                .instInfo(StdBcdDetailResponseDTO.of(centerDetails))
                .deptInfo(StdBcdDetailResponseDTO.of(deptDetails))
                .teamInfo(StdBcdDetailResponseDTO.of(teamDetails))
                .gradeInfo(StdBcdDetailResponseDTO.of(gradeDetails))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public String getInstNm(String instCd) {
        return stdDetailRepository.findByGroupCdAndDetailCd(center, instCd)
                .orElseThrow(() -> new NoSuchElementException("Institution not found"))
                .getDetailNm();
    }

    @Override
    @Transactional(readOnly = true)
    public String getDeptNm(String deptCd) {
        return stdDetailRepository.findByGroupCdAndDetailCd(dept, deptCd)
                .orElseThrow(() -> new NoSuchElementException("Department not found"))
                .getDetailNm();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getTeamNm(String teamCd) {
        List<String> teamNms = new ArrayList<>();
        StdDetail stdDetail = stdDetailRepository.findByGroupCdAndDetailCd(team, teamCd)
                .orElseThrow(() -> new NoSuchElementException("Team not found"));
        teamNms.add(stdDetail.getDetailNm());  // 팀명
        teamNms.add(stdDetail.getEtcItem2());  // 영문팀명
        return teamNms;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getGradeNm(String gradeCd) {
        List<String> gradeNms = new ArrayList<>();
        StdDetail stdDetail = stdDetailRepository.findByGroupCdAndDetailCd(grade, gradeCd)
                .orElseThrow(() -> new NoSuchElementException("Grade not found"));
        gradeNms.add(stdDetail.getDetailNm());  // 직급/직책명
        gradeNms.add(stdDetail.getEtcItem2());  // 영문 직급/직책명
        return gradeNms;
    }

    @Override
    @Transactional(readOnly = true)
    public String getApplyStatusNm(String applyStatusCd) {
        return stdDetailRepository.findByGroupCdAndDetailCd(applyStatus, applyStatusCd)
                .orElseThrow(() -> new NoSuchElementException("ApplyStatus not found"))
                .getDetailNm();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StdStatusResponseDTO> getApplyStatus() {

        StdGroup applyStatus = stdGroupRepository.findByGroupCd("A005")
                .orElseThrow(() -> new NoSuchElementException("Apply status StdGroup not found"));
        List<StdDetail> stdDetails = stdDetailRepository.findByGroupCd(applyStatus)
                .orElseThrow(() -> new NoSuchElementException("Apply status details not found"));

        return StdStatusResponseDTO.of(stdDetails);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getBcdStdNames(BcdDetail bcdDetail) {

        List<String> names = new ArrayList<>();
        names.add(this.getInstNm(bcdDetail.getInstCd()));
        names.add(this.getDeptNm(bcdDetail.getDeptCd()));
        names.add(this.getTeamNm(bcdDetail.getTeamCd()).getFirst() + " | " + this.getTeamNm(bcdDetail.getTeamCd()).getLast());

        if (this.getGradeNm(bcdDetail.getGradeCd()).getFirst().equals("직접입력")) {
            names.add(bcdDetail.getGradeNm() + "| " + bcdDetail.getEngradeNm());
        } else {
            names.add(this.getGradeNm(bcdDetail.getGradeCd()).getFirst() + " | " + this.getGradeNm(bcdDetail.getGradeCd()).getLast());
        }

        return names;
    }

}
