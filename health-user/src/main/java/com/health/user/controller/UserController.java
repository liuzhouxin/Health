package com.health.user.controller;

import com.health.common.result.PageResult;
import com.health.common.result.Result;
import com.health.user.entity.HealthArchiveCategory;
import com.health.user.entity.SysUser;
import com.health.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Api(tags = "用户管理")
public class UserController {

    private final UserService userService;

    @GetMapping("/list")
    @ApiOperation("用户列表")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public Result<List<SysUser>> list(@RequestParam(required = false) String keyword) {
        return userService.listUsers(keyword);
    }

    @GetMapping("/page")
    @ApiOperation("分页查询用户")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public Result<PageResult<SysUser>> page(
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        return userService.pageUsers(pageNo, pageSize, keyword);
    }

    @GetMapping("/{id}")
    @ApiOperation("用户详情")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','USER')")
    public Result<SysUser> getById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    @ApiOperation("创建用户")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<SysUser> create(@RequestBody SysUser user) {
        return userService.createUser(user);
    }

    @PutMapping
    @ApiOperation("更新用户")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<SysUser> update(@RequestBody SysUser user) {
        return userService.updateUser(user);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除用户")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        return userService.deleteUser(id);
    }

    @GetMapping("/profile")
    @ApiOperation("获取当前登录用户资料")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','USER')")
    public Result<SysUser> getProfile() {
        return userService.getProfile();
    }

    @PutMapping("/profile")
    @ApiOperation("更新当前登录用户资料")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','USER')")
    public Result<SysUser> updateProfile(@RequestBody SysUser user) {
        return userService.updateProfile(user);
    }

    @PutMapping("/password")
    @ApiOperation("修改当前登录用户密码")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','USER')")
    public Result<Void> changePassword(@RequestBody Map<String, String> body) {
        return userService.changePassword(body.get("oldPassword"), body.get("newPassword"));
    }

    @GetMapping("/category/list")
    @ApiOperation("档案分类列表")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','USER')")
    public Result<List<HealthArchiveCategory>> listCategories() {
        return userService.listCategories();
    }

    @PostMapping("/category")
    @ApiOperation("创建档案分类")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<HealthArchiveCategory> createCategory(@RequestBody HealthArchiveCategory category) {
        return userService.createCategory(category);
    }

    @PutMapping("/category")
    @ApiOperation("更新档案分类")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<HealthArchiveCategory> updateCategory(@RequestBody HealthArchiveCategory category) {
        return userService.updateCategory(category);
    }

    @DeleteMapping("/category/{id}")
    @ApiOperation("删除档案分类")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        return userService.deleteCategory(id);
    }
}