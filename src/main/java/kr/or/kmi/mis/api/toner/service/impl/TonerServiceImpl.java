package kr.or.kmi.mis.api.toner.service.impl;

import kr.or.kmi.mis.api.exception.EntityNotFoundException;
import kr.or.kmi.mis.api.toner.model.entity.TonerInfo;
import kr.or.kmi.mis.api.toner.model.entity.TonerPrice;
import kr.or.kmi.mis.api.toner.model.request.TonerRequestDTO;
import kr.or.kmi.mis.api.toner.model.response.TonerApplyResponseDTO;
import kr.or.kmi.mis.api.toner.model.response.TonerInfo2ResponseDTO;
import kr.or.kmi.mis.api.toner.repository.TonerInfoRepository;
import kr.or.kmi.mis.api.toner.repository.TonerPriceRepository;
import kr.or.kmi.mis.api.toner.service.TonerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TonerServiceImpl implements TonerService {

    private final TonerInfoRepository tonerInfoRepository;
    private final TonerPriceRepository tonerPriceRepository;

    @Override
    @Transactional(readOnly = true)
    public TonerInfo2ResponseDTO getTonerInfo(String mngNum) {
        TonerInfo tonerInfo = tonerInfoRepository.findById(mngNum)
                .orElseThrow(() -> new EntityNotFoundException("Not found: " + mngNum));
        TonerPrice tonerPrice = tonerPriceRepository.findByTonerNm(tonerInfo.getTonerNm())
                .orElseThrow(() -> new EntityNotFoundException("Not found: " + tonerInfo.getTonerNm()));

        return TonerInfo2ResponseDTO.of(tonerInfo, tonerPrice.getPrice());
    }

    @Override
    @Transactional(readOnly = true)
    public TonerApplyResponseDTO getTonerApply(Long draftId) {
        return null;
    }

    @Override
    @Transactional
    public void applytoner(TonerRequestDTO tonerRequestDTO) {

    }

    @Override
    @Transactional
    public void updateTonerApply(TonerRequestDTO tonerRequestDTO) {

    }

    @Override
    @Transactional
    public void cancelTonerApply(Long draftId) {

    }
}
