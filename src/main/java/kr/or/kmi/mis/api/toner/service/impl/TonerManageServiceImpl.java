package kr.or.kmi.mis.api.toner.service.impl;

import jakarta.persistence.EntityExistsException;
import kr.or.kmi.mis.api.docstorage.domain.response.CenterResponseDTO;
import kr.or.kmi.mis.api.exception.EntityNotFoundException;
import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import kr.or.kmi.mis.api.std.repository.StdDetailRepository;
import kr.or.kmi.mis.api.std.repository.StdGroupRepository;
import kr.or.kmi.mis.api.toner.model.entity.TonerInfo;
import kr.or.kmi.mis.api.toner.model.entity.TonerPrice;
import kr.or.kmi.mis.api.toner.model.request.TonerInfoRequestDTO;
import kr.or.kmi.mis.api.toner.model.response.CenterTonerListResponseDTO;
import kr.or.kmi.mis.api.toner.model.response.TonerExcelResponseDTO;
import kr.or.kmi.mis.api.toner.model.response.TonerInfoResponseDTO;
import kr.or.kmi.mis.api.toner.model.response.TonerTotalListResponseDTO;
import kr.or.kmi.mis.api.toner.repository.TonerInfoRepository;
import kr.or.kmi.mis.api.toner.repository.TonerPriceRepository;
import kr.or.kmi.mis.api.toner.service.TonerManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    /**
     * 토너 관리표의 항목들을 TonerExcelResponseDTO 리스트로 반환.
     * @param instCd
     * @return TonerExcelResponseDTO의 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<TonerExcelResponseDTO> getTonerList(String instCd) {

        // 1. tonerInfoList 조회
        List<TonerInfo> tonerInfoList = tonerInfoRepository.findAllByInstCd(instCd);

        // 2. 모든 TonerPrice 조회
        List<TonerPrice> tonerPriceList = tonerPriceRepository.findAll();

        // TonerPrice 목록을 Map으로 변환
        Map<String, TonerPrice> tonerPriceMap = tonerPriceList.stream()
                .collect(Collectors.toMap(TonerPrice::getTonerNm, Function.identity()));

        // 3. TonerPrice가 없는 경우 null 처리
        return tonerInfoList.stream()
                .map(tonerInfo -> {
                    String[] tonerNmParts = tonerInfo.getTonerNm().split(" / ");

                    // 각 토너 이름에 맞는 가격을 찾고, 없으면 null로 처리
                    List<String> prices = Arrays.stream(tonerNmParts)
                            .map(String::trim)
                            .map(tonerPriceMap::get)
                            .map(tonerPrice -> tonerPrice != null ? tonerPrice.getPrice() : null)
                            .collect(Collectors.toList());

                    // 가격이 전부 동일한지 확인
                    boolean allPricesSame = prices.stream().distinct().count() == 1;

                    // 가격이 동일하다면 하나의 가격만 사용, 아니면 콤마로 연결된 가격 반환
                    String finalPrice = allPricesSame ? prices.getFirst() : String.join(" / ", prices);

                    return TonerExcelResponseDTO.of(tonerInfo, finalPrice);
                })
                .collect(Collectors.toList());
    }

    /**
     * 각 센터의 토너 관리표 항목들을 TonerExcelResponseDTO 리스트로 반환.
     * @param instCd
     * @return TonerExcelResponseDTO의 리스트
     */
    public List<TonerExcelResponseDTO> getCenterTonerList(String instCd) {

        // 1. TonerInfo 리스트 조회
        List<TonerInfo> tonerInfoList = tonerInfoRepository.findAllByInstCd(instCd);

        // 2. 모든 TonerPrice 조회
        List<TonerPrice> tonerPriceList = tonerPriceRepository.findAll();

        // TonerPrice 목록을 Map으로 변환
        Map<String, TonerPrice> tonerPriceMap = tonerPriceList.stream()
                .collect(Collectors.toMap(TonerPrice::getTonerNm, Function.identity()));

        // 3. TonerPrice가 없는 경우 null 처리
        return tonerInfoList.stream()
                .map(tonerInfo -> {
                    String[] tonerNmParts = tonerInfo.getTonerNm().split(" / ");

                    List<String> prices = Arrays.stream(tonerNmParts)
                            .map(String::trim)
                            .map(tonerPriceMap::get)
                            .map(tonerPrice -> tonerPrice != null ? tonerPrice.getPrice() : null)
                            .collect(Collectors.toList());

                    boolean allPricesSame = prices.stream().distinct().count() == 1;

                    String finalPrice = allPricesSame ? prices.getFirst() : String.join(" / ", prices);

                    return TonerExcelResponseDTO.of(tonerInfo, finalPrice);
                })
                .collect(Collectors.toList());
    }

    /**
     * 전국의 토너 관리표 항목들을 하나의 TonerTotalListResponseDTO로 반환.
     * @return TonerTotalListResponseDTO
     */
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

    /**
     * 상세 정보 조회 또는 수정을 위해 단일 항목 반환.
     * @param mngNum 조회할 토너의 관리번호
     * @return TonerInfoResponseDTO
     */
    @Override
    @Transactional(readOnly = true)
    public TonerInfoResponseDTO getTonerInfo(String mngNum) {

        TonerInfo tonerInfo = tonerInfoRepository.findByMngNum(mngNum)
                .orElseThrow(() -> new EntityNotFoundException("Toner Info Detail Not Found: " + mngNum));

        Optional<TonerPrice> optionalTonerPrice = tonerPriceRepository.findByTonerNm(tonerInfo.getTonerNm());

        TonerPrice tonerPrice = optionalTonerPrice.orElse(null);

        return TonerInfoResponseDTO.of(tonerInfo, tonerPrice);
    }

    /**
     * 토너 관리 항목 추가.
     * @param tonerInfoRequestDTO 토너 관리 정보
     * @param userId 추가자 사번
     * @param instCd 추가자 센터코드
     */
    @Override
    @Transactional
    public void addTonerInfo(TonerInfoRequestDTO tonerInfoRequestDTO, String userId, String instCd) {

        // 1. 관리번호 중복 예외처리
        boolean exists = tonerInfoRepository.existsByMngNum(tonerInfoRequestDTO.getMngNum());
        if (exists) {
            throw new EntityExistsException("Toner Info already exists");
        }

        // 2. 토너 정보 입력
        TonerInfo tonerInfo = tonerInfoRequestDTO.toEntity(instCd);
        tonerInfo.setRgstDt(LocalDateTime.now());
        tonerInfo.setRgstrId(userId);

        // 3. 저장
        tonerInfoRepository.save(tonerInfo);
    }

    /**
     * 토너 관리 항목 수정.
     * @param mngNum 수정할 토너의 관리번호
     * @param tonerInfoRequestDTO 토너 수정 정보
     * @param userId 수정자 사번
     */
    @Override
    @Transactional
    public void updateTonerInfo(String mngNum, TonerInfoRequestDTO tonerInfoRequestDTO, String userId) {

        // 1. tonerInfo 조회
        TonerInfo tonerInfo = tonerInfoRepository.findByMngNum(tonerInfoRequestDTO.getMngNum())
                .orElseThrow(() -> new EntityNotFoundException("Toner Info Detail Not Found: " + mngNum));

        // 2. mngNum 중복 예외처리
        if (!mngNum.equals(tonerInfoRequestDTO.getMngNum())) {
            boolean exists = tonerInfoRepository.existsByMngNum(tonerInfoRequestDTO.getMngNum());
            if (exists) {
                throw new EntityExistsException("Toner with the same management number already exists: " + tonerInfoRequestDTO.getMngNum());
            }
        }

        // 3. tonerInfo 업데이트
        tonerInfo.tonerInfoUpdate(tonerInfoRequestDTO);
        tonerInfo.setUpdtDt(LocalDateTime.now());
        tonerInfo.setUpdtrId(userId);

        // 4. 저장
        tonerInfoRepository.save(tonerInfo);
    }

    /**
     * 토너 관리 항목 삭제.
     * @param mngNum 삭제할 토너의 관리번호
     */
    @Override
    @Transactional
    public void deleteTonerInfo(String mngNum) {

        // 1. tonerInfo 조회
        TonerInfo tonerInfo = tonerInfoRepository.findByMngNum(mngNum)
                .orElseThrow(() -> new EntityNotFoundException("Toner Info Detail Not Found: " + mngNum));

        // 2. 삭제
        tonerInfoRepository.delete(tonerInfo);
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
