package com.example.test_application.controller;

import com.example.test_application.dto.UserDto;
import com.example.test_application.model.User;
import com.example.test_application.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "User controller", description = "Operation with user for Admin")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/search")
    @Operation(summary = "Список пользователей")
    public ResponseEntity<Page<UserDto>> searchUsers(
            @RequestParam(required = false) LocalDate dateOfBirth,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Page<UserDto> users = userService.searchUsers(dateOfBirth, phone, name, email, page, size);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Ошибка поиска пользователей: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @GetMapping("/{id}")
    @Operation(summary = "Получить пользователя по имени")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        UserDto user = userService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    @Operation(summary = "Создать нового пользователя")
    public ResponseEntity<User> createUser(@RequestBody UserDto userDto) {
        userService.createUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить пользователя по id")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody UserDto userDto) {
        userDto.setId(id);
        userService.updateUser(userDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить пользователя по id")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/email")
    public ResponseEntity<Void> updateEmail(@PathVariable Long id,
                                            @RequestBody String newEmail) {
        userService.updateEmail(id, newEmail);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/phone")
    public ResponseEntity<Void> updatePhone(@PathVariable Long id,
                                            @RequestBody String newPhone) {
        userService.updatePhone(id, newPhone);
        return ResponseEntity.noContent().build();
    }
}