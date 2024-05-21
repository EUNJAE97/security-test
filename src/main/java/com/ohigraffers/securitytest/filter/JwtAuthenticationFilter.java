package com.ohigraffers.securitytest.filter;

import com.ohigraffers.securitytest.service.AuthService;
import com.ohigraffers.securitytest.util.TokenUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthService authService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String refreshToken = TokenUtils.getToken(request.getHeader("Refresh-Token"));
        if (refreshToken != null && TokenUtils.isValidToken(refreshToken)) {
            String username = TokenUtils.getUsername(refreshToken);

            UserDetails userDetails = authService.loadUserByUsername(username);
            Map<String, Object> memberInfo = new HashMap<>();
            memberInfo.put("username", userDetails.getUsername());
            memberInfo.put("password",userDetails.getPassword());

            String newAccessToken = TokenUtils.createAccessToken(memberInfo);
            response.setHeader("Access-Token", newAccessToken);
            response.setHeader("Refresh-Token", refreshToken);
            authService.saveAuthentication(username);


        }
        String accessToken = TokenUtils.getToken(request.getHeader("Access-Token"));
        if(accessToken != null && TokenUtils.isValidToken(accessToken)) {
            String username = TokenUtils.getUsername(accessToken);
            authService.saveAuthentication(username);
        }

        filterChain.doFilter(request, response);

    }
}
