package ru.netology.cloudstorage.security.jwt;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.stereotype.Component;
import ru.netology.cloudstorage.dto.AuthenticationRequestDto;
//import ru.netology.cloudstorage.exceptions.JwtAuthenticationException;
import ru.netology.cloudstorage.exceptions.UserNotExistException;
import ru.netology.cloudstorage.model.Role;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Component
@Slf4j
public class JwtTokenProvider {

    private static final Map<String, String> jwtTokenProviderCache = new ConcurrentHashMap<>();

    public boolean checkJwtInCache(String token) {

        return jwtTokenProviderCache.containsKey(token);
    }

    @Value("${jwt.token.secret}")
    private String secret;

    @Value("${jwt.token.expired}0000")
    private long validityInMilliseconds;

    private UserDetailsService userDetailsService;

    @Autowired
    public void setUserDetailsService(UserDetailsService userDetailsService) {

        this.userDetailsService = userDetailsService;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @PostConstruct
    protected void init() {

        secret = Base64.getEncoder().encodeToString(secret.getBytes());
    }

    public String createToken(AuthenticationRequestDto authenticationRequestDto) {

        Claims claims = Jwts.claims().setSubject(authenticationRequestDto.getLogin());
        claims.put("roles", getRoleNames(authenticationRequestDto.getRoles()));
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);
        String jwt = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
        jwtTokenProviderCache.put(jwt, authenticationRequestDto.getLogin());
        log.info("User - " + authenticationRequestDto.getLogin() + " issued a token: " + jwt);
        return jwt;
    }

    public void deleteTokenFromCache(String token) {

        String bearerToken = jwtTokenProviderCache.remove(token);
        log.info("Token deleted: " + bearerToken);
    }

    public Authentication getAuthentication(String token) {

        UserDetails userDetails = this.userDetailsService.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token) {

        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }

    private List<String> getRoleNames(List<Role> userRoles) {

        List<String> result = new ArrayList<>();
        userRoles.forEach(role -> {
            result.add(role.getName());
        });
        return result;
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("auth-token");
        if (bearerToken != null) {
            String token = bearerToken.substring(7);
            if(validateToken(token) && jwtTokenProviderCache.containsKey(token)) {
                return token;
            }
        }
        log.warn("Token does not exist or was not found in the provider's cache");
        return null;
    }

    public boolean validateToken(String token) {

        boolean validState = false;
        try {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token);
            if (!claims.getBody().getExpiration().before(new Date())) {
                validState = true;
            }
        } catch (RuntimeException e) {
            log.error(e.getMessage());
        }
        return validState;
    }


}
