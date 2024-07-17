package kr.or.kmi.mis.api.std.service.impl;

import kr.or.kmi.mis.api.exception.EntityNotFoundException;
import kr.or.kmi.mis.api.std.model.entity.StdClass;
import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import kr.or.kmi.mis.api.std.model.request.StdGroupRequestDTO;
import kr.or.kmi.mis.api.std.model.response.StdGroupResponseDTO;
import kr.or.kmi.mis.api.std.repository.StdClassRepository;
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

    @Override
    @Transactional(readOnly = true)
    public List<StdGroupResponseDTO> getInfo(String classCd) {

        StdClass stdClass = stdClassRepository.findById(classCd)
                .orElseThrow(()-> new EntityNotFoundException(StdDetail.class.getName()));

        List<StdGroup> stdGroups = stdGroupRepository.findAllByClassCd(stdClass)
                .orElseThrow(() -> new EntityNotFoundException(StdDetail.class.getName()));

        return stdGroups.stream()
                .map(StdGroupResponseDTO::of)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addInfo(StdGroupRequestDTO stdGroupRequestDTO) {

        StdClass stdClass = stdClassRepository.findById(stdGroupRequestDTO.getClassCd())
                .orElseThrow(() -> new IllegalArgumentException("ggg"));

        StdGroup stdGroup = stdGroupRequestDTO.toEntity(stdClass);
        stdGroupRepository.save(stdGroup);
    }

}
