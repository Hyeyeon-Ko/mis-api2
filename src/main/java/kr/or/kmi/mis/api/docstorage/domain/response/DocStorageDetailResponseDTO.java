package kr.or.kmi.mis.api.docstorage.domain.response;

import kr.or.kmi.mis.api.docstorage.domain.entity.DocStorageDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
public class DocStorageDetailResponseDTO {

    private Long detailId;
    private String docId;
    private String docNm;
    private String teamNm;
    private String manager;
    private String subManager;
    private String storageYear;
    private String createDate;
    private String disposalDate;
    private String registr;
    private String lastupdtr;
    private LocalDateTime lastupdtDt;
    private String type;
    private String status;
    private String transferDate;
    private String tsdNum;
    private String dpdNum;
    private String location;

    public static DocStorageDetailResponseDTO of(DocStorageDetail detail, String type, String status) {
        return DocStorageDetailResponseDTO.builder()
                .detailId(detail.getDetailId())
                .docId(detail.getDocId())
                .docNm(detail.getDocNm())
                .teamNm(detail.getTeamNm())
                .manager(detail.getManager())
                .subManager(detail.getSubManager())
                .storageYear(detail.getStorageYear())
                .createDate(detail.getCreateDate())
                .disposalDate(detail.getDisposalDate())
                .transferDate(detail.getTransferDate())
                .tsdNum(detail.getTsdNum())
                .dpdNum(detail.getDpdNum())
                .location(detail.getLocation())
                .registr(detail.getRgstrId())
                .lastupdtr(detail.getUpdtrId())
                .lastupdtDt(detail.getUpdtDt())
                .type(type)
                .status(status)
                .build();
    }
}
