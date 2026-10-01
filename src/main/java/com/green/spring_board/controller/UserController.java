package com.green.spring_board.controller;

import com.green.spring_board.dto.SignUpRequest;
import com.green.spring_board.exceptions.ResourceConflictException;
import com.green.spring_board.exceptions.UserRequestException;
import com.green.spring_board.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {
    public final UserService userService;

    @PostMapping
    public ResponseEntity<Void> signup(@RequestBody SignUpRequest signUpRequest){
        try {
            userService.signUp(signUpRequest);
            return ResponseEntity.ok().build();
        } catch (ResourceConflictException e){
          //db에 중복된 값이 이미 있을 때
            return ResponseEntity.status(409).build();
        } catch (UserRequestException e){
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}
