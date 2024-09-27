package kr.or.kmi.mis.api.file.service;

import kr.or.kmi.mis.api.file.model.entity.FileDetail;

public interface FileHistorySevice {
    void createFileHistory(FileDetail fileDetail, String type);
}
