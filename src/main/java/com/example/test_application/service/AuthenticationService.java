package com.example.test_application.service;

import com.example.test_application.config.AuthRequest;
import com.example.test_application.exception.AuthenticationException;
import com.example.test_application.model.User; // Ваш класс пользователя
import com.example.test_application.dao.UserRepository; // Репозиторий для доступа к данным пользователя
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;

    public String authenticate(AuthRequest authRequest) {
        Optional<User> user = Optional.empty();

        if (authRequest.getEmail() != null) {
            user = userRepository.findUserByEmail(authRequest.getEmail());
            log.info("Пользователь найден по email: {}", authRequest.getEmail());
        } else if (authRequest.getPhone() != null) {
            user = userRepository.findUserByPhone(authRequest.getPhone());
            log.info("Пользователь найден по телефону: {}", authRequest.getPhone());
        }

        if (user.isEmpty()) {
            log.warn("Пользователь не найден");
            throw new AuthenticationException("Пользователь не найден");
        }

        if (!passwordEncoder.matches(authRequest.getPassword(), user.get().getPassword())) {
            log.warn("Пароль неверный для пользователя: {}", user);
            throw new AuthenticationException("Неверные учетные данные");
        }

        return jwtService.generateToken(user.get().getId());
    }
}
