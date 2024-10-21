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
    public TonerInfoResponseDTO getTonerInfo(String mngNum) {

        TonerInfo tonerInfo = tonerInfoRepository.findByMngNum(mngNum)
                .orElseThrow(() -> new EntityNotFoundException("Toner Info Detail Not Found: " + mngNum));

        Optional<TonerPrice> optionalTonerPrice = tonerPriceRepository.findByTonerNm(tonerInfo.getTonerNm());

        TonerPrice tonerPrice = optionalTonerPrice.orElse(null);

        return TonerInfoResponseDTO.of(tonerInfo, tonerPrice);
    }

    @Override
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

    @Override
    public void updateTonerInfo(String mngNum, TonerInfoRequestDTO tonerInfoRequestDTO, String userId) {

        // 1. tonerInfo 조회
        TonerInfo tonerInfo = tonerInfoRepository.findByMngNum(tonerInfoRequestDTO.getMngNum())
                .orElseThrow(() -> new EntityNotFoundException("Toner Info Detail Not Found: " + mngNum));

        // 2. mngNum 중복 예외처리
        if (!mngNum.equals(tonerInfoRequestDTO.getMngNum())) {
            boolean exists = tonerPriceRepository.existsByTonerNm(tonerInfoRequestDTO.getMngNum());
            if (exists) {
                throw new EntityExistsException("Toner with the same name already exists: " + tonerInfoRequestDTO.getMngNum());
            }
        }

        // 3. tonerInfo 업데이트
        tonerInfo.tonerInfoUpdate(tonerInfoRequestDTO);
        tonerInfo.setUpdtDt(LocalDateTime.now());
        tonerInfo.setUpdtrId(userId);

        // 4. 저장
        tonerInfoRepository.save(tonerInfo);
    }

    @Override
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
