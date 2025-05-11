package com.example.test_application.service;

import com.example.test_application.config.AuthRequest;
import com.example.test_application.dto.EmailDataDTO;
import com.example.test_application.dto.PhoneDataDTO;
import com.example.test_application.dto.UserDto;
import com.example.test_application.exception.ResourceNotFoundException;
import com.example.test_application.model.EmailData;
import com.example.test_application.model.PhoneData;
import com.example.test_application.model.User;
import com.example.test_application.dao.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public Page<UserDto> searchUsers(LocalDate dateOfBirth, String phone, String name, String email, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.searchUsers(dateOfBirth, phone, name, email, pageable);
        return userPage.map(this::mapToUserDto);
    }

    public String authenticate(AuthRequest authRequest) {
        log.info("Аутентификация пользователя с email: {} и номером телефона: {} и паролем: {} ",
                authRequest.getEmail(), authRequest.getPhone(), authRequest.getPassword());
        Optional<User> user = Optional.empty();
        if (authRequest.getPhone() != null) {
            user = userRepository.findUserByPhone(authRequest.getPhone());
        }
        if (user.isEmpty() && authRequest.getEmail() != null) {
            user = userRepository.findUserByEmail(authRequest.getEmail());
        }
        if (user.isEmpty() || !user.get().getPassword().equals(authRequest.getPassword())) {
            throw new RuntimeException("Неправильный email/номер пароль");
        }
        return jwtService.generateToken(user.get().getId());
    }


    @Cacheable(value = "users", key = "#id")
    public UserDto getUserById(Long id) {
        log.info("Пользователь с id: {}", id);
        return mapToUserDto(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с id: " + id + " не найден")));
    }


    public void createUser(UserDto userDto) {

        if (userDto.getName() == null) {
            throw new IllegalArgumentException("Имя пользователя не может быть пустым");
        }
        log.info("Создание нового пользователя: {}", userDto.getName());
        User user = new User();
        user.setName(userDto.getName());
        user.setPassword(userDto.getPassword());
        updateEmailList(user, userDto.getEmails());
        updatePhoneList(user, userDto.getPhones());

        if (userDto.getDateOfBirth() != null) {
            user.setDateOfBirth(userDto.getDateOfBirth());
        }

        User savedUser = userRepository.save(user);
        mapToUserDto(savedUser);
    }


    @CacheEvict(value = "users", key = "#id")
    public void updateUser(UserDto userDto) {
        log.info("Обновление пользователя с id: {}", userDto.getId());

        User user = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с id: " + userDto.getId() + " не найден"));
        user.setName(userDto.getName());
        updateEmailList(user, userDto.getEmails());
        updatePhoneList(user, userDto.getPhones());

        if (userDto.getDateOfBirth() != null) {
            user.setDateOfBirth(userDto.getDateOfBirth());
        }
        userRepository.save(user);
        mapToUserDto(user);
    }

    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(Long id) {
        log.info("Удаление пользователя с id : {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с id: " + id + " не найден"));
        userRepository.delete(user);
    }

    public void updateEmail(Long userId, String newEmail) {
        User user = getUserOrThrow(userId);

        if (isEmailInUse(user, newEmail)) {
            throw new IllegalArgumentException("Email уже зарегистрирован");
        }
        EmailData emailData = new EmailData();
        emailData.setEmail(newEmail);
        emailData.setUser(user);

        user.getEmailDataList().add(emailData);

        userRepository.save(user);
    }

    public void updatePhone(Long userId, String newPhone) {
        User user = getUserOrThrow(userId);
        if (isPhoneInUse(user, newPhone)) {
            throw new IllegalArgumentException("Номер уже зарегистрирован");
        }
        PhoneData phoneData = new PhoneData();
        phoneData.setPhone(newPhone);
        phoneData.setUser(user);
        user.getPhoneDataList().add(phoneData);
        userRepository.save(user);
    }

    private void updateEmailList(User user, List<EmailDataDTO> emailDtos) {
        if (emailDtos != null && !emailDtos.isEmpty()) {
            user.getEmailDataList().clear();
            for (EmailDataDTO emailDto : emailDtos) {
                EmailData emailData = new EmailData();
                emailData.setEmail(emailDto.getEmail());
                emailData.setUser(user);
                user.getEmailDataList().add(emailData);
            }
        }
    }

    private void updatePhoneList(User user, List<PhoneDataDTO> phoneDtos) {
        if (phoneDtos != null && !phoneDtos.isEmpty()) {
            user.getPhoneDataList().clear();
            for (PhoneDataDTO phoneDto : phoneDtos) {
                PhoneData phoneData = new PhoneData();
                phoneData.setPhone(phoneDto.getPhone());
                phoneData.setUser(user);
                user.getPhoneDataList().add(phoneData);
            }
        }
    }

    private boolean isEmailInUse(User user, String email) {
        return user.getEmailDataList().stream()
                .anyMatch(existingEmail -> existingEmail.getEmail().equals(email));
    }

    private boolean isPhoneInUse(User user, String phone) {
        return user.getPhoneDataList().stream()
                .anyMatch(existingPhone -> existingPhone.getPhone().equals(phone));
    }

    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с id: " + id + " не найден"));
    }

    private UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());

        userDto.setPassword(user.getPassword()); // Убедитесь, что это поле не null
        userDto.setDateOfBirth(user.getDateOfBirth()); // Убедитесь, что это поле не null

        List<EmailDataDTO> emailDtos = user.getEmailDataList().stream()
                .map(emailData -> {
                    EmailDataDTO emailDto = new EmailDataDTO();
                    emailDto.setId(emailData.getId()); // Если у вас есть ID
                    emailDto.setEmail(emailData.getEmail());
                    return emailDto;
                })
                .toList();
        userDto.setEmails(emailDtos);

        List<PhoneDataDTO> phoneDtos = user.getPhoneDataList().stream()
                .map(phoneData -> {
                    PhoneDataDTO phoneDto = new PhoneDataDTO();
                    phoneDto.setId(phoneData.getId()); // Если у вас есть ID
                    phoneDto.setPhone(phoneData.getPhone());
                    return phoneDto;
                })
                .toList();
        userDto.setPhones(phoneDtos);

        return userDto;
    }
}