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

    // Base64 문자열을 디코딩하여 이미지로 변환 (필요 시 사용)
    public static byte[] decodeBase64ToImage(String base64Image) {
        return Base64.getDecoder().decode(base64Image);
    }
}
