package com.codewithmosh.arypto.services;

import com.codewithmosh.arypto.dtos.UserDto;
import com.codewithmosh.arypto.mappers.UserMapper;
import com.codewithmosh.arypto.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException(email)
        );
        return new User(
                user.getEmail(),
                user.getPassword(),
                Collections.emptyList()
        );
    }


    public UserDto fetchUserWithWallets(String userId) {
        var user = userRepository.getUserWithWallets(userId).orElse(null);

        if (user == null) {
            throw new UsernameNotFoundException(userId); // or throw an exception if preferred
        }
        return userMapper.toDto(user);
    }

}
