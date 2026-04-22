package com.apex.timekeeping.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Slf4j
@Component
public class RequestLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        long start = Instant.now().toEpochMilli();
        String method = req.getMethod();
        String uri = req.getRequestURI();
        String query = req.getQueryString();

        try {
            chain.doFilter(request, response);
        } finally {
            long duration = Instant.now().toEpochMilli() - start;
            int status = res.getStatus();
            log.info("{} {}{} -> {} ({}ms)",
                    method,
                    uri,
                    query != null ? "?" + query : "",
                    status,
                    duration);
        }
    }
}
