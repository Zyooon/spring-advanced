package org.example.expert.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.user.enums.UserRole;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter implements Filter {

    private final JwtUtil jwtUtil;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();

        //auth url 판단
        if (isAuthUri(requestURI)) {
            chain.doFilter(request, response);
            return;
        }

        //토큰 추출
        String token = extractToken(httpRequest, httpResponse);
        if (token == null) return;

        //Claims 생성 및 정합성 검사
        Claims claims = validateToken(token, httpResponse);
        if (claims == null) return;

        //Attribute 에 유저 정보 저장
        setUserAttributes(httpRequest, claims);

        //관리자 권한 여부 판단
        if (!authorizeAdminAccess(requestURI, httpRequest, httpResponse)) {
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }

    //auth uri 판단
    private boolean isAuthUri(String uri) {
        return uri.startsWith("/auth");
    }

    //헤더에서 토큰 추출 및 가공
    private String extractToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "JWT 토큰이 필요합니다.");
            return null;
        }
        return jwtUtil.substringToken(bearerToken);
    }

    //관리자 권한 판단
    private boolean authorizeAdminAccess(String requestURI, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userRole = (String) request.getAttribute("userRole");

        if (requestURI.startsWith("/admin") && !UserRole.ADMIN.name().equals(userRole)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "관리자 권한이 없습니다.");
            return false;
        }
        return true;
    }

    //JWT 유효성 검사 및 claims 추출
    private Claims validateToken(String token, HttpServletResponse response) throws IOException {
        try {
            return jwtUtil.extractClaims(token);
        } catch (SecurityException | MalformedJwtException e) {
            log.error("유효하지 않은 JWT 서명", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않는 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰", e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "지원되지 않는 JWT 토큰입니다.");
        } catch (Exception e) {
            log.error("JWT 검증 실패", e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "유효하지 않는 JWT 토큰입니다.");
        }
        return null;
    }

    private void setUserAttributes(HttpServletRequest request, Claims claims) {
        request.setAttribute("userId", Long.parseLong(claims.getSubject()));
        request.setAttribute("email", claims.get("email"));
        request.setAttribute("userRole", claims.get("userRole"));
    }
    
}
