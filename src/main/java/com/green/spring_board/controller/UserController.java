package com.green.spring_board.controller;

import com.green.spring_board.dto.*;
import com.green.spring_board.entity.Board;
import com.green.spring_board.exceptions.*;
import com.green.spring_board.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;

import java.util.List;


@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
@Tag(name="그린보드 유저 API", description = "사용자 API입니다.")
public class UserController {
    public final UserService userService;

    @Operation(summary = "회원가입 API", description = "회원가입 시 사용")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody SignUpRequest signUpRequest) {

            userService.signUp(signUpRequest);
            return ResponseEntity.ok(ApiResponse.ok());

    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest) {

            LoginResponse result = userService.login(loginRequest);

            return ResponseEntity.ok(ApiResponse.ok(result));

    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserUpdateRequest>> getCurrentUser(){

//            //내 정보 조회하기(이메일과 닉네임)
//            //1.세션 가져오기
//            HttpSession session = httpServletRequest.getSession(false);
//            if (session == null || session.getAttribute("userId") == null) {
//                throw new UnauthenticatedException("로그인이 필요합니다.");
//            }
//            //2. 세션에서 유저 아이디 가져오기
//            int userId = (int) session.getAttribute("userId");

            //3.id로 db조회, 이메일과 닉네임 받아오기, 반환
            UserUpdateRequest userUpdateRequest = userService.getUserInfo();
            return ResponseEntity.ok(ApiResponse.ok(userUpdateRequest));

    }

    @GetMapping("/me/board")
    public ResponseEntity<ApiResponse<List<BoardResponse>>> getMyBoard(){
             List<BoardResponse> boardResponses = userService.getMyBoard();
        return ResponseEntity.ok(ApiResponse.ok(boardResponses));


    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout (
            @RequestHeader("Authorization") String authorization
    ){
        String accessToken = authorization.substring(7);
        userService.logout(accessToken);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshResponse>> refresh(
            @Valid @RequestBody RefreshRequest refreshRequest
    ){
        RefreshResponse refreshResponse = userService.Refresh(refreshRequest);
        return ResponseEntity.ok(ApiResponse.ok(refreshResponse));
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<Void>> updateInfo(
    @Valid @RequestBody UserUpdateRequest userUpdateRequest){
        //현재 유저 가져와서 해당 유저 정보로 덮어씌우기
        //null이면 수정하지 않기

            userService.updateInfo(userUpdateRequest);
            return ResponseEntity.ok(ApiResponse.ok());

    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteUser(
    ){
        userService.deleteUser();
        return ResponseEntity.ok().body(ApiResponse.ok());
//        return ResponseEntity.noContent().build();
    }

}

