package kr.or.kmi.mis.api.main.service;

import kr.or.kmi.mis.api.bcd.model.response.BcdMasterResponseDTO;
import kr.or.kmi.mis.api.bcd.service.BcdService;
import kr.or.kmi.mis.api.main.model.response.ApplyResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ApplyServiceImpl implements ApplyService {

    private final BcdService bcdService;

    @Override
    @Transactional(readOnly = true)
    public ApplyResponseDTO getAllApplyList(String documentType, Timestamp startDate, Timestamp endDate) {

        /**
         * 다른 유형의 신청목록 추가될 수 있음
         * 확장성을 고려해, switch-case 문으로 구현
         *    ex) 문서수발신 신청 추가
         *        case "문서수발신":
         *              messageApplyLists = messageService.getMessageApplyByDateRange(startDate, endDate);
         *              break;
         * */

        List<BcdMasterResponseDTO> bcdApplyLists;

        if (documentType != null) {
            switch (documentType) {
                case "명함신청":
                    bcdApplyLists = bcdService.getBcdApplyByDateRange(startDate, endDate);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid document type: " + documentType);
            }
        } else {
            // 전체 신청 목록을 조회합니다.
            bcdApplyLists = bcdService.getBcdApplyByDateRange(startDate, endDate);
        }

        return ApplyResponseDTO.of(bcdApplyLists);
    }

    @Override
    @Transactional(readOnly = true)
    public ApplyResponseDTO getAllMyApplyList(String documentType, Timestamp startDate, Timestamp endDate) {

        /**
         * 다른 유형의 신청목록 추가될 수 있음
         * 확장성을 고려해, switch-case 문으로 구현
         *    ex) 문서수발신 신청 추가
         *        case "문서수발신":
         *              messageApplyLists = messageService.getMessageApplyByDateRange(startDate, endDate);
         *              break;
         * */

        List<BcdMasterResponseDTO> myBcdApplyLists;

        if (documentType != null) {
            switch (documentType) {
                case "명함신청":
                    myBcdApplyLists = bcdService.getMyBcdApplyByDateRange(startDate, endDate);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid document type: " + documentType);
            }
        } else {
            // 전체 신청 목록을 조회합니다.
            myBcdApplyLists = bcdService.getMyBcdApplyByDateRange(startDate, endDate);
        }

        return ApplyResponseDTO.of(myBcdApplyLists);
    }
}
