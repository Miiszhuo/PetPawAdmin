package com.petpaw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.petpaw.common.exception.BusinessException;
import com.petpaw.common.model.PageRequest;
import com.petpaw.entity.SysUser;
import com.petpaw.entity.WjFile;
import com.petpaw.mapper.SysUserMapper;
import com.petpaw.service.SysUserService;
import com.petpaw.service.WjFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final WjFileService wjFileService;

    /**
     * BCrypt密码加密
     */
    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public SysUser findByUsername(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username)
               .eq(SysUser::getDeleted, 0);
        return this.getOne(wrapper);
    }

    @Override
    public IPage<SysUser> getUserPage(PageRequest pageRequest, String username, String realName, Integer status) {
        Page<SysUser> page = new Page<>(pageRequest.getCurrent(), pageRequest.getSize());
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(username), SysUser::getUsername, username)
               .like(StringUtils.hasText(realName), SysUser::getRealName, realName)
               .eq(status != null, SysUser::getStatus, status)
               .eq(SysUser::getDeleted, 0)
               .orderByDesc(SysUser::getCreateTime);
        
        IPage<SysUser> result = baseMapper.selectPage(page, wrapper);

        // 填充图片信息
        result.getRecords().forEach(user -> {
            List<WjFile> files = wjFileService.getFiles(user.getId(), "USER");
            if (!files.isEmpty()) {
                user.setImageUrl(files.get(0).getFilePath());
            }
        });
        
        return result;
    }

    @Override
    @Transactional
    public boolean createUser(SysUser user) {
        // 检查用户名是否已存在
        if (findByUsername(user.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }

        // 加密密码
        user.setPassword(encodePassword(user.getPassword()));

        boolean success = this.save(user);

        if (success && StringUtils.hasText(user.getImageUrl())) {
            wjFileService.bindFile(user.getImageUrl(), user.getId(), "USER");
        }
        return success;
    }

    @Override
    @Transactional
    public boolean updateUser(SysUser user) {
        // 检查用户名是否已被其他用户使用
        SysUser existingUser = findByUsername(user.getUsername());
        if (existingUser != null && !existingUser.getId().equals(user.getId())) {
            throw new BusinessException("用户名已存在");
        }

        boolean success = this.updateById(user);

        if (success && StringUtils.hasText(user.getImageUrl())) {
            wjFileService.bindFile(user.getImageUrl(), user.getId(), "USER");
        }
        return success;
    }

    @Override
    @Transactional
    public boolean resetPassword(Long userId, String newPassword) {
        SysUser user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        user.setPassword(encodePassword(newPassword));
        return this.updateById(user);
    }

    @Override
    @Transactional
    public boolean changeUserStatus(Long userId, Integer status) {
        SysUser user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        user.setStatus(status);
        return this.updateById(user);
    }
}
