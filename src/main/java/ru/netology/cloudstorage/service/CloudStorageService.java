package ru.netology.cloudstorage.service;

import lombok.extern.log4j.Log4j;


import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.netology.cloudstorage.dto.DownloadFileDto;
import ru.netology.cloudstorage.dto.FileRequestDto;
import ru.netology.cloudstorage.dto.FileResponseDto;
import ru.netology.cloudstorage.dto.RenameFileDto;
import ru.netology.cloudstorage.exceptions.FileException;
import ru.netology.cloudstorage.model.Image;
import ru.netology.cloudstorage.model.Status;
import ru.netology.cloudstorage.model.User;
import ru.netology.cloudstorage.repository.FileRepository;
//import ru.netology.cloudstorage.repository.RoleRepository;
import ru.netology.cloudstorage.repository.UserRepository;
import ru.netology.cloudstorage.security.jwt.JwtTokenProvider;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.util.*;


@Service
@Log4j
public class CloudStorageService {

    private final UserRepository userRepository;
//    private final RoleRepository roleRepository;
    private final FileRepository fileRepository;
//    private final BCryptPasswordEncoder passwordEncoder;
    private JwtTokenProvider jwtTokenProvider;

//    @Autowired
    public CloudStorageService(UserRepository userRepository, FileRepository fileRepository,
                               JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.fileRepository = fileRepository;
        this.jwtTokenProvider = jwtTokenProvider;

    }



    public User findByUsername(String username) throws BadCredentialsException {
        User result = userRepository.findByUsername(username);
        log.debug("User found:" + result + ";");
        return result;
    }

    private long getUserId(String jwtToken) {
        String userName = jwtTokenProvider.getUsername(jwtToken);
        return Long.parseLong(userRepository.findUserIdByUsername(userName));
    }

    public boolean saveNewFile(FileRequestDto fileRequestDto) {
        long userId;
        Image image;
        boolean sendStatus = false;
        try {
            byte[] fileBytes = fileRequestDto.getFile().getBytes();
            userId = getUserId(fileRequestDto.getRqUserToken());
            image =  new Image();
            image.setUserId(userId);
            image.setFileName(fileRequestDto.getFileName());
            image.setFileContent(fileBytes);
            image.setFileSize(fileRequestDto.getFileSize());
            log.debug(image.getFileName() + " - " + image.getFileSize());
            if(checkingUniqueness(image.getFileName(), userId)) {
                fileRepository.save(image);
                sendStatus = true;
            } else {
                log.warn("The file was not saved. There is a copy;");
            }

        } catch (Exception ex) {
            log.error(ex.getMessage() + "\n" + Arrays.toString(ex.getStackTrace()));
        }
        return sendStatus;
    }

    private boolean checkingUniqueness(String fileName, long userId) {
        boolean searchStatus = false;
        Image image = fileRepository.findByFileNameAndUserIdAndStatus(fileName, userId, Status.ACTIVE);
        if(image != null && !image.getFileName().equals(fileName)) {
          searchStatus = true;
        } else if (image == null) {
            searchStatus = true;
        }
        return searchStatus;
    }

    public boolean deleteFile(FileRequestDto fileRequestDto) {
        boolean deleteStatus = false;
        long userId = getUserId(fileRequestDto.getRqUserToken());
        long recordId = getRecordId(userId, fileRequestDto.getFileName());
        if(recordId > 0) {
            fileRepository.deleteById(recordId, Status.DELETED, new Timestamp(new Date().getTime()));
            deleteStatus = true;
        } else {
            log.warn("The file to delete does not exist;");;
        }
        return deleteStatus;
    }

    private long getRecordId(long userId, String fileName) {
        Long recordId = fileRepository.findRecordId(userId, fileName, Status.ACTIVE);
        if(recordId == null) {
            recordId = 0L;
            log.warn("Record not found;");
        }
        return recordId;
    }

    public DownloadFileDto downloadFile(FileRequestDto fileRequestDto) throws FileException {
        Image image;
        long userId = getUserId(fileRequestDto.getRqUserToken());
        long recordId = getRecordId(userId, fileRequestDto.getFileName());
        DownloadFileDto downloadFileDto = new DownloadFileDto();
        if(recordId > 0) {
            image = fileRepository.findById(recordId);
            downloadFileDto.setFile(Arrays.toString(image.getFileContent()));
            downloadFileDto.setHash(getFileHash(image.getFileContent()));
        } else {
            throw new FileException();
        }
        log.info("File for download: " + fileRequestDto.getFileName() );
        return downloadFileDto;
    }

    private String getFileHash(byte[] rawFile) {
        final String MD5 = "MD5";
        MessageDigest messageDigest;
        byte[] digest;
        String fileHash = null;
        try {
            messageDigest = MessageDigest.getInstance(MD5);
            messageDigest.update(rawFile);
            digest = messageDigest.digest();
            fileHash = DatatypeConverter.printHexBinary(digest).toUpperCase();
        } catch (Exception ex) {
            log.error(ex.getMessage() + "\n" + Arrays.toString(ex.getStackTrace()));
        }
        return MD5 + ":" + fileHash;
    }

    public boolean renameFile(RenameFileDto renameFileDto) {
        boolean renameState = false;
        long userId = getUserId(renameFileDto.getRqUserToken());
        long recordId = getRecordId(userId, renameFileDto.getOriginFileName());
        if(recordId > 0) {
            log.info("Record(" + recordId + ") changed;");
            fileRepository.renameFile(recordId, renameFileDto.getName(), new Timestamp(new Date().getTime()));
            renameState = true;
        }
        return renameState;
    }

    public List<FileResponseDto> getFilesList(FileRequestDto fileRequestDto) {
        FileResponseDto fileResponseDto;
        List<FileResponseDto> files = new ArrayList<>();
        long userId = getUserId(fileRequestDto.getRqUserToken());
        List<Image> images = fileRepository.getFileList(userId,
                                                        Status.ACTIVE,
                                                        PageRequest.of(0,fileRequestDto.getLimit()));
        for(Image image : images) {
            fileResponseDto = new FileResponseDto();
            fileResponseDto.setFileName(image.getFileName());
            fileResponseDto.setSize(image.getFileSize());
            files.add(fileResponseDto);
        }
        return files;
    }
}
