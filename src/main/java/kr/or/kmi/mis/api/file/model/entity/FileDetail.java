package kr.or.kmi.mis.api.file.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.or.kmi.mis.api.file.model.request.FileUploadRequestDTO;
import kr.or.kmi.mis.cmm.model.entity.BaseSystemFieldEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "hrmtfiled")
public class FileDetail extends BaseSystemFieldEntity {

    @Id
    @Column(name = "attach_id", length = 12)
    private String attachId;

    @Column(name = "draft_id", length = 12)
    private String draftId;

    @Column(name = "use_at", length = 1)
    private String useAt;

    public FileDetail(FileUploadRequestDTO fileUploadRequestDTO, String attachId) {
        this.attachId = attachId;
        this.draftId = fileUploadRequestDTO.getDraftId();
        this.useAt = "Y";
    }

    public void updateUseAt(String useAt) {
        this.useAt = useAt;
    }
}
