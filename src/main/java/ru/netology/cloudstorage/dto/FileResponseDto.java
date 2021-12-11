package ru.netology.cloudstorage.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class FileResponseDto {
    @SerializedName("filename")
    private String fileName;

    @SerializedName("size")
    private long size;
}
