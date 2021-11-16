package ru.netology.cloudstorage.dto;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import ru.netology.cloudstorage.model.Role;

import java.util.List;

@Data
public class AuthenticationRequestDto {
    private String login;
    private String password;
    private String jwt;
    private List<Role> roles;
}
