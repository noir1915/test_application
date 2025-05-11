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

    private final Map<Long, BigDecimal> initialBalances = new ConcurrentHashMap<>();

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;


    @Scheduled(fixedRate = 30000)
    @Transactional
    public void increaseBalances() {
        log.info("Увеличение баланса...");
        Iterable<Account> accounts = accountRepository.findAll();
        for (Account account : accounts) {
            BigDecimal currentBalance = account.getBalance();
            log.info("Текущий баланс для идентификатора счета {}: {}", account.getId(), currentBalance);
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
            log.info("Новый баланс для аккаунта {}: {}", account.getId(), newBalance);
            accountRepository.save(account);
        }
    }

    @Transactional
    public void transferMoney(Long fromUserId, Long toUserId, BigDecimal amount) {
        log.info("Перевод средств в размере: {} от пользователя: {} пользователю: {}", amount, fromUserId, toUserId);

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransferException("Сумма перевода должна быть больше нуля");
        }

        if (fromUserId.equals(toUserId)) {
            throw new TransferException("Нельзя перевести деньги на тот же счет");
        }

        User fromUser = getUserOrThrow(fromUserId);
        User toUser = getUserOrThrow(toUserId);

        if (fromUser.getAccount().getBalance().compareTo(amount) < 0) {
            throw new TransferException("Недостаточно средств");
        }

        try {
            fromUser.getAccount().setBalance(fromUser.getAccount().getBalance().subtract(amount));
            toUser.getAccount().setBalance(toUser.getAccount().getBalance().add(amount));

            accountRepository.save(fromUser.getAccount());
            accountRepository.save(toUser.getAccount());

            log.info("Транзакция успешно завершена");
        } catch (Exception e) {
            log.error("Ошибка: {}", e.getMessage());
            throw new TransferException("Передача не удалась из-за внутренней ошибки");
        }
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с идентификатором: " + userId +  " не найден" ));
    }
}