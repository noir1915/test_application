package com.example.test_application.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
public class UserDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;
    private String password;
    private LocalDate dateOfBirth;
    private List<EmailDataDTO> emails;
    private List<PhoneDataDTO> phones;
}
