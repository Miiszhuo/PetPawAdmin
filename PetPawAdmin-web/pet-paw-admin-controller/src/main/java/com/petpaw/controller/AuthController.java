package com.petpaw.controller;

import com.petpaw.common.result.Result;
import com.petpaw.common.utils.AesUtils;
import com.petpaw.common.utils.JwtUtils;
import com.petpaw.entity.SysUser;
import com.petpaw.service.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器（不使用Spring Security）
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SysUserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Transactional(readOnly = true)
    public Result login(@RequestBody LoginRequest loginRequest) {
        try {
            // 参数校验
            if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
                return Result.error("用户名不能为空");
            }
            if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
                return Result.error("密码不能为空");
            }

            // 根据用户名查询用户
            SysUser user = userService.lambdaQuery()
                    .eq(SysUser::getUsername, loginRequest.getUsername())
                    .eq(SysUser::getDeleted, 0)
                    .one();

            if (user == null) {
                return Result.error("用户名或密码错误");
            }

            // 验证密码
            String inputPassword = loginRequest.getPassword();
            String storedPassword = user.getPassword();

            // 使用AES解密存储的密码并与输入密码比较
            boolean passwordValid;
            try {
                String decryptedPassword = AesUtils.decrypt(storedPassword);
                passwordValid = inputPassword.equals(decryptedPassword);

                if (passwordValid) {
                    log.info("用户 {} 登录成功", user.getUsername());
                } else {
                    log.warn("用户 {} 密码验证失败", user.getUsername());
                }
            } catch (Exception e) {
                log.error("密码解密失败，用户: {}, 错误: {}", user.getUsername(), e.getMessage());
                return Result.error("密码验证失败，请联系管理员");
            }

            if (!passwordValid) {
                return Result.error("用户名或密码错误");
            }

            // 检查用户状态
            if (user.getStatus() != null && user.getStatus() == 0) {
                return Result.error("账号已被禁用");
            }

            // 生成token
            String token = JwtUtils.generateToken(user.getUsername(), user.getId());

            // 构建返回数据
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("realName", user.getRealName());
            userInfo.put("avatar", user.getAvatar());
            userInfo.put("status", user.getStatus());

            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("user", userInfo);

            log.info("用户 {} 登录成功", user.getUsername());
            return Result.success("登录成功", result);

        } catch (Exception e) {
            log.error("登录异常", e);
            return Result.error("登录失败，请稍后重试");
        }
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/user")
    @Transactional(readOnly = true)
    public Result getUserInfo(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return Result.error(401, "未授权");
            }

            String token = authHeader.substring(7);
            String username = JwtUtils.getUsernameFromToken(token);
            Long userId = JwtUtils.getUserIdFromToken(token);

            // 查询用户信息
            SysUser user = userService.getById(userId);
            if (user == null || !username.equals(user.getUsername())) {
                return Result.error(401, "用户信息已失效");
            }

            // 构建返回数据
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("realName", user.getRealName());
            userInfo.put("avatar", user.getAvatar());
            userInfo.put("status", user.getStatus());

            return Result.success(userInfo);

        } catch (Exception e) {
            log.error("获取用户信息异常", e);
            return Result.error(401, "获取用户信息失败");
        }
    }

    /**
     * 登出
     */
    @PostMapping("/logout")
    public Result logout() {
        // 由于使用JWT，客户端只需要删除token即可
        // 服务端可以选择将token加入黑名单，但这里为了简化不实现
        return Result.success("登出成功");
    }

    /**
     * 刷新token
     */
    @PostMapping("/refresh")
    public Result refreshToken(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return Result.error(401, "未授权");
            }

            String token = authHeader.substring(7);

            // 验证token是否过期
            if (JwtUtils.isTokenExpired(token)) {
                return Result.error(401, "Token已过期");
            }

            // 生成新token
            String newToken = JwtUtils.refreshToken(token);

            Map<String, Object> result = new HashMap<>();
            result.put("token", newToken);

            return Result.success("Token刷新成功", result);

        } catch (Exception e) {
            log.error("刷新token异常", e);
            return Result.error("刷新token失败");
        }
    }
}

/**
 * 登录请求类
 */
class LoginRequest {
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
