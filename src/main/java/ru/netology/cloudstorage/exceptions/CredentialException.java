package ru.netology.cloudstorage.exceptions;

import org.springframework.security.authentication.BadCredentialsException;

public class CredentialException extends BadCredentialsException {

    public CredentialException(String msg) {
        super(msg);
    }
}
