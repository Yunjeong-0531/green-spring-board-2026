package com.green.spring_board.exceptions;

//해당 사용자가 요청한 작업 인가 불가
public class AuthorizationFailureException extends RuntimeException {
    public AuthorizationFailureException(String message) {
        super(message);
    }
}
