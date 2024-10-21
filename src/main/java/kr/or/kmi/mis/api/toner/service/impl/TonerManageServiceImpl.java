package kr.or.kmi.mis.api.toner.service.impl;

import kr.or.kmi.mis.api.docstorage.domain.response.CenterResponseDTO;
import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import kr.or.kmi.mis.api.std.repository.StdDetailRepository;
import kr.or.kmi.mis.api.std.repository.StdGroupRepository;
import kr.or.kmi.mis.api.toner.model.entity.TonerInfo;
import kr.or.kmi.mis.api.toner.model.entity.TonerPrice;
import kr.or.kmi.mis.api.toner.model.request.TonerAddRequestDTO;
import kr.or.kmi.mis.api.toner.model.response.CenterTonerListResponseDTO;
import kr.or.kmi.mis.api.toner.model.response.TonerExcelResponseDTO;
import kr.or.kmi.mis.api.toner.model.response.TonerTotalListResponseDTO;
import kr.or.kmi.mis.api.toner.repository.TonerInfoRepository;
import kr.or.kmi.mis.api.toner.repository.TonerPriceRepository;
import kr.or.kmi.mis.api.toner.service.TonerManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TonerManageServiceImpl implements TonerManageService {

    private final TonerPriceRepository tonerPriceRepository;
    private final StdGroupRepository stdGroupRepository;
    private final StdDetailRepository stdDetailRepository;
    private final TonerInfoRepository tonerInfoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TonerExcelResponseDTO> getTonerList(String instCd) {

        // 1. tonerInfoList 조회
        List<TonerInfo> tonerInfoList = tonerInfoRepository.findAllByInstCd(instCd);

        // 2. 모든 TonerPrice 조회
        List<TonerPrice> tonerPriceList = tonerPriceRepository.findAll();

        Map<String, TonerPrice> tonerPriceMap = tonerPriceList.stream()
                .collect(Collectors.toMap(TonerPrice::getTonerNm, Function.identity()));

        // 3. TonerPrice가 없는 경우 null 처리
        return tonerInfoList.stream()
                .map(tonerInfo -> {
                    TonerPrice tonerPrice = tonerPriceMap.get(tonerInfo.getTonerNm());
                    return TonerExcelResponseDTO.of(tonerInfo, tonerPrice);
                })
                .collect(Collectors.toList());
    }

    public List<TonerExcelResponseDTO> getCenterTonerList(String instCd) {

        // 1. TonerInfo 리스트 조회
        List<TonerInfo> tonerInfoList = tonerInfoRepository.findAllByInstCd(instCd);

        // 2. 모든 TonerPrice 조회
        List<TonerPrice> tonerPriceList = tonerPriceRepository.findAll();

        Map<String, TonerPrice> tonerPriceMap = tonerPriceList.stream()
                .collect(Collectors.toMap(TonerPrice::getTonerNm, Function.identity()));

        // 3. TonerPrice가 없는 경우 null 처리
        return tonerInfoList.stream()
                .map(tonerInfo -> {
                    TonerPrice tonerPrice = tonerPriceMap.get(tonerInfo.getTonerNm());
                    return TonerExcelResponseDTO.of(tonerInfo, tonerPrice);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
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

    @Override
    @Transactional
    public void addToner(TonerAddRequestDTO tonerAddRequestDTO) {

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
