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

    @Override
    public List<DocstorageResponseDTO> getDocstorageDeptList(String deptCd) {
        List<DocStorageDetail> docStorageDetailList = fetchDocStorageDetailsByDeptCd(deptCd);
        return convertToResponseDTOList(docStorageDetailList);
    }

    @Override
    public List<DocstorageResponseDTO> getDocstoragePendingList(String instCd) {

        List<DocStorageMaster> docStorageMasterList = fetchDocStorageMastersByInstCdAndA(instCd);

        return docStorageMasterList.stream()
                .flatMap(master -> {
                    List<DocStorageDetail> details = fetchDocStorageDetailsByDraftId(master.getDraftId());

                    return details.stream()
                            .map(detail -> DocstorageResponseDTO.builder()
                                    .draftId(master.getDraftId())
                                    .teamNm(detail.getTeamNm())
                                    .docId(detail.getDocId())
                                    .location(detail.getLocation())
                                    .docNm(detail.getDocNm())
                                    .manager(detail.getManager())
                                    .subManager(detail.getSubManager())
                                    .storageYear(detail.getStorageYear())
                                    .createDate(detail.getCreateDate())
                                    .transferDate(detail.getTransferDate())
                                    .tsdNum(detail.getTsdNum())
                                    .disposalDate(detail.getDisposalDate())
                                    .dpdraftNum(detail.getDpdNum())
                                    .build());
                })
                .toList();
    }

    @Override
    public DocstorageCenterListResponseDTO getDocstorageCenterList(String instCd) {
        List<DeptResponseDTO> deptList = fetchDeptListForCenter(instCd);
        List<DocStorageMaster> docStorageMasterList = fetchDocStorageMastersByInstCdAndE(instCd);

        Map<String, List<DocStorageMaster>> groupedByDept = groupByDeptCd(docStorageMasterList);
        List<DeptDocstorageListResponseDTO> deptDocstorageListResponses = groupedByDept.entrySet().stream()
                .map(entry -> createDeptDocstorageResponse(entry.getKey(), entry.getValue()))
                .toList();

        return DocstorageCenterListResponseDTO.of(deptList, deptDocstorageListResponses);
    }

    @Override
    public DocstorageTotalListResponseDTO getTotalDocstorageList() {
        List<CenterResponseDTO> centerList = fetchAllCenters();
        Map<String, List<DocstorageResponseDTO>> responseMap = initializeResponseMap();

        centerList.forEach(center -> {
            List<DocStorageMaster> docStorageMasterList = fetchDocStorageMastersByInstCdAndE(center.getDetailCd());
            docStorageMasterList.forEach(master -> addToResponseMap(responseMap, center.getDetailCd(), master));
        });

        CenterDocstorageListResponseDTO centerDocstorageListResponseDTO = buildCenterDocstorageResponseDTO(responseMap);
        return DocstorageTotalListResponseDTO.of(centerList, List.of(centerDocstorageListResponseDTO));
    }

    private List<DocStorageDetail> fetchDocStorageDetailsByDeptCd(String deptCd) {
        return docStorageDetailRepository.findAllByDeptCd(deptCd)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
    }

    private List<DeptResponseDTO> fetchDeptListForCenter(String instCd) {
        StdGroup stdGroup = fetchStdGroup("A002");
        List<StdDetail> stdDetailList = stdDetailRepository.findByGroupCdAndEtcItem1(stdGroup, instCd)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        return stdDetailList.stream()
                .map(stdDetail -> DeptResponseDTO.builder()
                        .detailCd(stdDetail.getDetailCd())
                        .detailNm(stdDetail.getDetailNm())
                        .build())
                .toList();
    }

    private StdGroup fetchStdGroup(String groupCd) {
        return stdGroupRepository.findByGroupCd(groupCd)
                .orElseThrow(() -> new IllegalArgumentException("Standard Group not found for code: " + groupCd));
    }

    private List<DocStorageMaster> fetchDocStorageMastersByInstCdAndA(String instCd) {
        return docStorageMasterRepository.findAllByInstCdAndStatus(instCd, "A")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
    }

    private List<DocStorageMaster> fetchDocStorageMastersByInstCdAndE(String instCd) {
        return docStorageMasterRepository.findAllByInstCdAndStatus(instCd, "E")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
    }

    private Map<String, List<DocStorageMaster>> groupByDeptCd(List<DocStorageMaster> docStorageMasters) {
        return docStorageMasters.stream().collect(Collectors.groupingBy(DocStorageMaster::getDeptCd));
    }

    private DeptDocstorageListResponseDTO createDeptDocstorageResponse(String deptCd, List<DocStorageMaster> masters) {
        List<DocstorageResponseDTO> docstorageResponseDTOList = masters.stream()
                .flatMap(master -> getDocstorageResponsesForMaster(master).stream())
                .toList();
        return DeptDocstorageListResponseDTO.builder()
                .deptCd(deptCd)
                .docstorageResponseDTOList(docstorageResponseDTOList)
                .build();
    }

    private List<CenterResponseDTO> fetchAllCenters() {
        StdGroup stdGroup = fetchStdGroup("A001");
        List<StdDetail> stdDetailList = stdDetailRepository.findAllByUseAtAndGroupCd("Y", stdGroup)
                .orElseThrow(() -> new IllegalArgumentException("Standard Detail not found"));
        return stdDetailList.stream()
                .map(stdDetail -> CenterResponseDTO.builder()
                        .detailNm(stdDetail.getDetailNm())
                        .detailCd(stdDetail.getDetailCd())
                        .build())
                .toList();
    }

    private Map<String, List<DocstorageResponseDTO>> initializeResponseMap() {
        Map<String, List<DocstorageResponseDTO>> responseMap = new HashMap<>();
        for (int i = 100; i <= 108; i++) {
            responseMap.put(String.valueOf(i), new ArrayList<>());
        }
        return responseMap;
    }

    private void addToResponseMap(Map<String, List<DocstorageResponseDTO>> responseMap, String detailCd, DocStorageMaster master) {
        List<DocstorageResponseDTO> docstorageResponses = getDocstorageResponsesForMaster(master);
        responseMap.getOrDefault(detailCd, new ArrayList<>()).addAll(docstorageResponses);
    }

    private CenterDocstorageListResponseDTO buildCenterDocstorageResponseDTO(Map<String, List<DocstorageResponseDTO>> responseMap) {
        return CenterDocstorageListResponseDTO.of(
                responseMap.get("100"), responseMap.get("101"), responseMap.get("102"), responseMap.get("103"),
                responseMap.get("104"), responseMap.get("105"), responseMap.get("106"), responseMap.get("107"), responseMap.get("108")
        );
    }

    private List<DocstorageResponseDTO> convertToResponseDTOList(List<DocStorageDetail> docStorageDetailList) {
        return docStorageDetailList.stream()
                .map(docStorageDetail -> {
                    DocStorageMaster docStorageMaster = null;

                    if (docStorageDetail.getDraftId() != null) {
                        docStorageMaster = docStorageMasterRepository.findById(docStorageDetail.getDraftId())
                                .orElseThrow(() -> new IllegalArgumentException("Master record not found for draftId: " + docStorageDetail.getDraftId()));
                    }

                    return DocstorageResponseDTO.builder()
                            .draftId(docStorageDetail.getDraftId())
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
                            .type(docStorageMaster != null ? docStorageMaster.getType() : null)
                            .status(docStorageMaster != null ? docStorageMaster.getStatus() : null)
                            .build();
                })
                .toList();
    }

    private List<DocstorageResponseDTO> getDocstorageResponsesForMaster(DocStorageMaster docStorageMaster) {
        List<DocStorageDetail> docStorageDetails = fetchDocStorageDetailsByDraftId(docStorageMaster.getDraftId());
        return convertToResponseDTOList(docStorageDetails);
    }

    private List<DocStorageDetail> fetchDocStorageDetailsByDraftId(Long draftId) {
        return docStorageDetailRepository.findAllByDraftId(draftId)
                .orElseThrow(() -> new IllegalArgumentException("DocStorageDetails not found for draftId: " + draftId));
    }
}
