package com.example.test_application.service;

import com.example.test_application.dao.AccountRepository;
import com.example.test_application.dao.UserRepository;
import com.example.test_application.exception.ResourceNotFoundException;
import com.example.test_application.exception.TransferException;
import com.example.test_application.model.Account;
import com.example.test_application.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    private User fromUser;
    private User toUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        fromUser = new User();
        fromUser.setAccount(new Account());
        fromUser.getAccount().setBalance(BigDecimal.valueOf(100));

        toUser = new User();
        toUser.setAccount(new Account());
        toUser.getAccount().setBalance(BigDecimal.valueOf(50));
    }

    @Test
    void transferMoney_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(fromUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(toUser));

        accountService.transferMoney(1L, 2L, BigDecimal.valueOf(30));

        assertEquals(BigDecimal.valueOf(70), fromUser.getAccount().getBalance());
        assertEquals(BigDecimal.valueOf(80), toUser.getAccount().getBalance());

        verify(accountRepository).save(fromUser.getAccount());
        verify(accountRepository).save(toUser.getAccount());
    }

    @Test
    void transferMoney_InsufficientFunds() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(fromUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(toUser));

        TransferException exception = assertThrows(TransferException.class, () -> {
            accountService.transferMoney(1L, 2L, BigDecimal.valueOf(150));
        });

        assertEquals("Недостаточно средств", exception.getMessage());
    }

    @Test
    void transferMoney_SameAccount() {
        TransferException exception = assertThrows(TransferException.class, () -> {
            accountService.transferMoney(1L, 1L, BigDecimal.valueOf(30));
        });

        assertEquals("Нельзя перевести деньги на тот же счет", exception.getMessage());
    }

    @Test
    void transferMoney_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            accountService.transferMoney(1L, 2L, BigDecimal.valueOf(30));
        });

        assertEquals("Пользователь с идентификатором: 1 не найден", exception.getMessage());
    }
}
