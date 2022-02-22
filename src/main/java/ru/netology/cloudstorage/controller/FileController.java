package ru.netology.cloudstorage.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.dto.DownloadFileDto;
import ru.netology.cloudstorage.dto.FileRequestDto;
import ru.netology.cloudstorage.dto.FileResponseDto;
import ru.netology.cloudstorage.dto.RenameFileDto;
import ru.netology.cloudstorage.exceptions.FileException;
import ru.netology.cloudstorage.service.FileService;

import java.util.List;

@RestController
@CrossOrigin("http://localhost:8080")
@Log4j
public class FileController {

    private final FileService fileService;
    private static final String tokenAlias = "auth-token";

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<FileResponseDto> getFilesList(@RequestHeader(tokenAlias) String token,
                                              @RequestParam(required = false) int limit) {

        FileRequestDto fileRequestDto = FileRequestDto.builder()
                .rqUserToken(token)
                .limit(limit)
                .build();
        List<FileResponseDto> responseList = fileService.getFilesList(fileRequestDto);
        log.debug("/list - response: " + responseList);
        return responseList;
    }

    @PostMapping(value = "/file")
    public ResponseEntity<String> addNewFile(@RequestHeader(tokenAlias) String token,
                                             @RequestParam("filename") String fileName,
                                             @RequestParam MultipartFile file) throws FileException {

        FileRequestDto fileRequestDto = FileRequestDto.builder()
                .rqUserToken(token)
                .fileName(fileName)
                .file(file)
                .fileSize(file.getSize())
                .build();
        log.debug("/file request: " + fileRequestDto);
        if(!fileService.saveNewFile(fileRequestDto)) {
            throw new FileException();
        }
        return ResponseEntity.ok("Success upload");
    }

    @DeleteMapping(value = "/file")
    public ResponseEntity<String> deleteFile(@RequestHeader("auth-token") String token,
                                             @RequestParam("filename") String fileName) throws FileException {
        FileRequestDto fileRequestDto = FileRequestDto.builder()
                .rqUserToken(token)
                .fileName(fileName)
                .build();
        if(!fileService.deleteFile(fileRequestDto)) {
            throw new FileException();
        }
        log.info("File: " + fileName + ", success deleted;");
        return ResponseEntity.ok("Success deleted");
    }

    @GetMapping(value = "/file")
    public DownloadFileDto downloadFile(@RequestHeader("auth-token") String token,
                                        @RequestParam("filename") String fileName) throws FileException {
        FileRequestDto fileRequestDto = FileRequestDto.builder()
                .rqUserToken(token)
                .fileName(fileName)
                .build();
        return fileService.downloadFile(fileRequestDto);
    }

    @PutMapping(value = "/file")
    public ResponseEntity<String> renameFile(@RequestHeader("auth-token") String token,
                                             @RequestParam("filename") String fileName,
                                             @RequestBody RenameFileDto renameFileDto) {
        renameFileDto.setOriginFileName(fileName);
        renameFileDto.setRqUserToken(token);
        fileService.renameFile(renameFileDto);
        return ResponseEntity.ok("Renamed");
    }
}
