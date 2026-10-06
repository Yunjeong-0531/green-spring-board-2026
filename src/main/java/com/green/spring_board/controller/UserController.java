package com.green.spring_board.controller;

import com.green.spring_board.dto.LoginRequest;
import com.green.spring_board.dto.UserUpdateRequest;
import com.green.spring_board.dto.SignUpRequest;
import com.green.spring_board.exceptions.ResourceConflictException;
import com.green.spring_board.exceptions.ResourceNotFoundException;
import com.green.spring_board.exceptions.UnauthenticatedException;
import com.green.spring_board.exceptions.UserRequestException;
import com.green.spring_board.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpSession;


@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {
    public final UserService userService;


    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody SignUpRequest signUpRequest){
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

    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest httpServletRequest ) {
        try{
            int userId = userService.login(loginRequest);
            HttpSession session = httpServletRequest.getSession();
            httpServletRequest.changeSessionId();
            session.setAttribute("userId", userId);
            return ResponseEntity.ok().build();


        } catch (ResourceNotFoundException e){
            return ResponseEntity.notFound().build();
        } catch (UnauthenticatedException e){
            return ResponseEntity.status(401).build();
        } catch (Exception e) {
           return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserUpdateRequest> getCurrentUser(
            HttpServletRequest httpServletRequest){
        try {
            //내 정보 조회하기(이메일과 닉네임)
            //1.세션 가져오기
            HttpSession session = httpServletRequest.getSession(false);
            if (session == null || session.getAttribute("userId") == null) {
                return ResponseEntity.status(401).build();
            }
            //2. 세션에서 유저 아이디 가져오기
            int userId = (int) session.getAttribute("userId");

            //3.id로 db조회, 이메일과 닉네임 받아오기, 반환
            UserUpdateRequest userUpdateRequest = userService.getUserInfo(userId);
            return ResponseEntity.ok().body(userUpdateRequest);
        } catch (ResourceNotFoundException e){
            return ResponseEntity.notFound().build();
        } catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout (
            HttpServletRequest request
    ){
        HttpSession session = request.getSession(false);

        if(session==null || session.getAttribute("userId")==null){
            return ResponseEntity.status(401).build();
        }
        session.invalidate();
        return ResponseEntity.ok().build();
    }

    @PatchMapping("{id}")
    public ResponseEntity<Void> updateInfo(
            HttpServletRequest request,
    @Valid @RequestBody UserUpdateRequest userUpdateRequest){
        //현재 유저 가져와서 해당 유저 정보로 덮어씌우기
        //null이면 수정하지 않기
        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("userId") == null) {
                return ResponseEntity.status(401).build();
            }
            int id = (int) session.getAttribute("userId");
            userService.updateInfo(id, userUpdateRequest);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e){
            return ResponseEntity.notFound().build();
        } catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(
            HttpServletRequest request
    ){
        HttpSession session = request.getSession(false);
        if(session ==null||session.getAttribute("userId")==null){
            return ResponseEntity.status(401).build();
        }
        int id = (int)session.getAttribute("userId");
        userService.deleteUser(id);
        session.invalidate();

        return ResponseEntity.noContent().build();
    }

}
