package kr.or.kmi.mis.api.std.service.impl;

import kr.or.kmi.mis.api.exception.EntityNotFoundException;
import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import kr.or.kmi.mis.api.std.model.entity.StdDetailHist;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import kr.or.kmi.mis.api.std.model.request.StdDetailRequestDTO;
import kr.or.kmi.mis.api.std.model.request.StdDetailUpdateRequestDTO;
import kr.or.kmi.mis.api.std.model.response.StdDetailResponseDTO;
import kr.or.kmi.mis.api.std.repository.StdDetailHistRepository;
import kr.or.kmi.mis.api.std.repository.StdDetailRepository;
import kr.or.kmi.mis.api.std.repository.StdGroupRepository;
import kr.or.kmi.mis.api.std.service.StdDetailService;
import kr.or.kmi.mis.api.user.service.InfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StdDetailServiceImpl implements StdDetailService {

    private final StdGroupRepository stdGroupRepository;
    private final StdDetailRepository stdDetailRepository;
    private final StdDetailHistRepository stdDetailHistRepository;
    private final InfoService infoService;

    @Override
    @Transactional(readOnly = true)
    public List<StdDetailResponseDTO> getInfo(String groupCd) {

        StdGroup stdGroup = stdGroupRepository.findById(groupCd)
                .orElseThrow(() -> new EntityNotFoundException("Not found: " + StdDetail.class.getName()));

        List<StdDetail> stdDetails = stdDetailRepository.findAllByUseAtAndGroupCd("Y", stdGroup)
                .orElseThrow(() -> new EntityNotFoundException("Not found: " + StdDetail.class.getName()));

        return stdDetails.stream()
                .map(StdDetailResponseDTO::of)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public void addInfo(StdDetailRequestDTO stdDetailRequestDTO) {

        StdGroup stdGroup = stdGroupRepository.findById(stdDetailRequestDTO.getGroupCd())
                .orElseThrow(() -> new IllegalArgumentException("해당 그룹 정보 없음: groupCd = " + stdDetailRequestDTO.getGroupCd()));

        StdDetail stdDetail = stdDetailRequestDTO.toEntity(stdGroup);

        String fstRegisterId = infoService.getUserInfo().getUserId();
        stdDetail.setRgstrId(fstRegisterId);
        stdDetail.setRgstDt(new Timestamp(System.currentTimeMillis()));
        stdDetailRepository.save(stdDetail);
    }

    @Override
    @Transactional
    public void updateInfo(StdDetailUpdateRequestDTO stdDetailRequestDTO) {

        // 1. 기존 기준자료 값 조회
        StdDetail oriStdDetail = stdDetailRepository.findById(stdDetailRequestDTO.getDetailCd())
                .orElseThrow(() -> new IllegalArgumentException("해당 상세 정보 없음: detailCd = " + stdDetailRequestDTO.getDetailCd()));

        // 2. 기준자료 hist 생성 후, 수정된 내용 반영해 update
        StdDetailHist stdDetailHist = StdDetailHist.builder()
                .stdDetail(oriStdDetail)
                .build();

        stdDetailHist.setRgstrId(oriStdDetail.getRgstrId());
        stdDetailHist.setRgstDt(oriStdDetail.getRgstDt());
        stdDetailHist.setUpdtrId(oriStdDetail.getUpdtrId());
        stdDetailHist.setUpdtDt(oriStdDetail.getUpdtDt());
        stdDetailHistRepository.save(stdDetailHist);

        String lstUpdtr = infoService.getUserInfo().getUserName();
        oriStdDetail.update(stdDetailRequestDTO);
        oriStdDetail.setUpdtrId(lstUpdtr);
        oriStdDetail.setUpdtDt(new Timestamp(System.currentTimeMillis()));
        stdDetailRepository.save(oriStdDetail);

    }

    @Override
    @Transactional
    public void deleteInfo(String groupCd, String detailCd) {

        StdGroup stdGroup = stdGroupRepository.findById(groupCd)
                .orElseThrow(() -> new EntityNotFoundException("Not found: " + StdDetail.class.getName()));

        StdDetail stdDetail = stdDetailRepository.findByGroupCdAndDetailCd(stdGroup, detailCd)
                .orElseThrow(() -> new IllegalArgumentException("해당 상세 정보 없음: detailCd = " + detailCd));

        stdDetail.updateUseAt("N");
        stdDetailRepository.save(stdDetail);
    }
}
