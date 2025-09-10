package com.example.demo.service;

import com.example.demo.database.User;
import com.example.demo.database.UserRepository;
import com.example.demo.dto.UserDto;
import com.example.demo.exceptions.LoginFailedException;
import com.example.demo.exceptions.base.HttpBadRequestException;
import com.example.demo.mapper.UserMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidator passwordValidator;
    private final UserMapper userMapper;

    @Transactional
    public String login(String username, String password) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw new LoginFailedException();
        }

        try {
            var authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            var user = (UserDetails) authentication.getPrincipal();
            return jwtService.generateToken(user);
        } catch (AuthenticationException e) {
            throw new LoginFailedException(e);
        }
    }

    @Transactional
    public void register(@Valid UserDto user) {
        if (!isValidPasswordPolicy(user.getPassword())) {
            throw new HttpBadRequestException("Password does not meet the policy requirements.");
        }

        if (userRepository.existsByUsername(user.getUsername()) || userRepository.existsByEmail(user.getEmail())) {
            throw new HttpBadRequestException("registration failed");
        }

        User users = userMapper.toNewEntity(user, passwordEncoder);
        userRepository.save(users);
    }

    public User getUser(UUID id) {
        return userRepository.findById(id).orElseThrow();
    }

    private boolean isValidPasswordPolicy(String password) {
        PasswordData passwordData = new PasswordData(password);
        RuleResult result = passwordValidator.validate(passwordData);
        return result.isValid();
    }
}
