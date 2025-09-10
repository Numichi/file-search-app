package com.example.demo.config.security;

import com.example.demo.database.User;
import com.example.demo.database.UserRepository;
import com.example.demo.values.RegExp;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser;

        if (RegExp.UUID.matcher(username).matches()) {
            optionalUser = userRepository.findById(UUID.fromString(username));
        } else if (RegExp.EMAIL.matcher(username).matches()) {
            optionalUser = userRepository.findByEmail(username);
        } else {
            optionalUser = userRepository.findByUsername(username);
        }

        var user = optionalUser.orElseThrow(() -> new UsernameNotFoundException("user not found"));
        var roles = Optional.ofNullable(user.getRoles()).map(it -> it.split(",")).orElse(new String[0]);

        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getId().toString())
            .password(user.getPassword())
            .roles(roles)
            .build();
    }
}

