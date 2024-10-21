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

    @Override
    @Transactional(readOnly = true)
    public List<TonerPriceResponseDTO> getTonerPriceList() {

        List<TonerPrice> tonerPriceList = tonerPriceRepository.findAll();

        return tonerPriceList.stream()
                .map(TonerPriceResponseDTO::of)
                .collect(Collectors.toList());
    }

    @Override
    public TonerPriceResponseDTO getTonerPriceInfo(String tonerNm) {
        TonerPrice tonerPrice = tonerPriceRepository.findByTonerNm(tonerNm)
                .orElseThrow(() -> new EntityNotFoundException("Toner Price Detail Not Found: " + tonerNm));

        return TonerPriceResponseDTO.of(tonerPrice);
    }

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
