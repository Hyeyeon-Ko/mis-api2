package kr.or.kmi.mis.cmm.controller;

import kr.or.kmi.mis.api.exception.EntityNotFoundException;
import kr.or.kmi.mis.cmm.response.ApiResponse;
import kr.or.kmi.mis.cmm.response.ResponseWrapper;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ApiResponse<?> EntityNotFoundException(final EntityNotFoundException e) {
        return ResponseWrapper.error(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<?> IllegalArgumentException(final IllegalArgumentException e) {
        return ResponseWrapper.error();
    }
}
