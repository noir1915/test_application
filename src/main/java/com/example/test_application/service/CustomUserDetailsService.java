package com.example.test_application.service;

import com.example.test_application.model.User; // Ваш класс пользователя
import com.example.test_application.dao.UserRepository; // Репозиторий для доступа к данным пользователя
import com.example.test_application.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByName(username);
        if (user == null) {
            throw new UsernameNotFoundException("Пользователь не найден");
        }

        return new CustomUserDetails(user.getId(), user.getName(), user.getPassword());
    }
}
