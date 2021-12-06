package ru.netology.cloudstorage.dto;

import lombok.Data;

@Data
public class DownloadFileDto {
    private String hash;
    private String file;
}
