package ru.netology.cloudstorage.exceptions;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserNotExistException extends UsernameNotFoundException {

    public UserNotExistException(String msg) {
        super(msg);
    }
}
