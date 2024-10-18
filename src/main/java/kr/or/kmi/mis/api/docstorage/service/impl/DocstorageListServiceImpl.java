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

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocstorageListServiceImpl implements DocstorageListService {

    private final StdDetailRepository stdDetailRepository;
    private final DocStorageDetailRepository docStorageDetailRepository;
    private final DocStorageMasterRepository docStorageMasterRepository;
    private final StdGroupRepository stdGroupRepository;

    /* 부서 문서보관 목록표 */
    @Override
    public List<DocstorageResponseDTO> getDocstorageDeptList(String deptCd) {
        List<DocStorageDetail> docStorageDetailList = fetchDocStorageDetailsByDeptCd(deptCd);
        return convertToResponseDTOListUsingMasterStatus(docStorageDetailList);
    }

    /* 승인대기 문서보관 목록표 */
    @Override
    public List<DocstorageResponseDTO> getDocstoragePendingList(String instCd) {

        // 기관 코드로 승인대기 상태의 Master 리스트 조회
        List<DocStorageMaster> docStorageMasterList = fetchDocStorageMastersByInstCdAndA(instCd);

        // Master와 관련된 모든 Detail DTO로 변환
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
                                    .type(master.getType())
                                    .build());
                })
                .toList();
    }

    /* 부서별 문서보관 목록표 */
    @Override
    public List<DocstorageResponseDTO> getDocstorageCenterList(String deptCd) {

        List<DocStorageDetail> docStorageDetailList = fetchDocStorageDetailsByDeptCd(deptCd);

        List<DocStorageDetail> filteredDetails = docStorageDetailList.stream()
                .filter(detail -> "B".equals(detail.getStatus()) || "E".equals(detail.getStatus()))
                .toList();

        List<DocStorageDetail> finalFilteredDetails = filteredDetails.stream()
                .filter(detail -> {
                    DocStorageMaster master = docStorageMasterRepository.findById(detail.getDraftId())
                            .orElse(null);
                    return master != null && "A".equals(master.getType());
                })
                .collect(Collectors.toList());

        return convertToResponseDTOListUsingMasterStatus(finalFilteredDetails);
    }

    @Override
    public List<DocstorageResponseDTO> getTotalCenterDocstorageList(String instCd) {

        StdGroup stdGroup = stdGroupRepository.findByGroupCd("A002")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        List<StdDetail> stdDetailList = stdDetailRepository.findByGroupCdAndEtcItem1(stdGroup, instCd)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        List<DocstorageResponseDTO> responseList = new ArrayList<>();

        for (StdDetail stdDetail : stdDetailList) {
            List<DocStorageDetail> docstorageDetails = docStorageDetailRepository.findByDeptCdAndStatusIn(stdDetail.getDetailCd(), Arrays.asList("B", "E"))
                    .orElse(Collections.emptyList());

            for (DocStorageDetail docstorageDetail : docstorageDetails) {
                if (docstorageDetail.getDraftId() == null) {
                    continue;
                }

                DocStorageMaster docStorageMaster = docStorageMasterRepository.findByDraftId(docstorageDetail.getDraftId())
                        .orElseThrow(() -> new IllegalArgumentException("Not Found"));

                DocstorageResponseDTO responseDTO = DocstorageResponseDTO.builder()
                        .detailId(docstorageDetail.getDetailId())
                        .draftId(docstorageDetail.getDraftId())
                        .teamNm(docstorageDetail.getTeamNm())
                        .docId(docstorageDetail.getDocId())
                        .location(docstorageDetail.getLocation())
                        .docNm(docstorageDetail.getDocNm())
                        .manager(docstorageDetail.getManager())
                        .subManager(docstorageDetail.getSubManager())
                        .storageYear(docstorageDetail.getStorageYear())
                        .createDate(docstorageDetail.getCreateDate())
                        .transferDate(docstorageDetail.getTransferDate())
                        .tsdNum(docstorageDetail.getTsdNum())
                        .disposalDate(docstorageDetail.getDisposalDate())
                        .dpdraftNum(docstorageDetail.getDpdNum())
                        .type(docStorageMaster.getType())
                        .status(docstorageDetail.getStatus())
                        .build();

                responseList.add(responseDTO);
            }
        }

        return responseList;
    }

    /* 전국 센터별 문서보관 목록표 */
    @Override
    public DocstorageTotalListResponseDTO getTotalDocstorageList() {

        // 모든 센터 정보 조회
        List<CenterResponseDTO> centerList = fetchAllCenters();

        Map<String, List<DocstorageResponseDTO>> responseMap = initializeResponseMap();

        // 각 센터에 대해 문서보관 목록을 생성
        centerList.forEach(center -> {
            List<DocStorageMaster> docStorageMasterList = fetchDocStorageMastersByInstCdAndTypeA(center.getDetailCd());

            docStorageMasterList.forEach(master -> {
                List<DocStorageDetail> filteredDetails = fetchDocStorageDetailsByDraftId(master.getDraftId()).stream()
                        .filter(detail -> "E".equals(detail.getStatus()))
                        .toList();

                List<DocstorageResponseDTO> responseDTOs = convertToResponseDTOListUsingDetailStatus(filteredDetails);

                responseMap.computeIfAbsent(center.getDetailCd(), k -> new ArrayList<>()).addAll(responseDTOs);
            });
        });

        CenterDocstorageListResponseDTO centerDocstorageListResponseDTO = buildCenterDocstorageResponseDTO(responseMap);
        return DocstorageTotalListResponseDTO.of(centerList, List.of(centerDocstorageListResponseDTO));
    }

    /* 부서 리스트 반환 */
    @Override
    public List<DeptResponseDTO> getDeptListForCenter(String instCd) {
        return fetchDeptListForCenter(instCd);
    }

    /* 부서 코드로 문서보관 상세 리스트 조회 */
    private List<DocStorageDetail> fetchDocStorageDetailsByDeptCd(String deptCd) {
        return docStorageDetailRepository.findAllByDeptCd(deptCd)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
    }

    /* 센터에 속한 부서 리스트 조회 */
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

    /* 그룹 코드로 표준 그룹 조회 */
    private StdGroup fetchStdGroup(String groupCd) {
        return stdGroupRepository.findByGroupCd(groupCd)
                .orElseThrow(() -> new IllegalArgumentException("Standard Group not found for code: " + groupCd));
    }

    /* 승인대기 상태의 문서보관 Master 리스트 조회 */
    private List<DocStorageMaster> fetchDocStorageMastersByInstCdAndA(String instCd) {
        return docStorageMasterRepository.findAllByInstCdAndStatus(instCd, "A")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
    }

    /* 유형이 'A'인 문서보관 Master 리스트 조회 */
    private List<DocStorageMaster> fetchDocStorageMastersByInstCdAndTypeA(String instCd) {
        return docStorageMasterRepository.findAllByInstCdAndType(instCd, "A")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
    }

    /* 모든 센터 정보 조회 */
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

    /* 센터별 문서보관 목록 초기화 */
    private Map<String, List<DocstorageResponseDTO>> initializeResponseMap() {
        Map<String, List<DocstorageResponseDTO>> responseMap = new HashMap<>();
        for (int i = 100; i <= 108; i++) {
            responseMap.put(String.valueOf(i), new ArrayList<>());
        }
        return responseMap;
    }

    /* 센터별 문서보관 목록에 추가 */
    private void addToResponseMap(Map<String, List<DocstorageResponseDTO>> responseMap, String detailCd, DocStorageMaster master) {
        List<DocstorageResponseDTO> docstorageResponses = getDocstorageResponsesForMaster(master);
        responseMap.getOrDefault(detailCd, new ArrayList<>()).addAll(docstorageResponses);
    }

    /* 센터별 문서보관 목록 DTO 생성 */
    private CenterDocstorageListResponseDTO buildCenterDocstorageResponseDTO(Map<String, List<DocstorageResponseDTO>> responseMap) {
        return CenterDocstorageListResponseDTO.of(
                responseMap.getOrDefault("100", List.of()),
                responseMap.getOrDefault("111", List.of()),
                responseMap.getOrDefault("112", List.of()),
                responseMap.getOrDefault("113", List.of()),
                responseMap.getOrDefault("211", List.of()),
                responseMap.getOrDefault("611", List.of()),
                responseMap.getOrDefault("612", List.of()),
                responseMap.getOrDefault("711", List.of()),
                responseMap.getOrDefault("811", List.of())
        );
    }

    /* DocStorageMaster의 상태를 사용하는 DTO 변환 메서드 */
    private List<DocstorageResponseDTO> convertToResponseDTOListUsingMasterStatus(List<DocStorageDetail> docStorageDetailList) {
        return docStorageDetailList.stream()
                .map(docStorageDetail -> {
                    DocStorageMaster docStorageMaster = null;

                    if (docStorageDetail.getDraftId() != null) {
                        docStorageMaster = docStorageMasterRepository.findById(docStorageDetail.getDraftId())
                                .orElseThrow(() -> new IllegalArgumentException("Master record not found for draftId: " + docStorageDetail.getDraftId()));
                    }

                    return DocstorageResponseDTO.builder()
                            .detailId(docStorageDetail.getDetailId())
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
                            .status(docStorageDetail.getStatus())
                            .build();
                })
                .toList();
    }

    /* DocStorageDetail의 상태를 사용하는 DTO 변환 메서드 */
    private List<DocstorageResponseDTO> convertToResponseDTOListUsingDetailStatus(List<DocStorageDetail> docStorageDetailList) {
        return docStorageDetailList.stream()
                .map(docStorageDetail -> DocstorageResponseDTO.builder()
                        .detailId(docStorageDetail.getDetailId())
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
                        .status(docStorageDetail.getStatus())
                        .build())
                .toList();
    }

    /* Master를 기반으로 문서보관 응답 리스트 생성 */
    private List<DocstorageResponseDTO> getDocstorageResponsesForMaster(DocStorageMaster docStorageMaster) {
        List<DocStorageDetail> docStorageDetails = fetchDocStorageDetailsByDraftId(docStorageMaster.getDraftId());
        return convertToResponseDTOListUsingDetailStatus(docStorageDetails);
    }

    /* Draft ID로 문서보관 상세 리스트 조회 */
    private List<DocStorageDetail> fetchDocStorageDetailsByDraftId(String draftId) {
        return docStorageDetailRepository.findAllByDraftId(draftId)
                .orElseThrow(() -> new IllegalArgumentException("DocStorageDetails not found for draftId: " + draftId));
    }
}
