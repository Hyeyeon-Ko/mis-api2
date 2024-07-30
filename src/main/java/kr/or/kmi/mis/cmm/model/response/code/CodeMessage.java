package kr.or.kmi.mis.cmm.model.response.code;

/**
 * packageName    : kr.or.kmi.mis.cmm.response.code
 * fileName       : CodeMessage
 * author         : clsung
 * date           : 2024-07-02
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-02        clsung       the first create
 */
public enum CodeMessage {
    /**
     * Success ok code message.
     */
    SUCCESS_OK(200, "성공"),
    /**
     * Success created code message.
     */
    SUCCESS_CREATED(201, "생성"),
    /**
     * The Error bad request.
     */
    ERROR_BAD_REQUEST(400, "BAD REQUEST"),
    /**
     * The Error forbidden.
     */
    ERROR_FORBIDDEN(403, "FORBIDDEN ERROR"),
    /**
     * Error not found code message.
     */
    ERROR_NOT_FOUND(404, "NOT_FOUND"),
    /**
     * Error internal server code message.
     */
    ERROR_INTERNAL_SERVER(500, "INTERNAL_SERVER")
    ;

    /**
     * The Code.
     */
    public int code;
    /**
     * The Message.
     */
    public String message;

    CodeMessage(int code, String message){
        this.code = code;
        this.message = message;
    }
}
