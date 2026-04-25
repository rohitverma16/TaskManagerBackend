package com.rohit.taskmanager.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    // Thread-safe map: stores bucket per IP
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();


    // Create bucket configuration
    private Bucket createBucket(String apiType){

        // Login API
        if ("LOGIN".equals(apiType)) {
            return Bucket.builder()
                    .addLimit(Bandwidth.builder()
                            .capacity(5)
                            .refillGreedy(5, Duration.ofMinutes(1))
                            .build())
                    .build();
        }
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(20) // Maximum 20 requests allowed at a time (bucket size)
                        .refillGreedy(20, Duration.ofMinutes(1))  // Gradual refill: 20 tokens per minute (~1 token every 3 seconds)
                        .build())
                .build();
    }

    // Returns existing bucket for a client OR creates a new one if not present
    // computeIfAbsent ensures thread-safe creation in concurrent environment
    private Bucket resolveBucket(String key,String apiType){
       return cache.computeIfAbsent(key, k -> createBucket(apiType));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Identify client using IP address
        String ip = request.getRemoteAddr();
        String path=request.getRequestURI();

        String apiType=path.contains("/api/auth/login")?"LOGIN":"DEFAULT";

        String key=ip+":"+apiType;

        // Get bucket corresponding to this client
        Bucket bucket = resolveBucket(key,apiType);

        // Try to consume 1 token for the incoming request
        // If token available -> request allowed
        // If no token -> request rejected
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {

            // Add response header to show how many requests are remaining
            // Useful for monitoring/debugging
            response.addHeader("X-Rate-Limit-Remaining",
                    String.valueOf(probe.getRemainingTokens()));

            // Continue filter chain (pass request to next filter/controller)
            filterChain.doFilter(request, response);

        } else {

            // Calculate how long client should wait before next request is allowed
            long waitTime = probe.getNanosToWaitForRefill() / 1_000_000_000;

            // Set HTTP status code 429 (Too Many Requests)
            response.setStatus(429);
            response.setContentType("application/json");

            // Return error response with retry time
            response.getWriter().write(
                    "{\"error\": \"Too many requests\", \"retryAfter\": " + waitTime + "}"
            );
        }

    }
}
