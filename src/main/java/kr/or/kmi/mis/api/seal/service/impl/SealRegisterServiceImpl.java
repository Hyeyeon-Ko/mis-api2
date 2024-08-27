package kr.or.kmi.mis.api.seal.service.impl;

import kr.or.kmi.mis.api.seal.model.request.SealRegisterRequestDTO;
import kr.or.kmi.mis.api.seal.model.request.SealUpdateRequestDTO;
import kr.or.kmi.mis.api.seal.service.SealRegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SealRegisterServiceImpl implements SealRegisterService {

    @Override
    public void registerSeal(SealRegisterRequestDTO sealRegisterRequestDTO) {

    }

    @Override
    public void updateSeal(Long draftId, SealUpdateRequestDTO sealUpdateRequestDTO) {

    }

    @Override
    public void deleteSeal(Long draftId) {

    }
}
