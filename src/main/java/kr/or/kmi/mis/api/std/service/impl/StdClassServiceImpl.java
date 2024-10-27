package kr.or.kmi.mis.api.std.service.impl;

import kr.or.kmi.mis.api.std.model.entity.StdClass;
import kr.or.kmi.mis.api.std.model.request.StdClassRequestDTO;
import kr.or.kmi.mis.api.std.model.response.StdClassResponseDTO;
import kr.or.kmi.mis.api.std.repository.StdClassRepository;
import kr.or.kmi.mis.api.std.service.StdClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StdClassServiceImpl implements StdClassService {

    private final StdClassRepository stdClassRepository;

    @Override
    @Transactional(readOnly = true)
    public List<StdClassResponseDTO> getInfo() {

        List<StdClass> stdClassList = stdClassRepository.findAll();

        return stdClassList.stream()
                .map(StdClassResponseDTO::of)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addClassInfo(StdClassRequestDTO stdClassRequestDTO) {

        // 동일한 대분류코드 존재 여부 검사
        if(stdClassRepository.existsById(stdClassRequestDTO.getClassCd())) {
            throw new IllegalArgumentException("해당 대분류그룹에 이미 존재하는 DetailCd 입니다: detailCd = " + stdClassRequestDTO.getClassCd());
        }

        StdClass stdClass = stdClassRequestDTO.toEntity();
        stdClass.setRgstDt(LocalDateTime.now());
        stdClass.setRgstrId(stdClassRequestDTO.getUserId());

        stdClassRepository.save(stdClass);
    }

}
