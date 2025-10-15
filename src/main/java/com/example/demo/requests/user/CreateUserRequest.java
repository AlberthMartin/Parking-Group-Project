package com.example.demo.requests.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateUserRequest {
    @NotBlank
    private String email;
    @NotBlank
    private String password;
}
