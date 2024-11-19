package kr.or.kmi.mis.api.std.service.impl;

import kr.or.kmi.mis.api.exception.EntityNotFoundException;
import kr.or.kmi.mis.api.std.model.entity.StdClass;
import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import kr.or.kmi.mis.api.std.model.request.StdGroupRequestDTO;
import kr.or.kmi.mis.api.std.model.response.StdGroupResponseDTO;
import kr.or.kmi.mis.api.std.repository.StdClassRepository;
import kr.or.kmi.mis.api.std.repository.StdDetailRepository;
import kr.or.kmi.mis.api.std.repository.StdGroupRepository;
import kr.or.kmi.mis.api.std.service.StdGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StdGroupServiceImpl implements StdGroupService {

    private final StdClassRepository stdClassRepository;
    private final StdGroupRepository stdGroupRepository;
    private final StdDetailRepository stdDetailRepository;

    @Override
    @Transactional(readOnly = true)
    public List<StdGroupResponseDTO> getInfo(String classCd) {

        StdClass stdClass = stdClassRepository.findById(classCd)
                .orElseThrow(()-> new EntityNotFoundException("Not found: " + StdDetail.class.getName()));

        List<StdGroup> stdGroups = stdGroupRepository.findAllByClassCd(stdClass)
                .orElseThrow(() -> new EntityNotFoundException("Not found: " + StdDetail.class.getName()));

        return stdGroups.stream()
                .map(StdGroupResponseDTO::of)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addInfo(StdGroupRequestDTO stdGroupRequestDTO) {

        if (stdGroupRepository.existsById(stdGroupRequestDTO.getGroupCd())) {
            throw new IllegalArgumentException("해당 대분류그룹에 이미 존재하는 DetailCd 입니다: detailCd = " + stdGroupRequestDTO.getGroupCd());
        }

        StdClass stdClass = stdClassRepository.findById(stdGroupRequestDTO.getClassCd())
                .orElseThrow(() -> new IllegalArgumentException("Not found: " + stdGroupRequestDTO.getClassCd()));

        StdGroup stdGroup = stdGroupRequestDTO.toEntity(stdClass);
        stdGroupRepository.save(stdGroup);
    }

    @Override
    public boolean findStdGroupAndCheckFirstApprover(String groupCd, String firstApproverId) {
        StdGroup group = stdGroupRepository.findByGroupCd(groupCd)
                .orElseThrow(() -> new IllegalArgumentException("Group not found: " + groupCd));
        List<StdDetail> details = findAllActiveDetails(group);

        return details.stream()
                .anyMatch(detail -> firstApproverId.equals(detail.getEtcItem2()) || firstApproverId.equals(detail.getEtcItem3()));
    }

    private List<StdDetail> findAllActiveDetails(StdGroup group) {
        return stdDetailRepository.findAllByUseAtAndGroupCd("Y", group)
                .orElseThrow(() -> new IllegalArgumentException("No active details found for group: " + group.getGroupCd()));
    }
}
