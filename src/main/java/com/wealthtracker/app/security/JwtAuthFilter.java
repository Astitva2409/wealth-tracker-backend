package com.wealthtracker.app.security;

import com.wealthtracker.app.entities.User;
import com.wealthtracker.app.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    // Same @Qualifier pattern as your Uber project
    // Routes all filter exceptions through GlobalExceptionHandler
    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String requestHeaderToken = request.getHeader("Authorization");

            // No token present — pass through to next filter
            // Public routes (/api/auth/**) don't need a token
            if (requestHeaderToken == null || !requestHeaderToken.startsWith("Bearer")) {
                filterChain.doFilter(request, response);
                return;
            }

            // Extract token — same split pattern as Uber project
            String token = requestHeaderToken.split("Bearer ")[1];
            Long userId = jwtService.getUserIdFromToken(token);

            if (userId != null) {
                User user = userService.getUserById(userId);

                // Set authenticated user in Spring Security context
                // null in the middle = no credentials needed (token already verified)
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                user, null, user.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

            filterChain.doFilter(request, response);

        } catch (Exception ex) {
            // Route ALL filter exceptions to GlobalExceptionHandler
            // so every error response has the same ApiResponse shape
            handlerExceptionResolver.resolveException(request, response, null, ex);
        }
    }
}