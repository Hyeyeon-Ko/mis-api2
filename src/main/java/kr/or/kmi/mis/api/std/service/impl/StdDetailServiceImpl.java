package kr.or.kmi.mis.api.std.service.impl;

import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import kr.or.kmi.mis.api.std.model.entity.StdDetailHist;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import kr.or.kmi.mis.api.std.model.request.StdDetailRequestDTO;
import kr.or.kmi.mis.api.std.repository.StdDetailRepository;
import kr.or.kmi.mis.api.std.repository.StdGroupRepository;
import kr.or.kmi.mis.api.std.service.StdDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StdDetailServiceImpl implements StdDetailService {

    private final StdDetailRepository stdDetailRepository;
    private final StdGroupRepository stdGroupRepository;

/*    @Override
    @Transactional(readOnly = true)
    public List<StdDetailResponseDTO> getInfo(String clsCd, String etcCd) {

        List<StdDetail> bscdLists = bscdRepository.findAllByClsCdAndEtcCd(clsCd, etcCd);

        return bscdLists.stream()
                .map(BscdResponseDTO::of)
                .collect(Collectors.toList());
    }*/


    @Override
    @Transactional
    // - 되면, DTO에서 get해서 저장
    // - 안되면, 로그인 세션 ID로 그룹웨어에서 정보 호출해, toEntity param으로 같이 넘겨줘야 함
    public void addInfo(StdDetailRequestDTO stdDetailRequestDTO) {

        // todo: 로그인 세션 ID로 수정자 이름 찾기
        String fstRegisterId = "수정자";

        StdGroup stdGroup = stdGroupRepository.findById(stdDetailRequestDTO.getGroupCd())
                .orElseThrow(() -> new IllegalArgumentException("ggg"));

        StdDetail stdDetail = stdDetailRequestDTO.toEntity(fstRegisterId, stdGroup);
        stdDetailRepository.save(stdDetail);
    }

    /*@Override
    @Transactional
    public void updateInfo(StdDetailRequestDTO stdDetailRequestDTO) {
        StdDetail oriStdDetail = stdDetailRepository.findById(stdDetailRequestDTO.getDetailCd())
                .orElseThrow(() -> new IllegalArgumentException("ggg"));

        // todo: 로그인 세션 ID로 수정자 이름 찾기
        String "lastUpd정자";

        if (oriStdDetail != null) {
            StdDetailHist stdDetailHist =

            bscdRepository.save(oriInfo);
        } else {
            throw new EntityNotFoundException("Entity with the given criteria not found");
        }

        // 시작, 종료일자가 null 값으로 들어올 때 default 설정
        //   - Dates[0] : 시작일자
        //   - Dates[1] : 종료일자
        String[] Dates = setDefaultDates(bscdRequestDTO);

        // todo: 로그인 세션 ID로 수정자 이름 찾기
        String lastUpdater = "수정자";

        if (oriInfo != null) {
            oriInfo.update(bscdRequestDTO.getEtcDetlCd(),   // todo: unique 값 예외처리 필요
                    Dates[0], Dates[1],
                    lastUpdater, new Timestamp(System.currentTimeMillis()),
                    bscdRequestDTO.getEtcItem1(),
                    bscdRequestDTO.getEtcItem2(),
                    bscdRequestDTO.getEtcItem3(),
                    bscdRequestDTO.getEtcItem4(),
                    bscdRequestDTO.getEtcItem5());

            bscdRepository.save(oriInfo);
        } else {
            throw new EntityNotFoundException("Entity with the given criteria not found");
        }
    }*/
/*
    @Override
    public void deleteInfo(String clsCd, String etcCd, String etcDetlCd) {
        Bscd oriInfo = bscdRepository.findByClsCdAndEtcCdAndEtcDetlCd(clsCd, etcCd, etcDetlCd);

        if (oriInfo != null) {
            bscdRepository.delete(oriInfo);
        } else {
            throw new EntityNotFoundException("Entity with the given criteria not found");
        }
    }
*/
/*    public String[] setDefaultDates(StdDetailRequestDTO stdDetailRequestDTO) {

        String fromDd = stdDetailRequestDTO.getFromDd();
        String toDd = stdDetailRequestDTO.getToDd();

        if(fromDd == null) {
            fromDd = String.valueOf((LocalDate.now()));
        }
        if(toDd == null) {
            toDd = "9999-12-31";
        }

        return new String[]{fromDd, toDd};
    }*/
}
