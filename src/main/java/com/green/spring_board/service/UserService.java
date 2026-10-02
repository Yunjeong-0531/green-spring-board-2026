package com.green.spring_board.service;

import com.green.spring_board.dto.LoginRequest;
import com.green.spring_board.dto.MyInfoResponse;
import com.green.spring_board.dto.SignUpRequest;
import com.green.spring_board.entity.Board;
import com.green.spring_board.entity.User;
import com.green.spring_board.exceptions.ResourceConflictException;
import com.green.spring_board.exceptions.ResourceNotFoundException;
import com.green.spring_board.exceptions.UnauthenticatedException;
import com.green.spring_board.exceptions.UserRequestException;
import com.green.spring_board.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder=new BCryptPasswordEncoder();

    public void signUp(SignUpRequest signUpRequest){
        //유저네임과 비밀번호 공백이 아닌지 확인
        if(signUpRequest.getEmail().isBlank()
                ||signUpRequest.getPassword().isBlank()){
            throw  new UserRequestException("이메일과 비밀번호는 공백일 수 없습니다.");
        }
        //이메일이 사용중인지 확인
        if(userRepository.existsByEmail(signUpRequest.getEmail())){
            throw new ResourceConflictException("존재하는 이메일입니다.");
        }
        //비밀번호 해싱
        String hashedPassword = passwordEncoder.encode(signUpRequest.getPassword());
        //db save
        User user = new User();
        user.setNickname(signUpRequest.getNickname());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(hashedPassword);
        userRepository.save(user);
    }

    public int login(LoginRequest loginRequest){
        //사용자가 넘겨준 이메일 존재하는지 확인
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());

        if (userOptional.isEmpty()){
            throw new ResourceNotFoundException("존재하지 않는 이메일입니다.");
        }
        User user = userOptional.get();
        //존재한다면 비밀번호 동일한지 확인
        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            throw new UnauthenticatedException("잘못된 비밀번호입니다.");
        }
        //로그인 성공
        return user.getId();

    }

    public MyInfoResponse getUserInfo(int id){
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()){
            throw new ResourceNotFoundException("존재하지 않는 사용자id가 입력되었습니다.");
        }

        User user = userOptional.get();

        return new MyInfoResponse(
                user.getEmail(), user.getNickname());
    }

    public void updateInfo(int id,
                           MyInfoResponse myInfoResponse){
        Optional<User> userOptional = userRepository.findById(id);
        if(userOptional.isEmpty()){
            throw new ResourceNotFoundException("존재하지 않는 사용자 Id입니다.");
        }
        //이메일 닉네임 유효값 확인
        User user = userOptional.get();

        //제목 내용이 비었을 때
        if (myInfoResponse.getEmail() != null && !myInfoResponse.getEmail().isBlank()) {
            user.setEmail(myInfoResponse.getEmail());
        }
        if (myInfoResponse.getNickname() != null && !myInfoResponse.getNickname().isBlank()) {
            user.setNickname(myInfoResponse.getNickname());
        }

        userRepository.save(user);
    }

    public void deleteUser(int id){
        Optional<User> userOptional = userRepository.findById(id);
        if(userOptional.isEmpty()){
            throw new ResourceNotFoundException("존재하지 않는 사용자 id");
        }
        User user = userOptional.get();
        userRepository.delete(user);

    }
}
