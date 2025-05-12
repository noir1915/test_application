package com.example.test_application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@Schema(description = "Сущность пользователя")
public class UserDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "Уникальный идентификатор пользователя")
    private Long id;
    @Schema(description = "ФИО", example = "Иванов Иван Иванович")
    private String name;
    @Schema(description = "Пароль пользователя", example = "***********")
    private String password;
    @Schema(description = "Дата рождения пользователя", example = "2023-01-01", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDate dateOfBirth;
    private List<EmailDataDTO> emails;
    private List<PhoneDataDTO> phones;
}
