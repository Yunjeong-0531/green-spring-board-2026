package com.green.spring_board.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserUpdateRequest {

    @Email
    @Size(max=100)
    private String email;

    @Size(min=1, max=30)
    private String nickname;
}
