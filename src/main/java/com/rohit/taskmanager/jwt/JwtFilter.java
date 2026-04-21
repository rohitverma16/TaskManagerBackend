package com.rohit.taskmanager.jwt;

import com.rohit.taskmanager.service.CustomeUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomeUserDetailsService customeUserDetailsService;

    public JwtFilter(JwtUtil jwtUtil, CustomeUserDetailsService customeUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.customeUserDetailsService = customeUserDetailsService;
    }

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authToken = request.getHeader("Authorization");
        String token=null;
        String username = null;
        if (authToken != null && authToken.startsWith("Bearer ")) {
             token = authToken.substring(7);
             username= jwtUtil.extractUsername(token);
        }

        if(username!=null && !username.isEmpty() && SecurityContextHolder.getContext().getAuthentication()==null){
            UserDetails userDetails= customeUserDetailsService.loadUserByUsername(username);
            if(jwtUtil.validateToken(token,username)){
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        filterChain.doFilter(request,response);
    }
}
