package ru.netology.cloudstorage.dto;

import lombok.Data;

@Data
public class RenameFileDto {
    private String name;
    private String rqUserToken;
    private String originFileName;
}
