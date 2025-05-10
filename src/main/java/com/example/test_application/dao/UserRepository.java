package com.example.test_application.dao;

import com.example.test_application.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long>, JpaSpecificationExecutor<User> {
    @Query("SELECT u FROM User u LEFT JOIN u.phoneDataList p LEFT JOIN u.emailDataList e WHERE (:dateOfBirth IS NULL OR u.dateOfBirth > :dateOfBirth) " +
            "AND (:phone IS NULL OR p.phone = :phone) " +
            "AND (:name IS NULL OR u.name LIKE CONCAT(:name, '%')) " +
            "AND (:email IS NULL OR e.email = :email)")
    Page<User> searchUsers(@Param("dateOfBirth") LocalDate dateOfBirth,
                           @Param("phone") String phone,
                           @Param("name") String name,
                           @Param("email") String email,
                           Pageable pageable);

    User findUserByName(String name);

    @Query("SELECT u FROM User u JOIN u.emailDataList e WHERE e.email = :email")
    Optional<User> findUserByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u JOIN u.phoneDataList p WHERE p.phone = :phone")
    Optional<User> findUserByPhone(@Param("phone") String phone);
}
