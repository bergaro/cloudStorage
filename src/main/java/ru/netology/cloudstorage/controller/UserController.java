package ru.netology.cloudstorage.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.cloudstorage.dto.AuthenticationRequestDto;
import ru.netology.cloudstorage.exceptions.CredentialException;
import ru.netology.cloudstorage.model.User;
import ru.netology.cloudstorage.security.jwt.JwtTokenProvider;
import ru.netology.cloudstorage.service.UserService;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin("http://localhost:8080")
@Log4j
public class UserController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private static final String tokenAlias = "auth-token";

    @Autowired
    public UserController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider,
                          UserService userService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping(value = "/login", produces = "application/json")
    public ResponseEntity<Map<String, String>> login(@RequestBody AuthenticationRequestDto requestDto) {
        User user;
        String token;
        String username;
        try {
            log.debug("Authorization request: " + requestDto);
            username = requestDto.getLogin();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, requestDto.getPassword()));
            user = userService.findByUsername(username);
            requestDto.setRoles(user.getRoles());
            token = jwtTokenProvider.createToken(requestDto);
            Map<String, String> response = new HashMap<>();
            response.put(tokenAlias, token);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            throw new CredentialException(ex.getMessage());
        }
    }
}
