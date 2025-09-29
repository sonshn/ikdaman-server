package com.ikdaman.global.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j2;
import java.sql.SQLException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.ikdaman.global.exception.ErrorCode.*;

/**
 * 전역 예외 처리 핸들러 클래스
 */
@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    /**
     * 커스텀 예외(BaseException) 처리 핸들러
     *
     * @param ex BaseException
     * @return 에러 응답(ResponseEntity<ErrorDto>)
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorRes> handleCustomException(BaseException ex) {
        log.error(ex.getMessage(), ex);
        ErrorRes errorRes = ErrorRes.builder()
                .status(ex.getErrorCode().getStatus())
                .code(String.valueOf(ex.getErrorCode().getCode()))
                .message(ex.getErrorCode().getMessage())
                .build();
        return new ResponseEntity<>(errorRes, HttpStatus.valueOf(ex.getErrorCode().getStatus()));
    }

    /**
     * NullPointerException 처리 핸들러
     *
     * @param ex NullPointerException
     * @return 에러 응답(ResponseEntity<ErrorDto>)
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorRes> handleNullPointerException(NullPointerException ex) {
        log.error(ex.getMessage(), ex);
        ErrorRes errorRes = ErrorRes.builder()
                .status(NULL_POINTER_EXCEPTION.getStatus())
                .code(String.valueOf(NULL_POINTER_EXCEPTION.getCode()))
                .message(NULL_POINTER_EXCEPTION.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.valueOf("application/json; charset=UTF-8"))
                .body(errorRes);
    }

    /**
     * SQL 관련 예외(SQLException) 처리 핸들러
     *
     * @param ex SQLException
     * @return 에러 응답(ResponseEntity<ErrorDto>)
     */
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErrorRes> handleSQLException(SQLException ex) {
        log.error(ex.getMessage(), ex);
        ErrorRes errorRes = ErrorRes.builder()
                .status(DATABASE_ERROR.getStatus())
                .code(String.valueOf(DATABASE_ERROR.getCode()))
                .message(DATABASE_ERROR.getMessage())
                .build();
        return new ResponseEntity<>(errorRes, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Validate 관련 예외(ConstraintViolationException) 처리 핸들러
     *
     * @param ex ConstraintViolationException
     * @return 에러 응답(ResponseEntity<ErrorDto>)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorRes> handleException(ConstraintViolationException ex) {
        log.error(ex.getMessage(), ex);
        ErrorRes errorRes = ErrorRes.builder()
                .status(BAD_REQUEST_BY_VALIDATION.getStatus())
                .code(String.valueOf(BAD_REQUEST_BY_VALIDATION.getCode()))
                .message(ex.getMessage().split(": ")[1])
                .build();

        return new ResponseEntity<>(errorRes, HttpStatus.BAD_REQUEST);
    }

    /**
     * Valid 관련 예외(MethodArgumentNotValidException) 처리 핸들러
     *
     * @param ex MethodArgumentNotValidException
     * @return 에러 응답(ResponseEntity<ErrorDto>)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorRes> handleException(MethodArgumentNotValidException ex) {
        log.error(ex.getMessage(), ex);
        ErrorRes errorRes = ErrorRes.builder()
                .status(BAD_REQUEST_BY_VALIDATION.getStatus())
                .code(String.valueOf(BAD_REQUEST_BY_VALIDATION.getCode()))
                .message(ex.getBindingResult().getFieldError().getDefaultMessage())
                .build();

        return new ResponseEntity<>(errorRes, HttpStatus.BAD_REQUEST);
    }

    /**
     * 그 외 모든 예외(Exception) 처리 핸들러
     *
     * @param ex Exception
     * @return 에러 응답(ResponseEntity<ErrorDto>)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorRes> handleException(Exception ex) {
        log.error(ex.getMessage(), ex);
        ErrorRes errorRes = ErrorRes.builder()
                .status(INTERNAL_SERVER_ERROR.getStatus())
                .code(String.valueOf(INTERNAL_SERVER_ERROR.getCode()))
                .message(INTERNAL_SERVER_ERROR.getMessage())
                .build();
        return new ResponseEntity<>(errorRes, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
