package kr.or.kmi.mis.api.rental.service.impl;

import kr.or.kmi.mis.api.docstorage.domain.response.CenterResponseDTO;
import kr.or.kmi.mis.api.rental.model.entity.RentalDetail;
import kr.or.kmi.mis.api.rental.model.response.CenterRentalListResponseDTO;
import kr.or.kmi.mis.api.rental.model.response.RentalResponseDTO;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RentalListServiceImpl implements RentalListService {

    private final RentalDetailRepository rentalDetailRepository;
    private final StdDetailRepository stdDetailRepository;
    private final StdGroupRepository stdGroupRepository;

    @Override
    public List<RentalResponseDTO> getCenterRentalList(String instCd) {
        List<RentalDetail> rentalDetailList = rentalDetailRepository.findByInstCd(instCd)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        return rentalDetailList.stream()
                .map(RentalResponseDTO::of)
                .collect(Collectors.toList());
    }

    @Override
    public RentalTotalListResponseDTO getTotalRentalList() {

        // 모든 센터 정보 조회
        List<CenterResponseDTO> centerList = fetchAllCenters();

        // 각 센터의 instCd에 따른 렌탈 현황 조회
        List<RentalResponseDTO> foundationResponses = getCenterRentalList("100");
        List<RentalResponseDTO> gwanghwamunResponses = getCenterRentalList("111");
        List<RentalResponseDTO> yeouidoResponses = getCenterRentalList("112");
        List<RentalResponseDTO> gangnamResponses = getCenterRentalList("113");
        List<RentalResponseDTO> suwonResponses = getCenterRentalList("211");
        List<RentalResponseDTO> daeguResponses = getCenterRentalList("611");
        List<RentalResponseDTO> busanResponses = getCenterRentalList("612");
        List<RentalResponseDTO> gwangjuResponses = getCenterRentalList("711");
        List<RentalResponseDTO> jejuResponses = getCenterRentalList("811");

        // 센터별 렌탈 목록을 CenterRentalListResponseDTO에 담음
        CenterRentalListResponseDTO centerRentalResponses = CenterRentalListResponseDTO.of(
                foundationResponses, gwanghwamunResponses, yeouidoResponses, gangnamResponses,
                suwonResponses, daeguResponses, busanResponses, gwangjuResponses, jejuResponses
        );

        // CenterRentalListResponseDTO 리스트 생성
        List<CenterRentalListResponseDTO> centerRentalResponsesList = List.of(centerRentalResponses);

        // 센터 정보와 센터별 렌탈 현황을 포함한 객체 반환
        return RentalTotalListResponseDTO.of(centerList, centerRentalResponsesList);
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
