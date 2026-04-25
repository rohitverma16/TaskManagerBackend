package com.rohit.taskmanager.jwt;

import com.rohit.taskmanager.service.CustomeUserDetailsService;
import com.rohit.taskmanager.service.TokenBlackListService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomeUserDetailsService customeUserDetailsService;
    private final TokenBlackListService blacklistService;

    public JwtFilter(JwtUtil jwtUtil, CustomeUserDetailsService customeUserDetailsService, TokenBlackListService blacklistService) {
        this.jwtUtil = jwtUtil;
        this.customeUserDetailsService = customeUserDetailsService;
        this.blacklistService = blacklistService;
    }

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Extract Authorization header
        String authToken = request.getHeader("Authorization");
        String token=null;
        String username = null;
        try {

            // Check if header contains Bearer token
            if (authToken != null && authToken.startsWith("Bearer ")) {
                token = authToken.substring(7);
                username = jwtUtil.extractUsername(token);
            }

            // Proceed only if: 1. Username is present 2. User is not already authenticated
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Validate token (signature, expiry)
                if (!jwtUtil.validateToken(token, username)) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
                    return;
                }

                // Check if token is blacklisted (logout case)
                if (blacklistService.isBlacklisted(token)) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Blacklisted Token");
                    return;
                }

                // Load user details from database
                UserDetails userDetails = customeUserDetailsService.loadUserByUsername(username);

                // Create authentication object with user details and authorities (roles)
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // Attach request-specific details (IP address, session ID)
                auth.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Set authentication in SecurityContext (marks user as authenticated)
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            return;
        }
        filterChain.doFilter(request,response);
    }
}
