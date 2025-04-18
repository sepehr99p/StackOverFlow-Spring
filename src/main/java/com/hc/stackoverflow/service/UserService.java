package com.hc.stackoverflow.service;

import com.hc.stackoverflow.entity.UserEntity;
import com.hc.stackoverflow.entity.dto.response.AuthResponseDto;
import com.hc.stackoverflow.entity.dto.param.RegisterRequestDto;
import com.hc.stackoverflow.repository.UserRepository;
import com.hc.stackoverflow.security.JwtUtil;
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
    private final JwtUtil jwtUtil;


    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public AuthResponseDto register(RegisterRequestDto request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setDisplayName(request.getUsername());
        user.setIsActive(true);
        user.setReputation(0);

        UserEntity savedUser = userRepository.save(user);

        String token = jwtUtil.generateToken(savedUser);

        return AuthResponseDto.builder()
                .token(token)
                .username(savedUser.getUsername())
                .build();
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
