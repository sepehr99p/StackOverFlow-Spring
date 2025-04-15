package com.hc.stackoverflow.repository;

import com.hc.stackoverflow.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM UserEntity u WHERE " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.displayName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<UserEntity> searchUsers(@Param("keyword") String keyword);
}
