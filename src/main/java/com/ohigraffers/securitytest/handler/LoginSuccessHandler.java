package com.ohigraffers.securitytest.handler;

import com.ohigraffers.securitytest.service.AuthService;
import com.ohigraffers.securitytest.util.TokenUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Map<String, Object> memberInfo = getMemberInfo(authentication);
        String accessToken = TokenUtils.createAccessToken(memberInfo);
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(accessToken);
    }

    private Map<String, Object> getMemberInfo(Authentication authentication) {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String role = userDetails.getAuthorities()
                .stream().map(auth -> auth.getAuthority().toString())
                .collect(Collectors.joining());

        return Map.of(
                "username", userDetails.getUsername(),
                "role", role
        );

    }

}
