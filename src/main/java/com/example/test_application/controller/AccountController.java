package com.example.test_application.controller;

import com.example.test_application.model.Account;
import com.example.test_application.model.TransferRequest;
import com.example.test_application.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/transfer")
    public ResponseEntity<String> transferMoney(
            @RequestBody TransferRequest transferRequest) {

        Long fromUserId = transferRequest.getFromUserId();
        accountService.transferMoney(fromUserId, transferRequest.getToUserId(), transferRequest.getAmount());
        return ResponseEntity.ok("Транзакция успешно выполнена");
    }

}
