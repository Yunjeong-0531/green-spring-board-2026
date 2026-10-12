package com.green.spring_board.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SignUpRequest {

    @NotBlank
    @Size(max=30)
    private String username;

    @NotBlank
    @Email
    @Size(max=100)
    private String email;

    @NotBlank
    @Size(min=6)
    private String password;

    @NotBlank
    @Size(max=30)
    private String nickname;
}

