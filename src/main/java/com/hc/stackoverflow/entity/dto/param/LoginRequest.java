package com.hc.stackoverflow.entity.dto.param;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}