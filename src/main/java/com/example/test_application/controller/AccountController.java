package com.example.test_application.controller;

import com.example.test_application.model.Account;
import com.example.test_application.security.CustomUserDetails;
import com.example.test_application.service.AccountService;
import com.example.test_application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final UserService userService;


    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {
        Account account = accountService.findById(id);
        return ResponseEntity.ok(account);
    }

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        Account createdAccount = accountService.createAccount(account);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
    }

    @PutMapping("/{id}/balance")
    public ResponseEntity<Account> updateBalance(@PathVariable Long id, @RequestParam BigDecimal amount) {
        Account updatedAccount = accountService.updateBalance(id, amount);
        return ResponseEntity.ok(updatedAccount);
    }

    @PostMapping("/{id}/transfer")
    public ResponseEntity<String> transferMoney(
            @PathVariable Long id,
            @RequestParam Long toUserId,
            @RequestParam BigDecimal value,
            Authentication authentication) {

        Long fromUserId = getAuthenticatedUserId(authentication); // Получаем ID пользователя из токена

        accountService.transferMoney(fromUserId, toUserId, value); // Вызываем метод перевода

        return ResponseEntity.ok("Transfer successful");
    }
    private Long getAuthenticatedUserId(Authentication authentication) {
        // Предполагается, что ID пользователя хранится в Claims токена.
        return ((CustomUserDetails) authentication.getPrincipal()).getId();
    }
}
