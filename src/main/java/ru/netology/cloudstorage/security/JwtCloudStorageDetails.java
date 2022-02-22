package ru.netology.cloudstorage.security;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.netology.cloudstorage.exceptions.UserNotExistException;
import ru.netology.cloudstorage.model.User;
import ru.netology.cloudstorage.security.jwt.JwtUser;
import ru.netology.cloudstorage.security.jwt.JwtUserFactory;
import ru.netology.cloudstorage.service.UserService;


@Service
@Log4j
public class JwtCloudStorageDetails implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public JwtCloudStorageDetails(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByUsername(username);

        if (user == null) {
            throw new UserNotExistException("User with username: " + username + " not found");
        }

        JwtUser jwtUser = JwtUserFactory.create(user);
        log.info("User with username:" + username + " successfully loaded");
        return jwtUser;
    }
}
