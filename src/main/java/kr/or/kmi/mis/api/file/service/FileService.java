package kr.or.kmi.mis.api.file.service;

import kr.or.kmi.mis.api.file.model.request.FileUploadRequestDTO;

public interface FileService {

    void uploadFile(FileUploadRequestDTO fileUploadRequestDTO);
}
