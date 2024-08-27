package kr.or.kmi.mis.api.std.service.impl;

import kr.or.kmi.mis.api.exception.EntityNotFoundException;
import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import kr.or.kmi.mis.api.std.model.entity.StdDetailHist;
import kr.or.kmi.mis.api.std.model.entity.StdDetailId;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
                .orElseThrow(() -> new EntityNotFoundException("Not found: " + groupCd));

        List<StdDetail> stdDetails = stdDetailRepository.findAllByUseAtAndGroupCd("Y", stdGroup)
                .orElseThrow(() -> new EntityNotFoundException("Not found: " + StdDetail.class.getName()));

        return stdDetails.stream()
                .map(StdDetailResponseDTO::of)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StdDetailResponseDTO> getHeaderInfo(String groupCd) {

        if (groupCd == null) groupCd = "A000";

        StdGroup stdGroup = stdGroupRepository.findById("A000")
                .orElseThrow(() -> new EntityNotFoundException("Not found: " + "A000"));

        List<StdDetail> stdDetails = stdDetailRepository.findByUseAtAndGroupCdAndDetailCd("Y", stdGroup, groupCd)
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
        StdDetailId stdDetailId = new StdDetailId(stdDetailRequestDTO.getGroupCd(), stdDetailRequestDTO.getDetailCd());

        if (stdDetailRepository.existsById(stdDetailId)) {
            StdDetail existStdDetail = stdDetailRepository.findByGroupCdAndDetailCd(stdGroup, stdDetailId.getDetailCd())
                    .orElseThrow(() -> new EntityNotFoundException("Not found: " + StdDetail.class.getName()));

            if (Objects.equals(existStdDetail.getUseAt(), "N")) {
                StdDetail newStdDetail = stdDetailRequestDTO.toEntity(stdGroup);
                String userId = infoService.getUserInfo().getUserId();
                newStdDetail.setRgstrId(userId);
                newStdDetail.setRgstDt(new Timestamp(System.currentTimeMillis()));

                stdDetailRepository.save(newStdDetail);
            } else {
                throw new IllegalArgumentException("해당 중분류그룹에 이미 존재하는 DetailCd 입니다: detailCd = " + stdDetailRequestDTO.getDetailCd() + ", groupCd = " + stdDetailRequestDTO.getGroupCd());
            }
        } else {
            StdDetail stdDetail = stdDetailRequestDTO.toEntity(stdGroup);
            stdDetail.setRgstrId(infoService.getUserInfo().getUserId());
            stdDetail.setRgstDt(new Timestamp(System.currentTimeMillis()));

            stdDetailRepository.save(stdDetail);
        }
    }

    @Override
    @Transactional
    public void updateInfo(StdDetailUpdateRequestDTO stdDetailRequestDTO, String oriDetailCd) {

        // 1. 기존 기준자료 값 조회
        StdGroup stdGroup = stdGroupRepository.findById(stdDetailRequestDTO.getGroupCd())
                .orElseThrow(() -> new EntityNotFoundException("Not found: " + stdDetailRequestDTO.getGroupCd()));
        StdDetail oriStdDetail = stdDetailRepository.findByGroupCdAndDetailCd(stdGroup, oriDetailCd)
                .orElseThrow(() -> new EntityNotFoundException("Not found: " + oriDetailCd));

        // 2. 기존 기준자료 값 Hist 저장
        this.saveDetailIntoHist(oriStdDetail);

        // 3. 기준자료 수정
        Optional<StdDetail> optionalStdDetail = stdDetailRepository.findByGroupCdAndDetailCd(stdGroup, stdDetailRequestDTO.getDetailCd());
        String lstUpdtr = infoService.getUserInfo().getUserName();

        //  1) detailCd 변경 없이 상세 정보만 수정할 경우
        if(Objects.equals(stdDetailRequestDTO.getDetailCd(), oriDetailCd)) {
            oriStdDetail.update(stdDetailRequestDTO);
            oriStdDetail.setUpdtrId(lstUpdtr);
            oriStdDetail.setUpdtDt(new Timestamp(System.currentTimeMillis()));
        }
        //  2) detailCd 포함해 상세 정보를 수정할 경우
        //     - 변경 할 detailCd가 DB에 존재할 때 : 해당 데이터 update 후, 사용여부 "Y"로 변경, 기존 데이터 사용여부 "N"으로 변경
        //     - 변경 할 detailCd가 DB에 존재하지 않을 때 : 새롭게 생성한 후, 기존 데이터 사용여부 "N"으로 변경
        else {
            if (optionalStdDetail.isPresent()) {
                StdDetail stdDetail = optionalStdDetail.get();
                stdDetail.update(stdDetailRequestDTO);
                stdDetail.updateUseAt("Y");
                stdDetail.setUpdtrId(lstUpdtr);
                stdDetail.setUpdtDt(new Timestamp(System.currentTimeMillis()));
                stdDetailRepository.save(stdDetail);
            }
            else {
                StdDetail newStdDetail = StdDetail.builder()
                        .detailCd(stdDetailRequestDTO.getDetailCd())
                        .groupCd(stdGroup)
                        .detailNm(stdDetailRequestDTO.getDetailNm())
                        .etcItem1(stdDetailRequestDTO.getEtcItem1())
                        .etcItem2(stdDetailRequestDTO.getEtcItem2())
                        .etcItem3(stdDetailRequestDTO.getEtcItem3())
                        .etcItem4(stdDetailRequestDTO.getEtcItem4())
                        .etcItem5(stdDetailRequestDTO.getEtcItem5())
                        .etcItem6(stdDetailRequestDTO.getEtcItem6())
                        .etcItem7(stdDetailRequestDTO.getEtcItem7())
                        .etcItem8(stdDetailRequestDTO.getEtcItem8())
                        .etcItem9(stdDetailRequestDTO.getEtcItem9())
                        .etcItem10(stdDetailRequestDTO.getEtcItem10())
                        .etcItem11(stdDetailRequestDTO.getEtcItem11())
                        .build();
                newStdDetail.setRgstrId(oriStdDetail.getRgstrId());
                newStdDetail.setRgstDt(oriStdDetail.getRgstDt());
                newStdDetail.setUpdtrId(lstUpdtr);
                newStdDetail.setUpdtDt(new Timestamp(System.currentTimeMillis()));
                stdDetailRepository.save(newStdDetail);
            }
            oriStdDetail.updateUseAt("N");
            oriStdDetail.setUpdtrId(lstUpdtr);
            oriStdDetail.setUpdtDt(new Timestamp(System.currentTimeMillis()));
        }
        stdDetailRepository.save(oriStdDetail);
    }

    public void saveDetailIntoHist(StdDetail stdDetail) {
        StdDetailHist stdDetailHist = StdDetailHist.builder()
                .stdDetail(stdDetail)
                .build();

        stdDetailHist.setRgstrId(stdDetail.getRgstrId());
        stdDetailHist.setRgstDt(stdDetail.getRgstDt());
        stdDetailHist.setUpdtrId(stdDetail.getUpdtrId());
        stdDetailHist.setUpdtDt(stdDetail.getUpdtDt());
        stdDetailHistRepository.save(stdDetailHist);
    }

    @Override
    @Transactional
    public void deleteInfo(String groupCd, String detailCd) {

        StdGroup stdGroup = stdGroupRepository.findById(groupCd)
                .orElseThrow(() -> new EntityNotFoundException("Not found: " + StdDetail.class.getName()));
        StdDetail stdDetail = stdDetailRepository.findByGroupCdAndDetailCd(stdGroup, detailCd)
                .orElseThrow(() -> new IllegalArgumentException("해당 상세 정보 없음: detailCd = " + detailCd));

        stdDetail.updateToDd(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        this.saveDetailIntoHist(stdDetail);

        String deleter = infoService.getUserInfo().getUserId();
        stdDetail.setUpdtrId(deleter);
        stdDetail.setUpdtDt(new Timestamp(System.currentTimeMillis()));
        stdDetail.updateUseAt("N");
        stdDetailRepository.save(stdDetail);
    }

    @Override
    public StdDetailResponseDTO getSelectedInfo(String groupCd, String detailCd) {

        StdGroup stdGroup = stdGroupRepository.findById(groupCd)
                .orElseThrow(() -> new EntityNotFoundException("Not found: " + groupCd));

        StdDetail stdDetail = stdDetailRepository.findByGroupCdAndDetailCd(stdGroup, detailCd)
                .orElseThrow(() -> new EntityNotFoundException("Not found"));

        return StdDetailResponseDTO.of(stdDetail);
    }

}
