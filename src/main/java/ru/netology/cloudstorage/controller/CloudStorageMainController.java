package ru.netology.cloudstorage.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import ru.netology.cloudstorage.dto.AuthenticationRequestDto;
import ru.netology.cloudstorage.dto.FileResponseDto;
import ru.netology.cloudstorage.exceptions.CredentialException;
import ru.netology.cloudstorage.model.User;
import ru.netology.cloudstorage.security.jwt.JwtTokenProvider;
import ru.netology.cloudstorage.service.CloudStorageService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@CrossOrigin("http://localhost:8080")
@Slf4j
public class CloudStorageMainController {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    private final CloudStorageService cloudStorageService;

    @Autowired
    public CloudStorageMainController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, CloudStorageService cloudStorageService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.cloudStorageService = cloudStorageService;
    }

    @PostMapping(value = "/login", produces = "application/json")
    public ResponseEntity<Map<String, String>> login(@RequestBody AuthenticationRequestDto requestDto) {
        try {
            System.out.println(requestDto);
            String username = requestDto.getLogin();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, requestDto.getPassword()));
            User user = cloudStorageService.findByUsername(username);
            requestDto.setRoles(user.getRoles());
            String token = jwtTokenProvider.createToken(requestDto);

            Map<String, String> response = new HashMap<>();
            response.put("auth-token", token);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            throw new CredentialException(ex.getMessage());
        }
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public FileResponseDto getFilesList(@RequestParam(required = false) int limit) {
        //Заглушка
        List<FileResponseDto> responseList = new ArrayList<>();
        FileResponseDto file1 = new FileResponseDto();
        file1.setFilename("String");
        file1.setSize(100);
        responseList.add(file1);
        return file1;
    }

    @PostMapping("/file")
    public void addNewFile() {

    }

}
