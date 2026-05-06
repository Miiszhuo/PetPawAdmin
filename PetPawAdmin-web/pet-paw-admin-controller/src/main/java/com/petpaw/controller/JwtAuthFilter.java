package com.petpaw.controller;

import com.petpaw.common.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT认证过滤器
 */
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // 跳过登录接口和其他不需要认证的接口
        if (path.startsWith("/api/auth/login") ||
            path.startsWith("/api/auth/logout") ||
            path.startsWith("/api/auth/refresh") ||
            path.startsWith("/api/auth/aes-test") ||
            path.startsWith("/swagger") ||
            path.startsWith("/v3/api-docs") ||
            path.startsWith("/webjars")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 获取Authorization头
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                // 验证token
                if (!JwtUtils.isTokenExpired(token)) {
                    String username = JwtUtils.getUsernameFromToken(token);
                    Long userId = JwtUtils.getUserIdFromToken(token);

                    // 将用户信息设置到请求属性中，供后续使用
                    request.setAttribute("username", username);
                    request.setAttribute("userId", userId);

                    log.debug("用户 {} 访问接口: {}", username, path);
                } else {
                    log.warn("Token已过期");
                    response.setStatus(401);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":401,\"message\":\"Token已过期\",\"timestamp\":" + System.currentTimeMillis() + "}");
                    return;
                }
            } catch (Exception e) {
                log.warn("Token验证失败: {}", e.getMessage());
                response.setStatus(401);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":401,\"message\":\"Token无效\",\"timestamp\":" + System.currentTimeMillis() + "}");
                return;
            }
        } else {
            // 需要认证但没有提供token
            if (!isPublicPath(path)) {
                log.warn("访问需要认证的接口但未提供token: {}", path);
                response.setStatus(401);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":401,\"message\":\"请先登录\",\"timestamp\":" + System.currentTimeMillis() + "}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 判断是否为公开路径（不需要认证）
     */
    private boolean isPublicPath(String path) {
        return path.startsWith("/api/auth/login") ||
               path.startsWith("/api/auth/logout") ||
               path.startsWith("/api/auth/refresh") ||
               path.startsWith("/swagger") ||
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/webjars");
    }
}
