package com.green.spring_board.service;

import com.green.spring_board.dto.BoardResponse;
import com.green.spring_board.dto.LoginRequest;
import com.green.spring_board.dto.UserUpdateRequest;
import com.green.spring_board.dto.SignUpRequest;
import com.green.spring_board.entity.Board;
import com.green.spring_board.entity.User;
import com.green.spring_board.exceptions.AuthorizationFailureException;
import com.green.spring_board.exceptions.ResourceConflictException;
import com.green.spring_board.exceptions.ResourceNotFoundException;
import com.green.spring_board.exceptions.UnauthenticatedException;
import com.green.spring_board.repository.LikeRepository;
import com.green.spring_board.repository.UserRepository;

import com.green.spring_board.repository.BoardRepository;
import lombok.AllArgsConstructor;
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
    private final PasswordEncoder passwordEncoder=new BCryptPasswordEncoder();

    public void signUp(SignUpRequest signUpRequest){
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

    public UserUpdateRequest getUserInfo(int id){
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()){
            throw new ResourceNotFoundException("존재하지 않는 사용자id가 입력되었습니다.");
        }

        User user = userOptional.get();
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
        userRepository.delete(user);

    }

    public List<BoardResponse> getMyBoard(int id){
        Optional<User> userOptional = userRepository.findById(id);
        if(userOptional.isEmpty()){
            throw new ResourceNotFoundException("존재하지 않는 사용자 id");
        }
        User user = userOptional.get();
        List<Board> ListBoards=boardRepository.findByUser(user);
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
