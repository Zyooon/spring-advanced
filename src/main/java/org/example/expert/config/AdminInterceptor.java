package org.example.expert.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class AdminInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String roleStr = (String) request.getAttribute("userRole");
        UserRole userRole = UserRole.valueOf(roleStr);

        if (!UserRole.ADMIN.equals(userRole)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "관리자 권한이 없습니다.");
            return false;
        }

        String requestUri = request.getRequestURI();
        String method = request.getMethod();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        log.info("[{}] {} {}", timestamp, method, requestUri);

        return true;
    }
}
