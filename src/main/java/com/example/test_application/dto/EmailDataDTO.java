package com.example.test_application.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class EmailDataDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;
    private String email;
}
