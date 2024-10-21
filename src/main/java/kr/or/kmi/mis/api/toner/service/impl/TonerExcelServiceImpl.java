//package kr.or.kmi.mis.api.toner.service.impl;
//
//import kr.or.kmi.mis.api.toner.model.entity.TonerInfo;
//import kr.or.kmi.mis.api.toner.model.request.TonerExcelRequestDTO;
//import kr.or.kmi.mis.api.toner.repository.TonerInfoRepository;
//import kr.or.kmi.mis.api.toner.service.TonerExcelService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Objects;
//
//@Service
//@Transactional(readOnly = true)
//@RequiredArgsConstructor
//public class TonerExcelServiceImpl implements TonerExcelService {
//
//    private final TonerInfoRepository tonerInfoRepository;
//
//    @Override
//    @Transactional
//    public void saveTonerDetails(TonerExcelRequestDTO tonerExcelRequestDTO) {
//
//        List<TonerInfo> entities = tonerExcelRequestDTO.getDetails().stream()
//                .map(dto -> tonerInfoRepository.findByModelNmAndTonerNm(dto.getModelNm(), dto.getTonerNm())
//                        .map(tonerInfo -> TonerInfo.builder()
//                                .price(dto.getPrice())
//                                .build())
//                        .orElse(null))
//                .filter(Objects::nonNull)
//                .toList();
//
//        tonerInfoRepository.saveAll(entities);
//    }
//}
