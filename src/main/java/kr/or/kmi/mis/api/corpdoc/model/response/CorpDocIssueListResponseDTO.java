package kr.or.kmi.mis.api.corpdoc.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

@Builder
@Data
@AllArgsConstructor
public class CorpDocIssueListResponseDTO {

    private Page<CorpDocIssueResponseDTO> issuePendingList;
    private Page<CorpDocIssueResponseDTO> issueList;

    public static CorpDocIssueListResponseDTO of(Page<CorpDocIssueResponseDTO> issueList,
                                                 Page<CorpDocIssueResponseDTO> issuePendingList) {
        return CorpDocIssueListResponseDTO.builder()
                .issueList(issueList)
                .issuePendingList(issuePendingList)
                .build();
    }
}
