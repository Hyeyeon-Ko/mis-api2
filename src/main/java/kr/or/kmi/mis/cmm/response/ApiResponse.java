package kr.or.kmi.mis.cmm.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * packageName    : mng.mrk.cmm.response
 * fileName       : ApiResponse
 * author         : clsung
 * date           : 2024-04-11
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-04-11        clsung       the first create
 */
@Getter
@RequiredArgsConstructor
public class ApiResponse<T> {
    private int code = 0;
    private String message = "";
    private T data = null;

    /**
     * Instantiates a new Api response.
     *
     * @param code    the code
     * @param message the message
     * @param data    the data
     */
    public ApiResponse(int code, String message, T data){
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * Instantiates a new Api response.
     *
     * @param code    the code
     * @param message the message
     */
    public ApiResponse(int code, String message){
        this.code = code;
        this.message = message;
    }


    /**
     * Instantiates a new Api response.
     *
     * @param code the code
     * @param data the data
     */
    public ApiResponse(int code, T data){
        this.code = code;
        this.data = data;
    }
}
