package com.green.spring_board.global;

import com.green.spring_board.exceptions.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.awt.*;
import org.springframework.validation.FieldError;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //해당 데이터 조회했는데 없을 때 ---404
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Void> handleNotFound(ResourceNotFoundException e) {
        return ResponseEntity.notFound().build();
    }

    //인증 정보 없거나 적절하지 않을 때 ----401
    @ExceptionHandler(UnauthenticatedException.class)
    public ResponseEntity<Void> handleUnauthenticated(UnauthenticatedException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }


    //Validator가 검증 이후 반환하는 에러
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationError(MethodArgumentNotValidException e) {
        String resultMessage = "";
        List<FieldError> errors = e.getBindingResult().getFieldErrors();
        for (FieldError error : errors) {
            resultMessage = resultMessage + error.getField() + "은(는)" +
                    error.getDefaultMessage() + "\n";
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.parseMediaType("text/plain;charset=UTF-8"))
                .body(resultMessage);


    }

    //고유값이 중복되어 저장에 실패하거나, 존재하지 않는 외래키 이용해
    // 데이터 생성 시도 등 문제 상황 공통 처리
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataConflict(DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.parseMediaType("text/plain;charset=UTF-8"))
                .body("중복되거나 저장할 수 없는 데이터입니다.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.parseMediaType("text/plain;charset=UTF-8"))
                .body("서버에서 오류가 발생했습니다.");
    }


    //인증 정보가 없을 때가 아닌, 권한이 부족할 때
    @ExceptionHandler(AuthorizationFailureException.class)
    public ResponseEntity<Void> handleForbidden(AuthorizationFailureException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    //요청한 작업 수행하기에 현재 객체의 상태가 올바르지 않을 때
    //로그인 이후 로그인 요청
    @ExceptionHandler(InvalidStateException.class)
    public ResponseEntity<Void> handleInvalidState(InvalidStateException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }


    //세션 방식 로그인 시 이메일 중복
    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<String> handleResourceConflict(ResourceConflictException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

}