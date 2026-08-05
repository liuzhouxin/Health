package com.health.common.security;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class LoginUser implements Serializable {

    private Long userId;
    private String username;
    private String realName;
    private String phone;
    private String email;
    private String avatar;
    private List<String> roles;
    private List<String> permissions;
    private String token;

    public LoginUser() {}

    public LoginUser(Long userId, String username, String realName, List<String> roles, List<String> permissions) {
        this.userId = userId;
        this.username = username;
        this.realName = realName;
        this.roles = roles;
        this.permissions = permissions;
    }
}