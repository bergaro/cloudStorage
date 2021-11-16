package ru.netology.cloudstorage.dto;

import lombok.Data;

@Data
public class FileResponseDto {
    private String filename;
    private int size;
}
