package com.example.test_application.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

import java.math.BigDecimal;

@Data
public class TransferRequest {

    @NotNull(message = "From User ID cannot be null")
    private Long fromUserId;

    @NotNull(message = "To User ID cannot be null")
    private Long toUserId;

    @NotNull(message = "Amount cannot be null")
    private BigDecimal amount;
}
