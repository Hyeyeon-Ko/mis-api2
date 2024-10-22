package kr.or.kmi.mis.api.toner.service.impl;

import jakarta.persistence.EntityExistsException;
import kr.or.kmi.mis.api.exception.EntityNotFoundException;
import kr.or.kmi.mis.api.toner.model.entity.TonerPrice;
import kr.or.kmi.mis.api.toner.model.request.TonerPriceRequestDTO;
import kr.or.kmi.mis.api.toner.model.response.TonerPriceResponseDTO;
import kr.or.kmi.mis.api.toner.repository.TonerPriceRepository;
import kr.or.kmi.mis.api.toner.service.TonerPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TonerPriceServiceImpl implements TonerPriceService {

    private final TonerPriceRepository tonerPriceRepository;

    /**
     * 토너 단가표의 항목들을 TonerPriceResponseDTO 리스트로 반환.
     * @return TonerPriceResponseDTO의 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<TonerPriceResponseDTO> getTonerPriceList() {

        List<TonerPrice> tonerPriceList = tonerPriceRepository.findAll();

        return tonerPriceList.stream()
                .map(TonerPriceResponseDTO::of)
                .collect(Collectors.toList());
    }

    /**
     * 상세 정보 조회 또는 수정을 위해 단일 항목 반환.
     * @param tonerNm 조회할 토너의 토너명
     * @return TonerPriceResponseDTO
     */
    @Override
    public TonerPriceResponseDTO getTonerPriceInfo(String tonerNm) {
        TonerPrice tonerPrice = tonerPriceRepository.findByTonerNm(tonerNm)
                .orElseThrow(() -> new EntityNotFoundException("Toner Price Detail Not Found: " + tonerNm));

        return TonerPriceResponseDTO.of(tonerPrice);
    }

    /**
     * 토너 단가 항목 추가.
     * @param tonerPriceRequestDTO 토너 단가 정보
     * @param userId 추가자 사번
     */
    @Override
    public void addTonerPriceInfo(TonerPriceRequestDTO tonerPriceRequestDTO, String userId) {

        // 1. 토너명 중복 예외처리
        boolean exists = tonerPriceRepository.existsByTonerNm(tonerPriceRequestDTO.getTonerNm());
        if (exists) {
            throw new EntityExistsException("Toner Price already exists");
        }

        // 2. 토너 단가 정보 입력
        TonerPrice tonerPrice = tonerPriceRequestDTO.toEntity();
        tonerPrice.setRgstDt(LocalDateTime.now());
        tonerPrice.setRgstrId(userId);

        // 3. 저장
        tonerPriceRepository.save(tonerPrice);
    }

    /**
     * 토너 단가 항목 수정.
     * @param tonerNm 수정할 토너의 토너명
     * @param tonerPriceRequestDTO 토너 수정 정보
     * @param userId 수정자 사번
     */
    @Override
    public void updateTonerPriceInfo(String tonerNm, TonerPriceRequestDTO tonerPriceRequestDTO, String userId) {

        // 1. tonerPrice 조회
        TonerPrice tonerPrice = tonerPriceRepository.findByTonerNm(tonerNm)
                .orElseThrow(() -> new EntityNotFoundException("Toner Price Detail Not Found: " + tonerNm));

        // 2. tonerNm 중복 예외처리
        if (!tonerNm.equals(tonerPriceRequestDTO.getTonerNm())) {
            boolean exists = tonerPriceRepository.existsByTonerNm(tonerPriceRequestDTO.getTonerNm());
            if (exists) {
                throw new EntityExistsException("Toner with the same name already exists: " + tonerPriceRequestDTO.getTonerNm());
            }
        }

        // 3. tonerPrice 업데이트
        tonerPrice.tonerPriceUpdate(tonerPriceRequestDTO);
        tonerPrice.setUpdtDt(LocalDateTime.now());
        tonerPrice.setUpdtrId(userId);

        // 4. 저장
        tonerPriceRepository.save(tonerPrice);
    }

    /**
     * 토너 단가 항목 삭제.
     * @param tonerNm 삭제할 토너의 토너명
     */
    @Override
    public void deleteTonerPriceInfo(String tonerNm) {

        // 1. tonerPrice 조회
        TonerPrice tonerPrice = tonerPriceRepository.findByTonerNm(tonerNm)
                .orElseThrow(() -> new EntityNotFoundException("Toner Price Detail Not Found: " + tonerNm));

        // 2. 삭제
        tonerPriceRepository.delete(tonerPrice);
    }

    // TonerNm을 기준으로 중복 제거
    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

}
