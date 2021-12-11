package ru.netology.cloudstorage.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.dto.*;
import ru.netology.cloudstorage.exceptions.CredentialException;
import ru.netology.cloudstorage.exceptions.FileException;
import ru.netology.cloudstorage.model.User;
import ru.netology.cloudstorage.security.jwt.JwtTokenProvider;
import ru.netology.cloudstorage.service.CloudStorageService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@CrossOrigin("http://localhost:8080")
@Log4j
public class CloudStorageMainController {

    private final JwtTokenProvider jwtTokenProvider;
    private final CloudStorageService cloudStorageService;
    private final AuthenticationManager authenticationManager;
    private static final String tokenAlias = "auth-token";
    @Autowired
    public CloudStorageMainController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider,
                                      CloudStorageService cloudStorageService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.cloudStorageService = cloudStorageService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping(value = "/login", produces = "application/json")
    public ResponseEntity<Map<String, String>> login(@RequestBody AuthenticationRequestDto requestDto) {
        User user;
        String token;
        String username;
        try {
            log.debug("Authorization request: " + requestDto);
            username = requestDto.getLogin();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, requestDto.getPassword()));
            user = cloudStorageService.findByUsername(username);
            requestDto.setRoles(user.getRoles());
            token = jwtTokenProvider.createToken(requestDto);
            Map<String, String> response = new HashMap<>();
            response.put(tokenAlias, token);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            throw new CredentialException(ex.getMessage());
        }
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<FileResponseDto> getFilesList(@RequestHeader(tokenAlias) String token,
                                              @RequestParam(required = false) int limit) {

        FileRequestDto fileRequestDto = FileRequestDto.builder()
                                        .rqUserToken(token)
                                        .limit(limit)
                                        .build();
        List<FileResponseDto> responseList = cloudStorageService.getFilesList(fileRequestDto);
        log.debug("/list - response: " + responseList);
        return responseList;
    }

    //Hash  не приходит
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
        if(!cloudStorageService.saveNewFile(fileRequestDto)) {
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
        if(!cloudStorageService.deleteFile(fileRequestDto)) {
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
        return cloudStorageService.downloadFile(fileRequestDto);
    }

    @PutMapping(value = "/file")
    public ResponseEntity<String> renameFile(@RequestHeader("auth-token") String token,
                                             @RequestParam("filename") String fileName,
                                             @RequestBody RenameFileDto renameFileDto) {
        renameFileDto.setOriginFileName(fileName);
        renameFileDto.setRqUserToken(token);
        cloudStorageService.renameFile(renameFileDto);
        return ResponseEntity.ok("Renamed");
    }

}
