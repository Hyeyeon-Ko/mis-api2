package kr.or.kmi.mis.api.seal.util;

import org.springframework.web.multipart.MultipartFile;
import java.util.Base64;
import java.io.IOException;

public class ImageUtil {

    // 이미지를 Base64로 인코딩
    public static String encodeImageToBase64(MultipartFile image) throws IOException {
        byte[] imageBytes = image.getBytes();
        return Base64.getEncoder().encodeToString(imageBytes);
    }

}
