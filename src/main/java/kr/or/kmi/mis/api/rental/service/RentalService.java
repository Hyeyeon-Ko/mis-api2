package kr.or.kmi.mis.api.rental.service;

import kr.or.kmi.mis.api.rental.model.request.RentalBulkUpdateRequestDTO;
import kr.or.kmi.mis.api.rental.model.request.RentalRequestDTO;
import kr.or.kmi.mis.api.rental.model.response.RentalResponseDTO;

import java.util.List;

public interface RentalService {
    void addRentalInfo(RentalRequestDTO rentalRequestDTO);
    RentalResponseDTO getRentalInfo(Long detailId);
    void updateRentalInfo(Long detailId, RentalRequestDTO rentalRequestDTO);
    void bulkUpdateRentalInfo(RentalBulkUpdateRequestDTO rentalBulkUpdateRequestDTO);
    void deleteRentalInfo(Long detailId);
    void finishRentalInfo(List<Long> detailIds);
}
