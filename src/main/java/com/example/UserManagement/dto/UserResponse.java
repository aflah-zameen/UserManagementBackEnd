package com.example.UserManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class UserResponse {
    private String username;
    private String email;
    private String profileImageURL;
}
