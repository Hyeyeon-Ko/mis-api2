package kr.or.kmi.mis.api.rental.service.impl;

import kr.or.kmi.mis.api.exception.EntityNotFoundException;
import kr.or.kmi.mis.api.rental.model.entity.RentalDetail;
import kr.or.kmi.mis.api.rental.model.request.RentalRequestDTO;
import kr.or.kmi.mis.api.rental.model.response.RentalResponseDTO;
import kr.or.kmi.mis.api.rental.repository.RentalDetailRepository;
import kr.or.kmi.mis.api.rental.service.RentalService;
import kr.or.kmi.mis.api.user.service.InfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {

    private final RentalDetailRepository rentalDetailRepository;
    private final InfoService infoService;

    @Override
    @Transactional
    public void addRentalInfo(RentalRequestDTO rentalRequestDTO) {

        // 1. 계약번호 중복 불가 예외처리
        checkDuplicateCNm(rentalRequestDTO.getContractNum(), "");

      // 2. 렌탈정보 입력 사용자 정보 조회
        String userId = infoService.getUserInfo().getUserId();
        String instCd = infoService.getUserInfoDetail(userId).getInstCd();

        // 3. 등록자 정보 update
        RentalDetail rentalDetail = rentalRequestDTO.toEntity(instCd);
        rentalDetail.setRgstrId(userId);
        rentalDetail.setRgstDt(new Timestamp(System.currentTimeMillis()));

        // 4. 저장
        rentalDetailRepository.save(rentalDetail);
    }

    @Override
    @Transactional(readOnly = true)
    public RentalResponseDTO getRentalInfo(Long detailId) {
        RentalDetail rentalDetail = rentalDetailRepository.findById(detailId)
                .orElseThrow(() -> new EntityNotFoundException("Rental Detail Not Found: " + detailId));

        return RentalResponseDTO.of(rentalDetail);
    }

    @Override
    @Transactional
    public void updateRentalInfo(Long detailId, RentalRequestDTO rentalUpdateRequestDTO) {

        // 1. rentalDetail 조회
        RentalDetail rentalDetail = rentalDetailRepository.findById(detailId)
                .orElseThrow(() -> new EntityNotFoundException("Rental Detail Not Found: " + detailId));

        // 2. 계약번호 중복 불가 예외처리
        checkDuplicateCNm(rentalUpdateRequestDTO.getContractNum(), rentalDetail.getContractNum());

        // 3. rentalDetail 업데이트
        rentalDetail.update(rentalUpdateRequestDTO);
        rentalDetail.setUpdtrId(infoService.getUserInfo().getUserId());
        rentalDetail.setUpdtDt(new Timestamp(System.currentTimeMillis()));

        if(Objects.equals(rentalDetail.getStatus(), "E")){
            rentalDetail.updateStatus("A");
        }

        rentalDetailRepository.save(rentalDetail);
    }

    void checkDuplicateCNm(String NewContractNum, String oriContractNum) {
        boolean exists = rentalDetailRepository.existsByContractNum(NewContractNum);
        if (exists) {
            if(!Objects.equals(NewContractNum, oriContractNum)) {
                throw new IllegalArgumentException("계약번호가 중복됩니다: " + NewContractNum);
            }
        }
    }

    @Override
    @Transactional
    public void deleteRentalInfo(Long detailId) {
        RentalDetail rentalDetail = rentalDetailRepository.findById(detailId)
                        .orElseThrow(() -> new EntityNotFoundException("Rental Detail Not Found: " + detailId));

        rentalDetailRepository.delete(rentalDetail);
    }

    @Override
    @Transactional
    public void finishRentalInfo(List<Long> detailIds) {
        detailIds.forEach(detailId -> {
            rentalDetailRepository.findById(detailId).ifPresent(rentalDetail -> {
                rentalDetail.updateStatus("E");
                rentalDetailRepository.save(rentalDetail);
            });
        });
    }
}
