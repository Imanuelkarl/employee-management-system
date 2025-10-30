//package ng.darum.gateway.components;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.List;
//
//@Component
//public class JwtFilter extends OncePerRequestFilter {
//
//    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
//    private static final String AUTH_COOKIE_NAME = "access_token";
//
//    // List of API endpoints that don't require authentication
//    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
//
//    );
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//
//        // Skip filter for whitelisted paths
//        if (shouldNotFilter(request)) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        try {
//            String token = extractTokenFromCookie(request);
//
//            if (token != null) {
//                String email = jwtUtil.extractEmail(token);
//                if (email != null ) {
//                    User userDetails = new User(
//                            email, "", List.of()); // Add roles if needed
//
//                    UsernamePasswordAuthenticationToken auth =
//                            new UsernamePasswordAuthenticationToken(
//                                    userDetails, null, userDetails.getAuthorities());
//                    SecurityContextHolder.getContext().setAuthentication(auth);
//                } else {
//                    logger.warn("Invalid JWT token found in cookie");
//                    sendErrorResponse(response, "Invalid token", HttpServletResponse.SC_UNAUTHORIZED);
//                    return;
//                }
//            } else {
//                logger.warn("No JWT token found in request");
//                sendErrorResponse(response, "Authentication required", HttpServletResponse.SC_UNAUTHORIZED);
//                return;
//            }
//        } catch (Exception e) {
//            logger.error("Error processing JWT token", e);
//            SecurityContextHolder.clearContext();
//            sendErrorResponse(response, "Authentication error", HttpServletResponse.SC_UNAUTHORIZED);
//            return;
//        }
//
//        filterChain.doFilter(request, response);
//    }
//
//    private String extractTokenFromCookie(HttpServletRequest request) {
//        Cookie[] cookies = request.getCookies();
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                if (AUTH_COOKIE_NAME.equals(cookie.getName())) {
//                    return cookie.getValue();
//                }
//            }
//        }
//        return null;
//    }
//
//    private void sendErrorResponse(HttpServletResponse response, String message, int status) throws IOException {
//        response.setStatus(status);
//        response.setContentType("application/json");
//        response.getWriter().write("{\"error\": \"" + message + "\"}");
//    }
//
//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) {
//        String path = request.getRequestURI();
//
//
//        // Allow OPTIONS requests for CORS preflight
//        if (path.startsWith("/auth/login")) {
//            return true;
//        }
//        if (path.startsWith("/swagger")) {
//            return true;
//        }
//
//        return false;
//    }
//}