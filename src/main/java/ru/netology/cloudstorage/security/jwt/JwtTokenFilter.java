package ru.netology.cloudstorage.security.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class JwtTokenFilter extends GenericFilterBean {

    private JwtTokenProvider jwtTokenProvider;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
            throws IOException, ServletException {
        String loginURI = "/login";
        HttpServletRequest servletRequest = (HttpServletRequest) req;
        HttpServletResponse servletResponse = (HttpServletResponse)res;
        String token = jwtTokenProvider.resolveToken(servletRequest);
        logger.warn(servletRequest.getRequestURI());
        if (token != null) {
            Authentication auth = jwtTokenProvider.getAuthentication(token);
            if (auth != null) {
                SecurityContextHolder.getContext().setAuthentication(auth);
                filterChain.doFilter(req, res);
            }
        } else if(servletRequest.getRequestURI().equals(loginURI)) {
            filterChain.doFilter(req, res);
        } else {
            sendTokenNotValidMsg(servletResponse);
        }
    }

    private void sendTokenNotValidMsg(HttpServletResponse servletResponse) throws IOException{

        Map<String, Object> abstractErrMessage = new HashMap<>();
        abstractErrMessage.put("\"message\"", "\"Token is not valid.\"");
        abstractErrMessage.put("\"id\"", 0);
        servletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        servletResponse.getWriter().write(abstractErrMessage.toString());
        servletResponse.getWriter().flush();
    }

}
