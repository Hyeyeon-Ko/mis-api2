package kr.or.kmi.mis.cmm.controller;

import kr.or.kmi.mis.api.exception.EntityNotFoundException;
import kr.or.kmi.mis.cmm.model.entity.SessionExpiredException;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleEntityNotFoundException(EntityNotFoundException e) {
        return new ResponseEntity<>(ResponseWrapper.error(e.getMessage()), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalArgumentException(IllegalArgumentException e) {
        return new ResponseEntity<>(ResponseWrapper.error(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponse<String>> handleNullPointerException(NullPointerException e) {
        return new ResponseEntity<>(ResponseWrapper.error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalStateException(IllegalStateException e) {
        return new ResponseEntity<>(ResponseWrapper.error(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchBeanDefinitionException.class)
    public ResponseEntity<ApiResponse<String>> handleNoSuchBeanDefinitionException(NoSuchBeanDefinitionException e) {
        return new ResponseEntity<>(ResponseWrapper.error(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SessionExpiredException.class)
    public ResponseEntity<ApiResponse<String>> handleSessionExpiredException(SessionExpiredException e) {
        return new ResponseEntity<>(ResponseWrapper.error(e.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<String>> handleRuntimeException(RuntimeException e) {
        return new ResponseEntity<>(ResponseWrapper.error("An unexpected error occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

