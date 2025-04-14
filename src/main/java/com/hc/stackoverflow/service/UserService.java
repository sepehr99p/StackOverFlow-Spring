package com.hc.stackoverflow.service;

import com.hc.stackoverflow.entity.UserEntity;
import com.hc.stackoverflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public UserEntity createUser(UserEntity user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setIsActive(true);
        user.setReputation(0);
        return userRepository.save(user);
    }

    @Cacheable(value = "users", key = "#id")
    public Optional<UserEntity> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Cacheable(value = "users", key = "#username")
    public Optional<UserEntity> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Cacheable(value = "users", key = "#email")
    public Optional<UserEntity> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Cacheable(value = "users", key = "'search:' + #keyword")
    public List<UserEntity> searchUsers(String keyword) {
        return userRepository.searchUsers(keyword);
    }

    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public UserEntity updateUser(Long id, UserEntity updatedUser) {
        UserEntity existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (updatedUser.getDisplayName() != null) {
            existingUser.setDisplayName(updatedUser.getDisplayName());
        }
        if (updatedUser.getBio() != null) {
            existingUser.setBio(updatedUser.getBio());
        }
        if (updatedUser.getProfilePictureUrl() != null) {
            existingUser.setProfilePictureUrl(updatedUser.getProfilePictureUrl());
        }
        if (updatedUser.getPassword() != null) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        return userRepository.save(existingUser);
    }

    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public void updateUserReputation(Long userId, int reputationChange) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setReputation(user.getReputation() + reputationChange);
        userRepository.save(user);
    }

    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public void deactivateUser(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setIsActive(false);
        userRepository.save(user);
    }
}
