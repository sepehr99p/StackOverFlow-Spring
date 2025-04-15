package com.hc.stackoverflow.service;

import com.hc.stackoverflow.entity.UserEntity;
import com.hc.stackoverflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import com.hc.stackoverflow.exception.ResourceNotFoundException;
import com.hc.stackoverflow.exception.UserAlreadyExistsException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public UserEntity createUser(UserEntity user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
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
    public UserEntity updateUser(Long id, UserEntity updatedUser) {
        UserEntity existingUser = getUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (!existingUser.getEmail().equals(updatedUser.getEmail()) &&
                userRepository.existsByEmail(updatedUser.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        existingUser.setDisplayName(updatedUser.getDisplayName());
        existingUser.setBio(updatedUser.getBio());
        existingUser.setProfilePictureUrl(updatedUser.getProfilePictureUrl());
        existingUser.setEmail(updatedUser.getEmail());

        return userRepository.save(existingUser);
    }

    @Transactional
    public void deactivateUser(Long id) {
        UserEntity user = getUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setIsActive(false);
        userRepository.save(user);
    }

    public void checkUserPermission(Long resourceUserId, Long currentUserId) {
        if (!resourceUserId.equals(currentUserId)) {
            throw new AccessDeniedException("You don't have permission to perform this action");
        }
    }
}
