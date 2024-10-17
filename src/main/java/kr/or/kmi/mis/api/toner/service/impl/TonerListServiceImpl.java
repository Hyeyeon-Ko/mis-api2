package kr.or.kmi.mis.api.toner.service.impl;

import kr.or.kmi.mis.api.docstorage.domain.response.CenterResponseDTO;
import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import kr.or.kmi.mis.api.std.repository.StdDetailRepository;
import kr.or.kmi.mis.api.std.repository.StdGroupRepository;
import kr.or.kmi.mis.api.toner.model.entity.TonerInfo;
import kr.or.kmi.mis.api.toner.model.response.CenterTonerListResponseDTO;
import kr.or.kmi.mis.api.toner.model.response.TonerExcelResponseDTO;
import kr.or.kmi.mis.api.toner.model.response.TonerTotalListResponseDTO;
import kr.or.kmi.mis.api.toner.repository.TonerInfoRepository;
import kr.or.kmi.mis.api.toner.service.TonerListService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TonerListServiceImpl implements TonerListService {

    private final TonerInfoRepository tonerInfoRepository;
    private final StdGroupRepository stdGroupRepository;
    private final StdDetailRepository stdDetailRepository;

    @Override
    public List<TonerExcelResponseDTO> getTonerList(String instCd) {

        List<TonerInfo> tonerInfoList = tonerInfoRepository.findAllByInstCd(instCd);

        return tonerInfoList.stream()
                .map(TonerExcelResponseDTO::of)
                .collect(Collectors.toList());
    }

    public List<TonerExcelResponseDTO> getCenterTonerList(String instCd) {
        List<TonerInfo> tonerInfoList = tonerInfoRepository.findAllByInstCd(instCd);

        return tonerInfoList.stream()
                .map(TonerExcelResponseDTO::of)
                .collect(Collectors.toList());
    }

    @Override
    public TonerTotalListResponseDTO getTotalTonerList() {

        List<CenterResponseDTO> centerList = fetchAllCenters();

        List<TonerExcelResponseDTO> foundationResponses = getCenterTonerList("100");
        List<TonerExcelResponseDTO> bonwonResponses = getCenterTonerList("111");
        List<TonerExcelResponseDTO> yeouidoResponses = getCenterTonerList("112");
        List<TonerExcelResponseDTO> gangnamResponses = getCenterTonerList("113");
        List<TonerExcelResponseDTO> gwanghwamunResponses = getCenterTonerList("119");
        List<TonerExcelResponseDTO> suwonResponses = getCenterTonerList("211");
        List<TonerExcelResponseDTO> daeguResponses = getCenterTonerList("611");
        List<TonerExcelResponseDTO> busanResponses = getCenterTonerList("612");
        List<TonerExcelResponseDTO> gwangjuResponses = getCenterTonerList("711");
        List<TonerExcelResponseDTO> jejuResponses = getCenterTonerList("811");

        CenterTonerListResponseDTO centerTonerResponses = CenterTonerListResponseDTO.of(
                foundationResponses, bonwonResponses, yeouidoResponses, gangnamResponses, gwanghwamunResponses,
                suwonResponses, daeguResponses, busanResponses, gwangjuResponses, jejuResponses
        );

        List<CenterTonerListResponseDTO> centerTonerListResponsesList = List.of(centerTonerResponses);

        return TonerTotalListResponseDTO.of(centerList, centerTonerListResponsesList);
    }

    /* 모든 센터 정보 조회 */
    private List<CenterResponseDTO> fetchAllCenters() {
        StdGroup stdGroup = fetchStdGroup();
        List<StdDetail> stdDetailList = stdDetailRepository.findAllByUseAtAndGroupCd("Y", stdGroup)
                .orElseThrow(() -> new IllegalArgumentException("Standard Detail not found"));
        return stdDetailList.stream()
                .map(stdDetail -> CenterResponseDTO.builder()
                        .detailNm(stdDetail.getDetailNm())
                        .detailCd(stdDetail.getDetailCd())
                        .build())
                .toList();
    }

    /* 그룹 코드로 표준 그룹 조회 */
    private StdGroup fetchStdGroup() {
        return stdGroupRepository.findByGroupCd("A001")
                .orElseThrow(() -> new IllegalArgumentException("Standard Group not found for code: " + "A001"));
    }
}
