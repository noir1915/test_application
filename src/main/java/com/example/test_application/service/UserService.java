package com.example.test_application.service;

import com.example.test_application.dto.UserDto;
import com.example.test_application.exception.ResourceNotFoundException;
import com.example.test_application.model.EmailData;
import com.example.test_application.model.PhoneData;
import com.example.test_application.model.User;
import com.example.test_application.dao.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;

    public Page<UserDto> searchUsers(LocalDate dateOfBirth, String phone, String name, String email, int page, int size) {
        // Создаем объект Pageable для управления пагинацией
        Pageable pageable = PageRequest.of(page, size);

        // Выполняем запрос к репозиторию с фильтрацией
        Page<User> userPage = userRepository.searchUsers(dateOfBirth, phone, name, email, pageable);

        // Преобразуем Page<User> в Page<UserDto>
        return userPage.map(this::mapToUserDto);
    }

    @Cacheable(value = "users", key = "#id")
    public UserDto getUserById(Long id) {
        log.info("Get user by id: " + id);
        return mapToUserDto(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id)));
    }

    @CachePut(value = "users", key = "#userDto.id")
    public UserDto updateUser(UserDto userDto) {
        log.info("Updating user with id: " + userDto.getId());

        User user = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userDto.getId()));

        // Обновление полей пользователя
        user.setName(userDto.getName());

        updateEmailList(user, userDto.getEmails());
        updatePhoneList(user, userDto.getPhones());

        if (userDto.getDateOfBirth() != null) {
            user.setDateOfBirth(userDto.getDateOfBirth());
        }

        // Сохраняем изменения в базе данных
        userRepository.save(user);

        return mapToUserDto(user);
    }

    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(Long id) {
        log.info("Deleting user with id: " + id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

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

    private void updateEmailList(User user, List<String> emails) {
        if (emails != null && !emails.isEmpty()) {
            // Удаляем старые email
            user.getEmailDataList().clear();
            // Добавляем новые email
            for (String email : emails) {
                EmailData emailData = new EmailData();
                emailData.setEmail(email);
                emailData.setUser(user);
                user.getEmailDataList().add(emailData);
            }
        }
    }

    private void updatePhoneList(User user, List<String> phones) {
        if (phones != null && !phones.isEmpty()) {
            // Удаляем старые телефоны
            user.getPhoneDataList().clear();
            // Добавляем новые телефоны
            for (String phone : phones) {
                PhoneData phoneData = new PhoneData();
                phoneData.setPhone(phone);
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
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());

        List<String> emails = user.getEmailDataList().stream()
                .map(EmailData::getEmail)
                .toList();
        userDto.setEmails(emails);

        List<String> phones = user.getPhoneDataList().stream()
                .map(PhoneData::getPhone)
                .toList();
        userDto.setPhones(phones);
        return userDto;
    }
}