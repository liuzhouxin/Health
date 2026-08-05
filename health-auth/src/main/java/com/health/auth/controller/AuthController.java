package com.health.auth.controller;

import com.health.auth.service.AuthService;
import com.health.common.context.UserContextHolder;
import com.health.common.dto.LoginRequest;
import com.health.common.result.Result;
import com.health.common.security.LoginUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Api(tags = "认证管理")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @ApiOperation("用户登录")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/logout")
    @ApiOperation("用户登出")
    public Result<Void> logout() {
        return authService.logout(UserContextHolder.getUserId());
    }

    @GetMapping("/userInfo")
    @ApiOperation("获取当前用户信息")
    public Result<LoginUser> getUserInfo() {
        return authService.getUserInfo(UserContextHolder.getUserId());
    }

    @GetMapping("/list")
    @ApiOperation("用户列表")
    public Result<List<com.health.auth.entity.SysUser>> listUsers() {
        return authService.listUsers();
    }
}