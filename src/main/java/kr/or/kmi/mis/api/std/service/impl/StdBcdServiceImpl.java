package kr.or.kmi.mis.api.std.service.impl;

import kr.or.kmi.mis.api.bcd.repository.BcdDetailRepository;
import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import kr.or.kmi.mis.api.std.model.response.StdBcdResponseDTO;
import kr.or.kmi.mis.api.std.repository.StdDetailRepository;
import kr.or.kmi.mis.api.std.repository.StdGroupRepository;
import kr.or.kmi.mis.api.std.service.StdBcdService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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
                .centerInfo(centerDetails)
                .deptInfo(deptDetails)
                .teamInfo(teamDetails)
                .gradeInfo(gradeDetails)
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
    public String getTeamNm(String teamCd) {
        return stdDetailRepository.findByGroupCdAndDetailCd(team, teamCd).get().getDetailNm();
    }

    @Override
    public String getGradeNm(String gradeCd) {
        return stdDetailRepository.findByGroupCdAndDetailCd(grade, gradeCd).get().getDetailNm();
    }

/*    @Override
    @Transactional(readOnly = true)
    public List<String> getDetailNames(String detailCd) {

        Map<String, String> groupCodeMap = new HashMap<>();
        groupCodeMap.put("A001", "centerCd");
        groupCodeMap.put("A002", "deptCd");
        groupCodeMap.put("A003", "teamCd");
        groupCodeMap.put("A004", "gradeCd");

        return this.getNamesByGroupAndCodes(groupCodeMap);
    }*/

    private List<String> getNamesByGroupAndCodes(Map<String, String> groupCodeMap) {
        return groupCodeMap.entrySet().stream()
                .map(entry -> {
                    String groupCd = entry.getKey();
                    String detailCd = entry.getValue();
                    StdGroup group = stdGroupRepository.findByGroupCd(groupCd).orElseThrow(
                            () -> new RuntimeException("Group not found for code: " + groupCd));
                    StdDetail detail = stdDetailRepository.findByGroupCdAndDetailCd(group, detailCd)
                            .orElseThrow(null);

                    if(detail != null) {
                        return detail.getDetailNm();
                    } else return null;
                })
                .collect(Collectors.toList());
    }

}
