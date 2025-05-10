package com.example.test_application.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {
    @Getter
    private Long id; // ID пользователя
    private final String username;
    private final String password;

    public CustomUserDetails(Long id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Логика проверки срока действия аккаунта
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Логика проверки блокировки аккаунта
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Логика проверки срока действия учетных данных
    }

    @Override
    public boolean isEnabled() {
        return true; // Логика проверки активности аккаунта
    }
}
