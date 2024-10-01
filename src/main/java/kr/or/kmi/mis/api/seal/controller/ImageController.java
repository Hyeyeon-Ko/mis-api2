package kr.or.kmi.mis.api.seal.controller;

import kr.or.kmi.mis.api.seal.model.entity.SealRegisterDetail;
import kr.or.kmi.mis.api.seal.repository.SealRegisterDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final SealRegisterDetailRepository sealRegisterDetailRepository;

    @GetMapping("/{draftId}")
    public ResponseEntity<String> getImageAsBase64(@PathVariable String draftId) {
        SealRegisterDetail detail = sealRegisterDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        String base64Image = detail.getSealImage();
        if (base64Image != null) {
            return ResponseEntity.ok(base64Image);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
