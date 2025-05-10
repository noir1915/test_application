package com.example.test_application.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UserDto {
    private Long id;
    private String name;
    private String password;
    private LocalDate dateOfBirth;
    private List<String> emails;
    private List<String> phones;
}
