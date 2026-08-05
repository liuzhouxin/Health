package com.health.common.security;

import com.health.common.constant.Constants;
import com.health.common.context.UserContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String userIdHeader = request.getHeader(Constants.USER_ID_HEADER);
        String usernameHeader = request.getHeader(Constants.USERNAME_HEADER);
        String rolesHeader = request.getHeader(Constants.ROLES_HEADER);

        if (userIdHeader != null && usernameHeader != null) {
            Long userId = Long.valueOf(userIdHeader);
            LoginUser loginUser = new LoginUser();
            loginUser.setUserId(userId);
            loginUser.setUsername(usernameHeader);
            if (rolesHeader != null) {
                loginUser.setRoles(Arrays.asList(rolesHeader.split(",")));
            }

            UserContextHolder.setUser(loginUser);

            if (rolesHeader != null && !rolesHeader.isEmpty()) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        loginUser, null,
                        Arrays.stream(rolesHeader.split(","))
                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.trim()))
                                .collect(Collectors.toList())
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            UserContextHolder.clear();
        }
    }
}