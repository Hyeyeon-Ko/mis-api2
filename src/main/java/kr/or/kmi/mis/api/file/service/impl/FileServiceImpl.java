package kr.or.kmi.mis.api.file.service.impl;

import kr.or.kmi.mis.api.file.model.entity.FileDetail;
import kr.or.kmi.mis.api.file.model.request.FileUploadRequestDTO;
import kr.or.kmi.mis.api.file.repository.FileDetailRepository;
import kr.or.kmi.mis.api.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Service
@Transactional
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileDetailRepository fileDetailRepository;

    @Override
    public void uploadFile(FileUploadRequestDTO fileUploadRequestDTO) {
        FileDetail fileDetail = new FileDetail(fileUploadRequestDTO);
        fileDetail.setUpdtrId(fileUploadRequestDTO.getDrafter());
        fileDetail.setUpdtDt(new Timestamp(System.currentTimeMillis()));
        fileDetailRepository.save(fileDetail);
    }
}
