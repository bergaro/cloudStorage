package ru.netology.cloudstorage.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.netology.cloudstorage.exceptions.UserNotExistException;
import ru.netology.cloudstorage.model.User;
import ru.netology.cloudstorage.security.jwt.JwtUser;
import ru.netology.cloudstorage.security.jwt.JwtUserFactory;
import ru.netology.cloudstorage.service.CloudStorageService;


@Service
@Slf4j
public class JwtCloudStorageDetails implements UserDetailsService {

    private final CloudStorageService cloudStorageService;

    @Autowired
    public JwtCloudStorageDetails(CloudStorageService cloudStorageService) {
        this.cloudStorageService = cloudStorageService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = cloudStorageService.findByUsername(username);

        if (user == null) {
            throw new UserNotExistException("User with username: " + username + " not found");
//            throw new UsernameNotFoundException("User with username: " + username + " not found");
        }

        JwtUser jwtUser = JwtUserFactory.create(user);
        log.info("IN loadUserByUsername - user with username: {} successfully loaded", username);
        return jwtUser;
    }
}