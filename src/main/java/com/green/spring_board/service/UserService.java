package com.green.spring_board.service;

import com.green.spring_board.dto.*;
import com.green.spring_board.entity.Board;
import com.green.spring_board.entity.RefreshToken;
import com.green.spring_board.entity.User;
import com.green.spring_board.exceptions.ResourceConflictException;
import com.green.spring_board.exceptions.ResourceNotFoundException;
import com.green.spring_board.exceptions.UnauthenticatedException;
import com.green.spring_board.global.UserState;
import com.green.spring_board.repository.LikeRepository;
import com.green.spring_board.repository.RefreshTokenRepository;
import com.green.spring_board.repository.UserRepository;

import com.green.spring_board.repository.BoardRepository;
import com.green.spring_board.security.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final LikeRepository likeRepository;
    private final RefreshTokenRepository refreshTokenRepository;
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

    @Transactional
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
//        User user = userRepository.findByUsername(loginRequest.getUsername());
//        if (user==null){
//            throw new ResourceNotFoundException("존재하지 않는 이메일입니다.");
//        }
//        if(user.getState()==UserState.quitted) {
//            throw new ResourceNotFoundException("탈퇴된 회원입니다.");
//        }
        //엑세스 토큰 발급
        User user = userRepository.findByUsername(loginRequest.getUsername());
        String accessToken = tokenProvider.generateAccessToken(user.getUsername());
        String refreshToken = tokenProvider.generateRefreshToken(user.getUsername());
        //유저의 기존 리프레시 토큰 삭제
        refreshTokenRepository.deleteByUserId(user.getId());

        //트랜잭셔널 없이는 기본키로 제거해야 한다.
        //List<RefreshToken> tokens = refreshTokenRepository.findByUserId(user.getId());
        //tokens.forEach(token-> refreshTokenRepository.deleteByUserId(token.getId()));

        //신규 리프레시 토큰 저장
        RefreshToken refreshTokenEntity = new RefreshToken();

        Date expirationDate = tokenProvider.getExpiration(refreshToken);
        LocalDateTime expirationDateTime = expirationDate
                .toInstant()
                .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();


        refreshTokenEntity.setToken(refreshToken);
        refreshTokenEntity.setExpirationDatetime(expirationDateTime);
        refreshTokenEntity.setUser(userRepository.findByUsername(loginRequest.getUsername()));
        refreshTokenRepository.save(refreshTokenEntity);
        //로그인 성공
        return new LoginResponse(accessToken, refreshToken);

    }

    public UserUpdateRequest getUserInfo(){
        User user = getUserFromToken();

            return new UserUpdateRequest(
                user.getEmail(), user.getNickname());
    }

    public void updateInfo(
                           UserUpdateRequest userUpdateRequest){
        User user = getUserFromToken();

            //제목 내용이 비었을 때
        if (userUpdateRequest.getEmail() != null && !userUpdateRequest.getEmail().isBlank()) {
            user.setEmail(userUpdateRequest.getEmail());
        }
        if (userUpdateRequest.getNickname() != null && !userUpdateRequest.getNickname().isBlank()) {
            user.setNickname(userUpdateRequest.getNickname());
        }

        userRepository.save(user);
    }

    public RefreshResponse Refresh(RefreshRequest refreshRequest){
        String userRefreshToken = refreshRequest.getRefreshToken();
        //리프레시 토큰 위변조 여부 검증
        if(!tokenProvider.validateRefreshToken(userRefreshToken)){
            throw new UnauthenticatedException("만료되었거나 유효하지 않은 리프레시 토큰입니다.");
        }
        //db내 존재하는 리프레시 토큰인지 검증
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByToken(userRefreshToken);
        if(optionalRefreshToken.isEmpty()){
            throw new UnauthenticatedException("유효하지 않은 리프레시 토큰입니다.");
        }
        //해당 토큰이 만료되지 않았는지
        if(!optionalRefreshToken.get().getExpirationDatetime().isAfter(LocalDateTime.now())){
            throw new UnauthenticatedException("만료된 리프레시 토큰입니다.");
        }
        //토큰 내 유저네임의 유저 정보 유효한가 (유저가 없거나 활성화 상태 아닐 때)
        String username = tokenProvider.getUsernameFromToken(userRefreshToken);
        User user = userRepository.findByUsername(username);
        if(user == null | user.getState() !=UserState.active ){
            throw new UnauthenticatedException("인증할 수 없는 계정입니다.");
        }

        String accessToken = tokenProvider.generateAccessToken(username);
        return new RefreshResponse(accessToken);
    }

    public void deleteUser(){
        User user = getUserFromToken();
        user.setState(UserState.quitted);
        userRepository.save(user);

    }

    public List<BoardResponse> getMyBoard(){
        User user = getUserFromToken();

        List<Board> ListBoards=boardRepository.findByUserIdAndIsDeletedFalse(user.getId());
        if(ListBoards.isEmpty()){
            throw new ResourceNotFoundException("작성한 게시글이 없습니다.");
        }
        return ListBoards.stream()
                .map(board ->
                        BoardResponse.from(board,isLikedByMe(user.getId(), board.getId())))
                .toList();

    }

    //중복메서드
    public boolean isLikedByMe(int userId, int boardId){
        return likeRepository.existsByUserIdAndBoardId(userId, boardId);
    }


    public User getUserFromToken(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();


        return userRepository.findByUsername(username);
    }


}
