package ru.netology.cloudstorage.service;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import ru.netology.cloudstorage.model.User;
import ru.netology.cloudstorage.repository.UserRepository;
import ru.netology.cloudstorage.security.jwt.JwtTokenProvider;

@Service
@Log4j
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;

    }

    public User findByUsername(String username) throws BadCredentialsException {
        User result = userRepository.findByUsername(username);
        log.debug("User found:" + result + ";");
        return result;
    }

    public long getUserId(String jwtToken) {
        String userName = jwtTokenProvider.getUsername(jwtToken);
        return Long.parseLong(userRepository.findUserIdByUsername(userName));
    }
}
