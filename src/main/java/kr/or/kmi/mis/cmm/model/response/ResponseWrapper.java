package kr.or.kmi.mis.cmm.model.response;

import kr.or.kmi.mis.cmm.model.response.code.CodeMessage;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ResponseWrapper {
    /**
     * Success api response.
     *
     * @param <T>  the type parameter
     * @param data the data
     * @return the api response
     */
    public static <T> ApiResponse<T> success(T data){
        log.info("data : {}", data);
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
        log.error("data : {}", data);
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
        log.info("data : {}", data);
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
        log.info("data : {}", data);
        return new ApiResponse<T>(999, data);
    }

    /**
     * Error response with custom HTTP status.
     *
     * @param <T>     the type parameter
     * @param data    the error message or data
     * @param status  the HTTP status code
     * @return the api response
     */
    public static <T> ApiResponse<T> error(T data, HttpStatus status) {
        log.error("Error: {}, Status: {}", data, status);
        return new ApiResponse<T>(status.value(), data.toString(), null);
    }

    /**
     * Error response with default message and custom HTTP status.
     *
     * @param status the HTTP status code
     * @return the api response
     */
    public static ApiResponse error(HttpStatus status) {
        log.error("Error Status: {}", status);
        return new ApiResponse(status.value(), status.getReasonPhrase());
    }
}
