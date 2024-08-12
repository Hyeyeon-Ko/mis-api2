package kr.or.kmi.mis.api.docstorage.service.impl;

import kr.or.kmi.mis.api.docstorage.domain.entity.DocStorageDetail;
import kr.or.kmi.mis.api.docstorage.domain.entity.DocStorageMaster;
import kr.or.kmi.mis.api.docstorage.domain.response.*;
import kr.or.kmi.mis.api.docstorage.repository.DocStorageDetailRepository;
import kr.or.kmi.mis.api.docstorage.repository.DocStorageMasterRepository;
import kr.or.kmi.mis.api.docstorage.service.DocstorageListService;
import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import kr.or.kmi.mis.api.std.repository.StdDetailRepository;
import kr.or.kmi.mis.api.std.repository.StdGroupRepository;
import kr.or.kmi.mis.api.user.model.response.InfoDetailResponseDTO;
import kr.or.kmi.mis.api.user.service.InfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocstorageListServiceImpl implements DocstorageListService {

    private final StdDetailRepository stdDetailRepository;
    private final DocStorageDetailRepository docStorageDetailRepository;
    private final DocStorageMasterRepository docStorageMasterRepository;
    private final StdGroupRepository stdGroupRepository;
    private final InfoService infoService;

    @Override
    public List<DocstorageResponseDTO> getDocstorageDeptList(String userId) {

//        배포시, 주석 제거
//        InfoDetailResponseDTO infoDetailResponseDTO = infoService.getUserInfoDetail(userId);
//        String teamCd = infoDetailResponseDTO.getTeamCd();

        String teamCd = "FDT30";

        // 1. 팀 코드 -> 부서 검색
        StdGroup stdGroup = stdGroupRepository.findByGroupCd("A003")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        StdDetail stdDetail = stdDetailRepository.findByGroupCdAndEtcItem3(stdGroup, teamCd)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        String deptCd = stdDetail.getEtcItem1();

        // 2. 부서 문서보관 내역 검색
        List<DocStorageMaster> docStorageMasterList = docStorageMasterRepository.findAllByDeptCd(deptCd)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        List<DocstorageResponseDTO> docstorageResponseDTOList = new ArrayList<>();

        docStorageMasterList.forEach(docStorageMaster -> docstorageResponseDTOList.addAll(getDocstorageResponsesForMaster(docStorageMaster)));

        return docstorageResponseDTOList;
    }

    @Override
    public DocstorageCenterListResponseDTO getDocstorageCenterList(String instCd) {

        // 1. 부서 목록
        StdGroup stdGroup = stdGroupRepository.findByGroupCd("A002")
                .orElseThrow(() -> new IllegalArgumentException("Standard Group not found for code: A002"));

        List<StdDetail> stdDetailList = stdDetailRepository.findByGroupCdAndEtcItem1(stdGroup, instCd)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        List<DeptResponseDTO> deptList = stdDetailList.stream()
                .map(stdDetail -> DeptResponseDTO.builder()
                        .detailCd(stdDetail.getDetailCd())
                        .detailNm(stdDetail.getDetailNm())
                        .build())
                .toList();

        // 2. 부서 목록에 따른 문서보관 내역
        List<DocStorageMaster> docStorageMasterList = docStorageMasterRepository.findAllByInstCd(instCd)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        Map<String, List<DocStorageMaster>> groupedByDept = docStorageMasterList.stream()
                .collect(Collectors.groupingBy(DocStorageMaster::getDeptCd));

        List<DeptDocstorageListResponseDTO> deptDocstorageListResponses = groupedByDept.entrySet().stream()
                .map(entry -> {
                    String deptCd = entry.getKey();
                    List<DocstorageResponseDTO> docstorageResponseDTOList = entry.getValue().stream()
                            .flatMap(docStorageMaster -> getDocstorageResponsesForMaster(docStorageMaster).stream())
                            .toList();

                    return DeptDocstorageListResponseDTO.builder()
                            .deptCd(deptCd)
                            .docstorageResponseDTOList(docstorageResponseDTOList)
                            .build();
                })
                .toList();

        return DocstorageCenterListResponseDTO.of(deptList, deptDocstorageListResponses);
    }


    @Override
    public DocstorageTotalListResponseDTO getTotalDocstorageList() {

        // 1. 센터 목록
        StdGroup stdGroup = stdGroupRepository.findByGroupCd("A001")
                .orElseThrow(() -> new IllegalArgumentException("Standard Group not found for code: A001"));
        List<StdDetail> stdDetailList = stdDetailRepository.findAllByUseAtAndGroupCd("Y", stdGroup)
                .orElseThrow(() -> new IllegalArgumentException("Standard Detail not found"));

        List<CenterResponseDTO> centerList = stdDetailList.stream()
                .map(stdDetail -> CenterResponseDTO.builder()
                        .detailNm(stdDetail.getDetailNm())
                        .detailCd(stdDetail.getDetailCd())
                        .build())
                .toList();

        // 2. 센터 목록에 따른 문서보관 내역
        Map<String, List<DocstorageResponseDTO>> responseMap = new HashMap<>();
        responseMap.put("100", new ArrayList<>());
        responseMap.put("101", new ArrayList<>());
        responseMap.put("102", new ArrayList<>());
        responseMap.put("103", new ArrayList<>());
        responseMap.put("104", new ArrayList<>());
        responseMap.put("105", new ArrayList<>());
        responseMap.put("106", new ArrayList<>());
        responseMap.put("107", new ArrayList<>());
        responseMap.put("108", new ArrayList<>());

        stdDetailList.forEach(stdDetail -> {
            List<DocStorageMaster> docStorageMasterList = docStorageMasterRepository.findAllByInstCd(stdDetail.getDetailCd())
                    .orElseThrow(() -> new IllegalArgumentException("Not Found"));

            docStorageMasterList.forEach(docStorageMaster -> {
                List<DocstorageResponseDTO> docstorageResponses = getDocstorageResponsesForMaster(docStorageMaster);
                responseMap.getOrDefault(stdDetail.getDetailCd(), new ArrayList<>()).addAll(docstorageResponses);
            });
        });

        CenterDocstorageListResponseDTO centerDocstorageListResponseDTO = CenterDocstorageListResponseDTO.of(
                responseMap.get("100"), responseMap.get("101"), responseMap.get("102"), responseMap.get("103"),
                responseMap.get("104"), responseMap.get("105"), responseMap.get("106"), responseMap.get("107"), responseMap.get("108")
        );

        return DocstorageTotalListResponseDTO.of(centerList, List.of(centerDocstorageListResponseDTO));
    }

    private List<DocstorageResponseDTO> getDocstorageResponsesForMaster(DocStorageMaster docStorageMaster) {
        List<DocStorageDetail> docStorageDetails = docStorageDetailRepository.findAllByDraftId(docStorageMaster.getDraftId())
                .orElseThrow(() -> new IllegalArgumentException("DocStorageDetails not found for draftId: " + docStorageMaster.getDraftId()));

        return docStorageDetails.stream()
                .map(docStorageDetail -> DocstorageResponseDTO.builder()
                        .teamNm(docStorageDetail.getTeamNm())
                        .docId(docStorageDetail.getDocId())
                        .location(docStorageDetail.getLocation())
                        .docNm(docStorageDetail.getDocNm())
                        .manager(docStorageDetail.getManager())
                        .subManager(docStorageDetail.getSubManager())
                        .storageYear(docStorageDetail.getStorageYear())
                        .createDate(docStorageDetail.getCreateDate())
                        .transferDate(docStorageDetail.getTransferDate())
                        .tsdNum(docStorageDetail.getTsdNum())
                        .disposalDate(docStorageDetail.getDisposalDate())
                        .dpdraftNum(docStorageDetail.getDpdNum())
                        .build())
                .toList();
    }

}
