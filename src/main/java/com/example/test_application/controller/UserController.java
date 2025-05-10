package com.example.test_application.controller;

import com.example.test_application.dto.UserDto;
import com.example.test_application.model.TransferRequest;
import com.example.test_application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

    // Эндпоинт для поиска пользователей с фильтрацией.
    @GetMapping("/search")
    public ResponseEntity<Page<UserDto>> searchUsers(
            @RequestParam(required = false) LocalDate dateOfBirth,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<UserDto> users = userService.searchUsers(dateOfBirth, phone, name, email, page, size);
        return ResponseEntity.ok(users);
    }
    // Эндпоинт для получения USer по id
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        UserDto user = userService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody UserDto userDto) {
        userDto.setId(id);
        userService.updateUser(userDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.deleteUser(id);
    }


    // Эндпоинт для обновления email.
    @PutMapping("/{id}/email")
    public ResponseEntity<Void> updateEmail(@PathVariable Long id,
                                            @RequestBody String newEmail) {
        userService.updateEmail(id, newEmail);
        return ResponseEntity.noContent().build();
    }

    // Эндпоинт для обновления телефона.
    @PutMapping("/{id}/phone")
    public ResponseEntity<Void> updatePhone(@PathVariable Long id,
                                            @RequestBody String newPhone) {
        userService.updatePhone(id, newPhone);
        return ResponseEntity.noContent().build();
    }
}
