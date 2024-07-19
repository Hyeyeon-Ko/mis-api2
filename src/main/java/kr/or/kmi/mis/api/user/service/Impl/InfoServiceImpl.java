package kr.or.kmi.mis.api.user.service.Impl;

import jakarta.servlet.http.HttpServletRequest;
import kr.or.kmi.mis.api.authority.model.response.ResponseData;
import kr.or.kmi.mis.api.authority.service.AuthorityService;
import kr.or.kmi.mis.api.user.model.response.InfoDetailResponseDTO;
import kr.or.kmi.mis.api.user.model.response.InfoResponseDTO;
import kr.or.kmi.mis.api.user.service.InfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class InfoServiceImpl implements InfoService {

    private final HttpServletRequest request;
    private final AuthorityService authorityService;

    @Override
    @Transactional(readOnly = true)
    public InfoResponseDTO getUserInfo() {
        String currentUserId = (String) request.getSession().getAttribute("userId");
        String currentUser = (String) request.getSession().getAttribute("hngNm");

        return InfoResponseDTO.of(currentUserId, currentUser);
    }

    @Override
    @Transactional(readOnly = true)
    public InfoDetailResponseDTO getUserInfoDetail(String userId) {

        if (userId == null) {
            userId = (String) request.getSession().getAttribute("userId");
        }

        ResponseData.ResultData resultData = authorityService.fetchUserInfo(userId).block();

        String userNm = Objects.requireNonNull(resultData).getUsernm();        // 성명
        String telNum = Objects.requireNonNull(resultData).getMpphonno();      // 전화번호
        String userEmail = Objects.requireNonNull(resultData).getEmail();      // 이메일

        return InfoDetailResponseDTO.of(userId, userNm, telNum, userEmail);
    }
}
