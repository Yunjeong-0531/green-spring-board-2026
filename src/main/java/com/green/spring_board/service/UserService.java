package com.green.spring_board.service;

import com.green.spring_board.dto.*;
import com.green.spring_board.entity.Board;
import com.green.spring_board.entity.User;
import com.green.spring_board.exceptions.AuthorizationFailureException;
import com.green.spring_board.exceptions.ResourceConflictException;
import com.green.spring_board.exceptions.ResourceNotFoundException;
import com.green.spring_board.exceptions.UnauthenticatedException;
import com.green.spring_board.global.UserState;
import com.green.spring_board.repository.LikeRepository;
import com.green.spring_board.repository.UserRepository;

import com.green.spring_board.repository.BoardRepository;
import com.green.spring_board.security.JwtTokenProvider;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final LikeRepository likeRepository;
    //빈으로 등록한 passwordEncoder사용
    private final PasswordEncoder passwordEncoder; //=new BCryptPasswordEncoder();
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;


    public void signUp(SignUpRequest signUpRequest){
        //아이디가 사용중인지 확인
        if(userRepository.existsByUsername(signUpRequest.getUsername())){
            throw new ResourceConflictException("존재하는 사용자 ID입니다.");
        }
        //비밀번호 해싱
        String hashedPassword = passwordEncoder.encode(signUpRequest.getPassword());
        //db save
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setNickname(signUpRequest.getNickname());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(hashedPassword);
        user.setState(UserState.active);
        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest loginRequest){
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(), loginRequest.getPassword()
            ));
        } catch (AuthenticationException e){
            throw new UnauthenticatedException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        //사용자가 넘겨준 이메일 존재하는지 확인
        //optional이 아닌 user로 결과를 받는다.
        //아래 줄은 없어도 정상 작동한다.
        User user = userRepository.findByUsername(loginRequest.getUsername());
        if (user==null){
            throw new ResourceNotFoundException("존재하지 않는 이메일입니다.");
        }
        if(user.getState()==UserState.quitted) {
            throw new ResourceNotFoundException("탈퇴된 회원입니다.");
        }
        //엑세스 토큰 발급
        String accessToken = tokenProvider.generateAccessToken(user.getUsername());
        //로그인 성공
        return new LoginResponse(accessToken);

    }

    public UserUpdateRequest getUserInfo(int id){
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()){
            throw new ResourceNotFoundException("존재하지 않는 사용자id가 입력되었습니다.");
        }

        User user = userOptional.get();

        if(user.getState()==UserState.quitted){
            throw new ResourceNotFoundException("탈퇴된 회원입니다.");
        }
            return new UserUpdateRequest(
                user.getEmail(), user.getNickname());
    }

    public void updateInfo(int id,
                           UserUpdateRequest userUpdateRequest){
        Optional<User> userOptional = userRepository.findById(id);
        if(userOptional.isEmpty()){
            throw new ResourceNotFoundException("존재하지 않는 사용자 Id입니다.");
        }
        //이메일 닉네임 유효값 확인
        User user = userOptional.get();

        if(user.getState()==UserState.quitted){
            throw new ResourceNotFoundException("탈퇴된 회원입니다.");
        }
            //제목 내용이 비었을 때
        if (userUpdateRequest.getEmail() != null && !userUpdateRequest.getEmail().isBlank()) {
            user.setEmail(userUpdateRequest.getEmail());
        }
        if (userUpdateRequest.getNickname() != null && !userUpdateRequest.getNickname().isBlank()) {
            user.setNickname(userUpdateRequest.getNickname());
        }

        userRepository.save(user);
    }

    public void deleteUser(int id){
        Optional<User> userOptional = userRepository.findById(id);
        if(userOptional.isEmpty()){
            throw new ResourceNotFoundException("존재하지 않는 사용자 id");
        }
        User user = userOptional.get();
        user.setState(UserState.quitted);
        userRepository.save(user);

    }

    public List<BoardResponse> getMyBoard(int id){
        Optional<User> userOptional = userRepository.findById(id);
        if(userOptional.isEmpty()){
            throw new ResourceNotFoundException("존재하지 않는 사용자 id");
        }
        //이메일 닉네임 유효값 확인
        User user = userOptional.get();

        if(user.getState()==UserState.quitted){
            throw new ResourceNotFoundException("탈퇴된 회원입니다.");
        }

        List<Board> ListBoards=boardRepository.findByUserIdAndIsDeletedFalse(id);
        if(ListBoards.isEmpty()){
            throw new ResourceNotFoundException("작성한 게시글이 없습니다.");
        }
        return ListBoards.stream()
                .map(board ->
                        BoardResponse.from(board,isLikedByMe(id, board.getId())))
                .toList();

    }

    //중복메서드
    public boolean isLikedByMe(int userId, int boardId){
        boolean isExists = likeRepository.existsByUserIdAndBoardId(userId, boardId);
        return isExists;
    }

}
