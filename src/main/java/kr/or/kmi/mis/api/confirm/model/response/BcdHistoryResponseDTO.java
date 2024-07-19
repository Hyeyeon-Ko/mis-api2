package kr.or.kmi.mis.api.confirm.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BcdHistoryResponseDTO {

    private String title;
    private String draftDate;
    private String applyStatus;

    @Builder
    public BcdHistoryResponseDTO(String title, String draftDate, String applyStatus) {
        this.title = title;
        this.draftDate = draftDate;
        this.applyStatus = applyStatus;
    }
}
