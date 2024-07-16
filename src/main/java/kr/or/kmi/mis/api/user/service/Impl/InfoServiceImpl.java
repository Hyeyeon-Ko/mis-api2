package kr.or.kmi.mis.api.user.service.Impl;

import jakarta.servlet.http.HttpServletRequest;
import kr.or.kmi.mis.api.user.model.response.InfoResponseDTO;
import kr.or.kmi.mis.api.user.service.InfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InfoServiceImpl implements InfoService {

    private final HttpServletRequest request;

    public InfoResponseDTO getUserInfo() {
        String currentUserId = (String) request.getSession().getAttribute("userId");
        String currentUser = (String) request.getSession().getAttribute("hngnm");

        return InfoResponseDTO.of(currentUserId, currentUser);
    }
}
