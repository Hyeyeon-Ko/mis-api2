package kr.or.kmi.mis.api.order.service;

import kr.or.kmi.mis.api.bcd.model.entity.BcdMaster;
import kr.or.kmi.mis.api.bcd.repository.BcdDetailRepository;
import kr.or.kmi.mis.api.bcd.repository.BcdMasterRepository;
import kr.or.kmi.mis.api.exception.EntityNotFoundException;
import kr.or.kmi.mis.api.order.model.response.OrderListResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final BcdMasterRepository bcdMasterRepository;
    private final BcdDetailRepository bcdDetailRepository;

    /* 승인 완료된 목록 불러오기 */
    @Transactional(readOnly = true)
    public List<OrderListResponseDTO> getOrderList() {
        // 1. 승인상태인 신청건 리스트 불러오기
        List<BcdMaster> bcdMasterList = bcdMasterRepository.findAllByStatus("B");

        // 2. 각 신청건의 상세 정보 불러오기 using Streams
        return bcdMasterList.stream()
                .map(bcdMaster -> {
                    Integer quantity = bcdDetailRepository.findQuantityByDraftId(bcdMaster.getDraftId())
                            .orElseThrow(() -> new EntityNotFoundException("Quantity not found for draft ID: " + bcdMaster.getDraftId()));
                    return OrderListResponseDTO.builder()
                            .title(bcdMaster.getTitle())
                            .draftDate(bcdMaster.getDraftDate())
                            .respondDate(bcdMaster.getRespondDate())
                            .drafter(bcdMaster.getDrafter())
                            .quantity(quantity)
                            .build();
                })
                .collect(Collectors.toList());
    }

    /* 발주 요청 -> 발주 요청 과정 받은 후 구현 */
}

