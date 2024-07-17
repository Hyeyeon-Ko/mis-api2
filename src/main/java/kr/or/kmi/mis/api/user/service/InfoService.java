package kr.or.kmi.mis.api.user.service;

import kr.or.kmi.mis.api.user.model.response.InfoDetailResponseDTO;
import kr.or.kmi.mis.api.user.model.response.InfoResponseDTO;

public interface InfoService {

    public InfoResponseDTO getUserInfo();

    /**
     * 명함 신청 > 본인 클릭 > userId == null
     * 명함 신청 > 타인 클릭 > 명함 대상자 선택  > userId = 대상자사번
     * */
    public InfoDetailResponseDTO getUserInfoDetail(String userId);
}
