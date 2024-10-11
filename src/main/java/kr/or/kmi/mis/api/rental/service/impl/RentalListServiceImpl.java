package kr.or.kmi.mis.api.rental.service.impl;

import kr.or.kmi.mis.api.docstorage.domain.response.CenterResponseDTO;
import kr.or.kmi.mis.api.rental.model.entity.RentalDetail;
import kr.or.kmi.mis.api.rental.model.response.CenterRentalListResponseDTO;
import kr.or.kmi.mis.api.rental.model.response.RentalResponseDTO;
import kr.or.kmi.mis.api.rental.model.response.RentalSummaryResponseDTO;
import kr.or.kmi.mis.api.rental.model.response.RentalTotalListResponseDTO;
import kr.or.kmi.mis.api.rental.repository.RentalDetailRepository;
import kr.or.kmi.mis.api.rental.service.RentalListService;
import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import kr.or.kmi.mis.api.std.repository.StdDetailRepository;
import kr.or.kmi.mis.api.std.repository.StdGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RentalListServiceImpl implements RentalListService {

    private final RentalDetailRepository rentalDetailRepository;
    private final StdDetailRepository stdDetailRepository;
    private final StdGroupRepository stdGroupRepository;

    @Override
    public List<RentalResponseDTO> getCenterRentalList(String instCd) {
        // instCd에 해당하는 모든 렌탈 내역을 가져옴
        List<RentalDetail> rentalDetailList = rentalDetailRepository.findByInstCd(instCd)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        LocalDateTime lastUpdateDate = rentalDetailList.stream()
                .flatMap(detail -> Stream.of(detail.getUpdtDt(), detail.getRgstDt()))
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        String lastUpdtDate = (lastUpdateDate != null) ? lastUpdateDate.toLocalDate().toString() : null;

        return rentalDetailList.stream()
                .map(detail -> RentalResponseDTO.of(detail, lastUpdtDate))
                .collect(Collectors.toList());
    }

    // 추가된 메서드: instCd와 status가 E인 내역만 가져옴
    public List<RentalResponseDTO> getCenterRentalListByStatus(String instCd, String status) {
        List<RentalDetail> rentalDetailList = rentalDetailRepository.findByInstCdAndStatus(instCd, status)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        LocalDateTime lastUpdateDate = rentalDetailList.stream()
                .flatMap(detail -> Stream.of(detail.getUpdtDt(), detail.getRgstDt()))
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        String lastUpdtDate = (lastUpdateDate != null) ? lastUpdateDate.toLocalDate().toString() : null;

        return rentalDetailList.stream()
                .map(detail -> RentalResponseDTO.of(detail, lastUpdtDate))
                .collect(Collectors.toList());
    }

    @Override
    public RentalTotalListResponseDTO getTotalRentalList() {
        List<CenterResponseDTO> centerList = fetchAllCenters();

        List<RentalResponseDTO> foundationResponses = getCenterRentalListByStatus("100", "E");
        List<RentalResponseDTO> bonwonResponses = getCenterRentalListByStatus("111", "E");
        List<RentalResponseDTO> yeouidoResponses = getCenterRentalListByStatus("112", "E");
        List<RentalResponseDTO> gangnamResponses = getCenterRentalListByStatus("113", "E");
        List<RentalResponseDTO> gwanghwamunResponses = getCenterRentalListByStatus("119", "E");
        List<RentalResponseDTO> suwonResponses = getCenterRentalListByStatus("211", "E");
        List<RentalResponseDTO> daeguResponses = getCenterRentalListByStatus("611", "E");
        List<RentalResponseDTO> busanResponses = getCenterRentalListByStatus("612", "E");
        List<RentalResponseDTO> gwangjuResponses = getCenterRentalListByStatus("711", "E");
        List<RentalResponseDTO> jejuResponses = getCenterRentalListByStatus("811", "E");

        List<RentalSummaryResponseDTO> summary = List.of(
                createSummary("재단본부", foundationResponses),
                createSummary("본원센터", bonwonResponses),
                createSummary("여의도", yeouidoResponses),
                createSummary("강남센터", gangnamResponses),
                createSummary("광화문", gwanghwamunResponses),
                createSummary("수원센터", suwonResponses),
                createSummary("대구센터", daeguResponses),
                createSummary("부산센터", busanResponses),
                createSummary("광주센터", gwangjuResponses),
                createSummary("제주센터", jejuResponses)
        );

        // 전국 센터 합계 계산
        RentalSummaryResponseDTO totalSummary = createTotalSummary(summary);

        summary = new ArrayList<>(summary);
        summary.add(totalSummary);

        CenterRentalListResponseDTO centerRentalResponses = CenterRentalListResponseDTO.of(
                foundationResponses, bonwonResponses, yeouidoResponses, gangnamResponses, gwanghwamunResponses,
                suwonResponses, daeguResponses, busanResponses, gwangjuResponses, jejuResponses
        );

        List<CenterRentalListResponseDTO> centerRentalResponsesList = List.of(centerRentalResponses);

        return RentalTotalListResponseDTO.of(centerList, centerRentalResponsesList, summary);
    }

    // 새로운 메서드: 전국 센터 합계를 계산
    private RentalSummaryResponseDTO createTotalSummary(List<RentalSummaryResponseDTO> summaryList) {
        int totalWaterPurifiers = 0;
        int totalAirPurifiers = 0;
        int totalBidets = 0;
        double totalRentalFee = 0.0;

        for (RentalSummaryResponseDTO summary : summaryList) {
            totalWaterPurifiers += summary.getWaterPurifier();
            totalAirPurifiers += summary.getAirPurifier();
            totalBidets += summary.getBidet();
            totalRentalFee += summary.getMonthlyRentalFee();
        }

        return RentalSummaryResponseDTO.builder()
                .center("합계")
                .waterPurifier(totalWaterPurifiers)
                .airPurifier(totalAirPurifiers)
                .bidet(totalBidets)
                .monthlyRentalFee((int) totalRentalFee)
                .build();
    }

    private RentalSummaryResponseDTO createSummary(String centerName, List<RentalResponseDTO> rentalResponses) {
        int waterPurifierCount = 0;
        int airPurifierCount = 0;
        int bidetCount = 0;
        double totalRentalFee = 0.0;

        for (RentalResponseDTO detail : rentalResponses) {
            switch (detail.getCategory()) {
                case "정수기":
                    waterPurifierCount++;
                    break;
                case "공기청정기":
                    airPurifierCount++;
                    break;
                case "비데":
                    bidetCount++;
                    break;
            }
            try {
                totalRentalFee += Double.parseDouble(detail.getRentalFee().replace(",", ""));
            } catch (NumberFormatException e) {
                totalRentalFee += 0.0;
            }
        }

        return RentalSummaryResponseDTO.builder()
                .center(centerName)
                .waterPurifier(waterPurifierCount)
                .airPurifier(airPurifierCount)
                .bidet(bidetCount)
                .monthlyRentalFee((int) totalRentalFee)
                .build();
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
