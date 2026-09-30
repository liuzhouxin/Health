package com.health.user.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.common.context.UserContextHolder;
import com.health.common.exception.BusinessException;
import com.health.common.result.PageResult;
import com.health.common.result.Result;
import com.health.user.entity.HealthArchiveCategory;
import com.health.user.entity.SysUser;
import com.health.user.mapper.HealthArchiveCategoryMapper;
import com.health.user.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final SysUserMapper userMapper;
    private final HealthArchiveCategoryMapper categoryMapper;
    private final PasswordEncoder passwordEncoder;

    public Result<List<SysUser>> listUsers(String keyword) {
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like("username", keyword)
                    .or().like("real_name", keyword)
                    .or().like("phone", keyword));
        }
        wrapper.orderByDesc("create_time");
        List<SysUser> users = userMapper.selectList(wrapper);
        users.forEach(this::maskPasswordAndFillRoles);
        return Result.success(users);
    }

    public Result<PageResult<SysUser>> pageUsers(Integer pageNo, Integer pageSize, String keyword) {
        Page<SysUser> page = new Page<>(pageNo, pageSize);
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like("username", keyword)
                    .or().like("real_name", keyword)
                    .or().like("phone", keyword));
        }
        wrapper.orderByDesc("create_time");
        Page<SysUser> result = userMapper.selectPage(page, wrapper);
        result.getRecords().forEach(this::maskPasswordAndFillRoles);
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords(), pageNo, pageSize));
    }

    public Result<SysUser> getUserById(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    @Transactional
    public Result<SysUser> createUser(SysUser user) {
        SysUser exist = userMapper.selectOne(
                new QueryWrapper<SysUser>().eq("username", user.getUsername()));
        if (exist != null) {
            throw new BusinessException("用户名已存在");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new BusinessException("密码不能为空");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setId(null);
        userMapper.insert(user);
        assignRoles(user.getId(), user.getRoleCodes());
        user.setPassword(null);
        return Result.success("创建成功", user);
    }

    @Transactional
    public Result<SysUser> updateUser(SysUser user) {
        SysUser exist = userMapper.selectById(user.getId());
        if (exist == null) {
            throw new BusinessException("用户不存在");
        }
        // 密码为空表示不修改，避免明文覆盖
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(null);
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userMapper.updateById(user);
        if (user.getRoleCodes() != null && !user.getRoleCodes().isEmpty()) {
            userMapper.deleteUserRoles(user.getId());
            assignRoles(user.getId(), user.getRoleCodes());
        }
        user.setPassword(null);
        return Result.success("更新成功", user);
    }

    private void assignRoles(Long userId, List<String> roleCodes) {
        List<String> codes = (roleCodes == null || roleCodes.isEmpty())
                ? java.util.Collections.singletonList("ROLE_USER") : roleCodes;
        for (String code : codes) {
            userMapper.insertUserRoleByCode(userId, code);
        }
    }

    private void maskPasswordAndFillRoles(SysUser u) {
        u.setPassword(null);
        u.setRoleCodes(userMapper.selectRolesByUserId(u.getId()));
    }

    /** 获取当前登录用户的个人资料 */
    public Result<SysUser> getProfile() {
        Long userId = UserContextHolder.getUserId();
        if (userId == null) {
            throw new BusinessException("未获取到登录信息");
        }
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        maskPasswordAndFillRoles(user);
        return Result.success(user);
    }

    /** 更新当前登录用户的个人资料(仅允许改部分字段) */
    @Transactional
    public Result<SysUser> updateProfile(SysUser form) {
        Long userId = UserContextHolder.getUserId();
        if (userId == null) {
            throw new BusinessException("未获取到登录信息");
        }
        SysUser exist = userMapper.selectById(userId);
        if (exist == null) {
            throw new BusinessException("用户不存在");
        }
        // 仅更新允许修改的字段,其余字段(用户名/密码/状态/角色)一律忽略
        exist.setRealName(form.getRealName());
        exist.setPhone(form.getPhone());
        exist.setEmail(form.getEmail());
        exist.setAvatar(form.getAvatar());
        if (form.getGender() != null) exist.setGender(form.getGender());
        if (form.getAge() != null) exist.setAge(form.getAge());
        if (form.getHeight() != null) exist.setHeight(form.getHeight());
        if (form.getWeight() != null) exist.setWeight(form.getWeight());
        userMapper.updateById(exist);
        maskPasswordAndFillRoles(exist);
        return Result.success("更新成功", exist);
    }

    /** 修改当前登录用户的密码 */
    @Transactional
    public Result<Void> changePassword(String oldPassword, String newPassword) {
        Long userId = UserContextHolder.getUserId();
        if (userId == null) {
            throw new BusinessException("未获取到登录信息");
        }
        if (oldPassword == null || oldPassword.isEmpty()
                || newPassword == null || newPassword.isEmpty()) {
            throw new BusinessException("原密码和新密码不能为空");
        }
        if (newPassword.length() < 6 || newPassword.length() > 20) {
            throw new BusinessException("新密码长度需在6-20位之间");
        }
        SysUser exist = userMapper.selectById(userId);
        if (exist == null) {
            throw new BusinessException("用户不存在");
        }
        if (!passwordEncoder.matches(oldPassword, exist.getPassword())) {
            throw new BusinessException("原密码不正确");
        }
        exist.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(exist);
        return Result.success("密码修改成功", null);
    }

    @Transactional
    public Result<Void> deleteUser(Long id) {
        SysUser exist = userMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException("用户不存在");
        }
        userMapper.deleteById(id);
        return Result.success("删除成功", null);
    }

    public Result<List<HealthArchiveCategory>> listCategories() {
        List<HealthArchiveCategory> list = categoryMapper.selectList(
                new QueryWrapper<HealthArchiveCategory>().orderByAsc("sort_order"));
        return Result.success(list);
    }

    @Transactional
    public Result<HealthArchiveCategory> createCategory(HealthArchiveCategory category) {
        categoryMapper.insert(category);
        return Result.success("创建成功", category);
    }

    @Transactional
    public Result<HealthArchiveCategory> updateCategory(HealthArchiveCategory category) {
        categoryMapper.updateById(category);
        return Result.success("更新成功", category);
    }

    @Transactional
    public Result<Void> deleteCategory(Long id) {
        categoryMapper.deleteById(id);
        return Result.success("删除成功", null);
    }
}