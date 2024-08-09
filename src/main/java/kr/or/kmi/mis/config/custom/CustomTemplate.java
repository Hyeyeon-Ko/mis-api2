package kr.or.kmi.mis.config.custom;

import com.querydsl.core.types.Ops;
import com.querydsl.jpa.JPQLTemplates;

/**
 * packageName    : kr.or.kmi.mis.config.custom
 * fileName       : CustomTemplate
 * author         : KMI_DI
 * date           : 2024-07-17
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-17        KMI_DI       the first create
 */
public class CustomTemplate extends JPQLTemplates {
    public static final CustomTemplate DEFAULT = new CustomTemplate();

    public CustomTemplate() {
        super();
        addCustomFunctions();
    }

    protected void addCustomFunctions() {
        // 사용자 정의 함수 등록
        add(Ops.STRING_CAST, "fn_getCodeNm({0}, {1})");
    }

}
