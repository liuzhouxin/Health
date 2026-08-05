package com.health.user.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.common.exception.BusinessException;
import com.health.common.result.PageResult;
import com.health.common.result.Result;
import com.health.user.entity.HealthArchiveCategory;
import com.health.user.entity.SysUser;
import com.health.user.mapper.HealthArchiveCategoryMapper;
import com.health.user.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final SysUserMapper userMapper;
    private final HealthArchiveCategoryMapper categoryMapper;

    public Result<List<SysUser>> listUsers(String keyword) {
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like("username", keyword)
                    .or().like("real_name", keyword)
                    .or().like("phone", keyword));
        }
        wrapper.orderByDesc("create_time");
        List<SysUser> users = userMapper.selectList(wrapper);
        users.forEach(u -> u.setPassword(null));
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
        result.getRecords().forEach(u -> u.setPassword(null));
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
        userMapper.insert(user);
        user.setPassword(null);
        return Result.success("创建成功", user);
    }

    @Transactional
    public Result<SysUser> updateUser(SysUser user) {
        SysUser exist = userMapper.selectById(user.getId());
        if (exist == null) {
            throw new BusinessException("用户不存在");
        }
        userMapper.updateById(user);
        user.setPassword(null);
        return Result.success("更新成功", user);
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