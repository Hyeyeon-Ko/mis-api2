package kr.or.kmi.mis.api.corpdoc.model.response;

import kr.or.kmi.mis.api.corpdoc.model.entity.CorpDocDetail;
import kr.or.kmi.mis.api.file.model.entity.FileHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class CorpDocDetailResponseDTO {

    private String submission;
    private String purpose;
    private String useDate;
    private String fileName;
    private String filePath;
    private int certCorpseal;
    private int certCoregister;
    private int certUsesignet;
    private int warrant;
    private String type;
    private String notes;

    public String getFileUrl() {
        return filePath != null ? "/api/corpDoc/download/" + fileName : null;
    }

    public static CorpDocDetailResponseDTO of(CorpDocDetail corpDocDetail, FileHistory fileHistory){
        return CorpDocDetailResponseDTO.builder()
                .submission(corpDocDetail.getSubmission())
                .purpose(corpDocDetail.getPurpose())
                .useDate(corpDocDetail.getUseDate())
                .fileName(fileHistory != null ? fileHistory.getFileName() : "")
                .filePath(fileHistory != null ? fileHistory.getFilePath() : "")
                .certCorpseal(corpDocDetail.getCertCorpseal())
                .certCoregister(corpDocDetail.getCertCoregister())
                .certUsesignet(corpDocDetail.getCertUsesignet())
                .warrant(corpDocDetail.getWarrant())
                .type(corpDocDetail.getType())
                .notes(corpDocDetail.getNotes())
                .build();
    }
}
