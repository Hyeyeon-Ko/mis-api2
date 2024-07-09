package kr.or.kmi.mis.api.user.model.response;

import lombok.Data;

@Data
public class LoginResponseDTO {

    // 로그인관련 응답객체인데 왜 결과코드가 필요한건가요?
    // 이 부분도 ResponseWrapper 에서 이미 결과 값을 code/message/data로 전달해주는데
    // 사용하는 이유가 뭔가요
    private String resultCd;
    // private String hngNm;
    private String resultMsg;
}
