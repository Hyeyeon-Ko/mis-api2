package kr.or.kmi.mis.api.corpdoc.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class CorpDocIssueListResponseDTO {

    private List<CorpDocIssueResponseDTO> issuePendingList;
    private List<CorpDocIssueResponseDTO> issueList;

    public static CorpDocIssueListResponseDTO of(List<CorpDocIssueResponseDTO> issueList,
                                                 List<CorpDocIssueResponseDTO> issuePendingList) {
        return CorpDocIssueListResponseDTO.builder()
                .issueList(issueList)
                .issuePendingList(issuePendingList)
                .build();
    }
}
