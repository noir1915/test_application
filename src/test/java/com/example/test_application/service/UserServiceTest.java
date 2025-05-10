package com.example.test_application.service;

import com.example.test_application.dao.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    void testSearchUsers() {
        // Напишите тест на поиск пользователей.
    }
}
