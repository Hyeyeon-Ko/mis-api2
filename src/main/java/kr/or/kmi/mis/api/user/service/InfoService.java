package kr.or.kmi.mis.api.user.service;

import kr.or.kmi.mis.api.user.model.response.*;
import reactor.core.publisher.Mono;

import java.util.List;

public interface InfoService {

    InfoResponseDTO getUserInfo();

    /**
     * 명함 신청 > 본인 클릭 > userId == null
     * 명함 신청 > 타인 클릭 > 명함 대상자 선택  > userId = 대상자사번
     * */
    InfoDetailResponseDTO getUserInfoDetail(String userId);

    List<OrgChartResponseDTO> getOrgChartInfo(String detailCd);

    List<ConfirmResponseDTO> getConfirmInfo(String instCd);

    Mono<List<OrgChartResponseData.OrgChartData>> fetchOrgChartInfo();
}
