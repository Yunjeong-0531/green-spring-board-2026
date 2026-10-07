package com.green.spring_board.controller;

import com.green.spring_board.dto.*;
import com.green.spring_board.entity.Board;
import com.green.spring_board.exceptions.*;
import com.green.spring_board.service.UserService;
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
public class UserController {
    public final UserService userService;


    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody SignUpRequest signUpRequest) {

            userService.signUp(signUpRequest);
            return ResponseEntity.ok(ApiResponse.ok());

    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Void>> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest httpServletRequest ) {

            int userId = userService.login(loginRequest);
            HttpSession session = httpServletRequest.getSession();
            httpServletRequest.changeSessionId();
            session.setAttribute("userId", userId);
            return ResponseEntity.ok(ApiResponse.ok());

    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserUpdateRequest>> getCurrentUser(
            HttpServletRequest httpServletRequest){

            //내 정보 조회하기(이메일과 닉네임)
            //1.세션 가져오기
            HttpSession session = httpServletRequest.getSession(false);
            if (session == null || session.getAttribute("userId") == null) {
                throw new UnauthenticatedException("로그인이 필요합니다.");
            }
            //2. 세션에서 유저 아이디 가져오기
            int userId = (int) session.getAttribute("userId");

            //3.id로 db조회, 이메일과 닉네임 받아오기, 반환
            UserUpdateRequest userUpdateRequest = userService.getUserInfo(userId);
            return ResponseEntity.ok(ApiResponse.ok(userUpdateRequest));

    }

    @GetMapping("/me/board")
    public ResponseEntity<ApiResponse<List<BoardResponse>>> getMyBoard(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        if(session==null||session.getAttribute("userId")==null){
            throw new UnauthenticatedException("로그인이 필요합니다.");
        }
        int id = (int) session.getAttribute("userId");

        List<BoardResponse> boardResponses = userService.getMyBoard(id);
        return ResponseEntity.ok(ApiResponse.ok(boardResponses));


    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout (
            HttpServletRequest request
    ){
        HttpSession session = request.getSession(false);

        if(session==null || session.getAttribute("userId")==null){
           throw new UnauthenticatedException("로그인이 필요합니다.");
        }
        session.invalidate();
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<Void>> updateInfo(
            HttpServletRequest request,
    @Valid @RequestBody UserUpdateRequest userUpdateRequest){
        //현재 유저 가져와서 해당 유저 정보로 덮어씌우기
        //null이면 수정하지 않기

            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("userId") == null) {
               throw new UnauthenticatedException("로그인이 필요합니다.") ;
            }
            int id = (int) session.getAttribute("userId");
            userService.updateInfo(id, userUpdateRequest);
            return ResponseEntity.ok(ApiResponse.ok());

    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            HttpServletRequest request
    ){
        HttpSession session = request.getSession(false);
        if(session ==null||session.getAttribute("userId")==null){
            throw new UnauthenticatedException("로그인이 필요합니다");
        }
        int id = (int)session.getAttribute("userId");
        userService.deleteUser(id);
        session.invalidate();
        return ResponseEntity.ok().body(ApiResponse.ok());
//        return ResponseEntity.noContent().build();
    }

}

