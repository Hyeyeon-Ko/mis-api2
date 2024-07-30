package kr.or.kmi.mis.cmm.model.response;

import kr.or.kmi.mis.cmm.model.response.code.CodeMessage;
import org.springframework.http.HttpStatus;

/**
 * packageName    : mng.mrk.cmm.response
 * fileName       : ResponseWrapper
 * author         : clsung
 * date           : 2024-04-11
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-04-11        clsung       the first create
 */
public class ResponseWrapper {
    /**
     * Success api response.
     *
     * @param <T>  the type parameter
     * @param data the data
     * @return the api response
     */
    public static <T> ApiResponse<T> success(T data){
        return new ApiResponse<T>(HttpStatus.OK.value(), CodeMessage.SUCCESS_OK.message, data);
    }

    /**
     * Success api response.
     *
     * @return the api response
     */
    public static ApiResponse success(){
        return new ApiResponse(HttpStatus.OK.value(), CodeMessage.SUCCESS_OK.message);
    }

    /**
     * Error api response.
     *
     * @param <T>  the type parameter
     * @param data the data
     * @return the api response
     */
    public static <T> ApiResponse<T> error(T data){
        return new ApiResponse<T>(HttpStatus.NOT_FOUND.value(), CodeMessage.ERROR_NOT_FOUND.message, data);
    }

    /**
     * Error api response.
     *
     * @return the api response
     */
    public static ApiResponse error(){
        return new ApiResponse(HttpStatus.NOT_FOUND.value(), CodeMessage.ERROR_NOT_FOUND.message);
    }

    /**
     * Null error api response.
     *
     * @param <T>  the type parameter
     * @param data the data
     * @return the api response
     */
    public static <T> ApiResponse<T> nullError(T data){
        return new ApiResponse<T>(000, "값이 존재하지 않습니다.", data);
    }

    /**
     * Null error api response.
     *
     * @return the api response
     */
    public static ApiResponse nullError(){
        return new ApiResponse(000, "값이 존재하지 않습니다.");
    }

    /**
     * Params error api response.
     *
     * @param <T>  the type parameter
     * @param data the data
     * @return the api response
     */
    public static <T> ApiResponse<T> paramsError(T data){
        return new ApiResponse<T>(999, data);
    }
}
