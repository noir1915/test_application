package com.example.test_application.service;

import com.example.test_application.dao.AccountRepository;
import com.example.test_application.dao.UserRepository;
import com.example.test_application.exception.ResourceNotFoundException;
import com.example.test_application.exception.TransferException;
import com.example.test_application.model.Account;
import com.example.test_application.model.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    // Хранение начальных значений балансов
    private final Map<Long, BigDecimal> initialBalances = new ConcurrentHashMap<>();

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public Account findById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
    }

    public Account createAccount(Account account) {
        account.setBalance(account.getBalance());
        return accountRepository.save(account);
    }

    @Transactional
    public Account updateBalance(Long id, BigDecimal amount) {
        Account account = findById(id);
        account.setBalance(account.getBalance().add(amount));
        return accountRepository.save(account);
    }

    @Scheduled(fixedRate = 30000)
    @Transactional
    public void increaseBalances() {
        log.info("Increasing balances...");
        List<Account> accounts = accountRepository.findAll();
        for (Account account : accounts) {
            BigDecimal currentBalance = account.getBalance();
            log.info("Current balance for account ID {}: {}", account.getId(), currentBalance);
            if (!initialBalances.containsKey(account.getId())) {
                initialBalances.put(account.getId(), currentBalance);
            }
            BigDecimal initialBalance = initialBalances.get(account.getId());
            BigDecimal newBalance = currentBalance.multiply(BigDecimal.valueOf(1.1));
            BigDecimal maxAllowedBalance = initialBalance.multiply(BigDecimal.valueOf(2.07));
            if (newBalance.compareTo(maxAllowedBalance) > 0) {
                newBalance = maxAllowedBalance;
            }
            account.setBalance(newBalance);
            log.info("New balance for account ID {}: {}", account.getId(), newBalance);
            accountRepository.save(account);
        }
    }

    @Transactional
    public void transferMoney(Long fromUserId, Long toUserId, BigDecimal amount) {
        log.info("Initiating transfer of {} from user {} to user {}", amount, fromUserId, toUserId);

        // Валидация входных данных
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransferException("Transfer amount must be greater than zero");
        }

        if (fromUserId.equals(toUserId)) {
            throw new TransferException("Cannot transfer money to the same account");
        }

        User fromUser = getUserOrThrow(fromUserId);
        User toUser = getUserOrThrow(toUserId);

        // Проверка наличия средств
        if (fromUser.getAccount().getBalance().compareTo(amount) < 0) {
            throw new TransferException("Insufficient funds");
        }

        // Обновление балансов
        try {
            fromUser.getAccount().setBalance(fromUser.getAccount().getBalance().subtract(amount));
            toUser.getAccount().setBalance(toUser.getAccount().getBalance().add(amount));

            accountRepository.save(fromUser.getAccount());
            accountRepository.save(toUser.getAccount());

            log.info("Transfer successful");
        } catch (Exception e) {
            log.error("Error during transfer: {}", e.getMessage());
            throw new TransferException("Transfer failed due to an internal error");
        }
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }
}