package kr.or.kmi.mis.api.std.service.impl;

import kr.or.kmi.mis.api.bcd.repository.BcdDetailRepository;
import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import kr.or.kmi.mis.api.std.model.response.StdBcdResponseDTO;
import kr.or.kmi.mis.api.std.repository.StdDetailRepository;
import kr.or.kmi.mis.api.std.repository.StdGroupRepository;
import kr.or.kmi.mis.api.std.service.StdBcdService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StdBcdServiceImpl implements StdBcdService {

    private final StdGroupRepository stdGroupRepository;
    private final StdDetailRepository stdDetailRepository;
    private final BcdDetailRepository bcdDetailRepository;

    @Override
    @Transactional(readOnly = true)
    public StdBcdResponseDTO getAllBcdStd() {

        StdGroup center = stdGroupRepository.findByGroupCd("A001").orElseThrow();
        StdGroup dept = stdGroupRepository.findByGroupCd("A002").orElseThrow();
        StdGroup team = stdGroupRepository.findByGroupCd("A003").orElseThrow();
        StdGroup grade = stdGroupRepository.findByGroupCd("A004").orElseThrow();

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
    @Transactional(readOnly = true)
    public List<String> getDetailNames(String detailCd) {

        Map<String, String> groupCodeMap = new HashMap<>();
        groupCodeMap.put("A001", "centerCd");
        groupCodeMap.put("A002", "deptCd");
        groupCodeMap.put("A003", "teamCd");
        groupCodeMap.put("A004", "gradeCd");

        return getNamesByGroupAndCodes(groupCodeMap);
    }

    private List<String> getNamesByGroupAndCodes(Map<String, String> groupCodeMap) {
        return groupCodeMap.entrySet().stream()
                .map(entry -> {
                    String groupCd = entry.getKey();
                    String detailCd = entry.getValue();
                    StdGroup group = stdGroupRepository.findByGroupCd(groupCd).orElseThrow(
                            () -> new RuntimeException("Group not found for code: " + groupCd));
                    return stdDetailRepository.findDetailNmByGroupCdAndDetailCd(group, detailCd);
                })
                .collect(Collectors.toList());
    }

}
