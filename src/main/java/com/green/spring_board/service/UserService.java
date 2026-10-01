package com.green.spring_board.service;

import com.green.spring_board.dto.SignUpRequest;
import com.green.spring_board.entity.User;
import com.green.spring_board.exceptions.ResourceConflictException;
import com.green.spring_board.exceptions.UserRequestException;
import com.green.spring_board.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
}
