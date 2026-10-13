package com.green.spring_board.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LogoutRequest {
    private String accessToken;
    private String refreshToken;

}
