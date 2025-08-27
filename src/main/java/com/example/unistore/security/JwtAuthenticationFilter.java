package com.example.unistore.security;

import com.example.unistore.entity.User;
import com.example.unistore.entity.UserToken;
import com.example.unistore.repository.UserRepository;
import com.example.unistore.repository.UserTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTokenRepository userTokenRepository;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // Skip filter if no Bearer token is present
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        try {
            String username = jwtUtil.extractUsername(token);

            if (username == null) {
                sendErrorResponse(response, "Invalid token structure");
                return;
            }

            Optional<User> userOpt = userRepository.findByUsername(username);
            Optional<UserToken> userTokenOpt = userTokenRepository.findByTokenAndRevokeFalse(token);

            if (!userOpt.isPresent()) {
                sendErrorResponse(response, "Invalid User");
                return;
            }

            if (!userTokenOpt.isPresent()){
                sendErrorResponse(response, "UNAUTHORIZED");
                return;
            }

            User user = userOpt.get();
            UserToken userToken = userTokenOpt.get();

            // Validate token against database and expiration
            if (!token.equals(userToken.getToken()) || jwtUtil.isTokenExpired(token)) {
                sendErrorResponse(response, "Invalid or expired token");
                return;
            }

            // Create authentication object
            UserDetails userDetails = org.springframework.security.core.userdetails.User
                    .withUsername(username)
                    .password("") // Password shouldn't be needed after authentication
                    .authorities(new ArrayList<>()) // Add proper authorities if needed
                    .build();

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            sendErrorResponse(response, "Authentication failed: " + e.getMessage());
//			System.out.println(response +  "Authentication failed: " + e.getMessage());

            return;
        }

        filterChain.doFilter(request, response);

    }
    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(
                String.format("{\"status\":\"error\",\"message\":\"%s\"}", message));
    }



}
