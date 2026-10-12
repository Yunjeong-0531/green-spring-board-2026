package com.green.spring_board.security;

import com.green.spring_board.entity.User;
import com.green.spring_board.global.UserState;
import com.green.spring_board.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       //유저 네임을 전달받아 db내에 존재하는지 확인한다.
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username+" 계정은 존재하지 않는 계정입니다");
        }
        if(user.getState()!= UserState.active){
            throw new UsernameNotFoundException("사용할 수 없는 계정입니다.");
        }
        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getUsername())
                .password(user.getPassword())
 //               .authorities(user.getRole().toString())
                .build();
    }
}
