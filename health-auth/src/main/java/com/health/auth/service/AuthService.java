package com.health.auth.service;

import com.health.auth.entity.SysUser;
import com.health.auth.mapper.SysUserMapper;
import com.health.common.constant.Constants;
import com.health.common.dto.LoginRequest;
import com.health.common.exception.BusinessException;
import com.health.common.security.JwtUtils;
import com.health.common.security.LoginUser;
import com.health.common.result.Result;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;

    public Result<Map<String, Object>> login(LoginRequest request) {
        SysUser user = userMapper.selectOne(
                new QueryWrapper<SysUser>().eq("username", request.getUsername())
        );

        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (user.getStatus() == 0) {
            throw new BusinessException("用户已被禁用");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("密码错误");
        }

        List<String> roles = userMapper.selectRolesByUserId(user.getId());
        List<String> permissions = userMapper.selectPermissionsByUserId(user.getId());

        String rolesStr = String.join(",", roles);
        String token = JwtUtils.generateToken(user.getId(), user.getUsername(), rolesStr);

        LoginUser loginUser = new LoginUser(user.getId(), user.getUsername(), user.getRealName(), roles, permissions);
        loginUser.setToken(token);

        redisTemplate.opsForValue().set(
                Constants.REDIS_KEY_TOKEN + user.getId(),
                loginUser,
                Constants.JWT_EXPIRE,
                TimeUnit.MILLISECONDS
        );

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", loginUser);

        log.info("用户登录成功: {}", user.getUsername());
        return Result.success("登录成功", result);
    }

    public Result<Void> logout(Long userId) {
        if (userId != null) {
            redisTemplate.delete(Constants.REDIS_KEY_TOKEN + userId);
        }
        return Result.success("登出成功", null);
    }

    public Result<LoginUser> getUserInfo(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        Object cached = redisTemplate.opsForValue().get(Constants.REDIS_KEY_TOKEN + userId);
        if (cached != null) {
            return Result.success((LoginUser) cached);
        }

        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        List<String> roles = userMapper.selectRolesByUserId(userId);
        List<String> permissions = userMapper.selectPermissionsByUserId(userId);
        LoginUser loginUser = new LoginUser(user.getId(), user.getUsername(), user.getRealName(), roles, permissions);
        loginUser.setAvatar(user.getAvatar());
        loginUser.setPhone(user.getPhone());
        loginUser.setEmail(user.getEmail());

        return Result.success(loginUser);
    }

    public Result<List<SysUser>> listUsers() {
        List<SysUser> users = userMapper.selectList(null);
        users.forEach(u -> u.setPassword(null));
        return Result.success(users);
    }
}