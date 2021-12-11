package ru.netology.cloudstorage.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class FileRequestDto {

    private String rqUserToken;
    private String fileName;
    private int limit;
    private long fileSize;
    private byte[] fileBytes;
    private transient MultipartFile file;
}
