package ru.netology.cloudstorage;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;

import ru.netology.cloudstorage.dto.DownloadFileDto;
import ru.netology.cloudstorage.dto.FileRequestDto;
import ru.netology.cloudstorage.dto.RenameFileDto;
import ru.netology.cloudstorage.exceptions.FileException;
import ru.netology.cloudstorage.repository.FileRepository;
import ru.netology.cloudstorage.service.FileService;
import ru.netology.cloudstorage.service.UserService;
import ru.netology.cloudstorage.model.Image;

public class FileServiceTest {
    
    private static FileRepository fileRepository = mock(FileRepository.class);
    private static UserService userService = mock(UserService.class);
    private static MultipartFile multipartFile;
    private static FileService fileService;
    private static FileRequestDto fileRequestDto;
    
    @BeforeAll
    static void initTestConext() {
        initFileServiceBean();
        initMulipartFile();
        initFileRequestDto();
        
    }

    private static void initFileServiceBean() {
        fileService = new FileService(fileRepository, userService);
    }

    static void initMulipartFile() {
        byte[] content = "Test file content".getBytes();
        multipartFile = new MockMultipartFile("text.txt",
            "test.txt", "text/plain", content);
    }

    private static void initFileRequestDto() {
        fileRequestDto = FileRequestDto.builder()
                                        .fileName("TestFile")
                                        .file(multipartFile)
                                        .rqUserToken("TestToken")
                                        .build();
    }

    @Test
    void saveNewFileValidTest() {
        boolean expectedResult = true;
        boolean actualResult;

        when(userService.getUserId(anyString())).thenReturn(123L);
        when(fileRepository.save(any())).thenReturn(null);
        
        actualResult = fileService.saveNewFile(fileRequestDto);
        Assertions.assertEquals(expectedResult, actualResult);
    }

    @Test
    void saveNewFileErrorTest() {
        FileRequestDto notValidFileRequestDto = FileRequestDto.builder().fileName("NotValid").build();
        boolean expectedResult = false;
        boolean actualResult;

        when(userService.getUserId(anyString())).thenReturn(123L);
        when(fileRepository.save(any())).thenReturn(null);
        
        actualResult = fileService.saveNewFile(notValidFileRequestDto);
        Assertions.assertEquals(expectedResult, actualResult);
    }

    @Test
    void checkingUniquenessValidTest() {
        boolean expectedResult = true;
        boolean actualResult;
        long userId = 1L;
        Image image = new Image();
        image.setUserId(userId);
        image.setFileName("TestName");

        when(fileRepository.findByFileNameAndUserIdAndStatus(anyString(), anyLong(), any()))
        .thenReturn(image);

        actualResult = fileService.checkingUniqueness("Name", userId);
        Assertions.assertEquals(expectedResult, actualResult);    
    }

    @Test
    void checkingUniquenessErrorTest() {
        boolean expectedResult = false;
        boolean actualResult;
        String fileName = "TestFileName";
        long userId = 1L;
        Image image = new Image();
        image.setUserId(userId);
        image.setFileName(fileName);

        when(fileRepository.findByFileNameAndUserIdAndStatus(anyString(), anyLong(), any()))
        .thenReturn(image);

        actualResult = fileService.checkingUniqueness(fileName, userId);
        Assertions.assertEquals(expectedResult, actualResult);    
    }

    @Test
    void deleteFileValidTest() {
        long userId = 1L;
        long recordId = 2L;
        boolean expectedResult = true;
        boolean actualResult;
        
        when(userService.getUserId(anyString())).thenReturn(userId);
        when(fileRepository.findRecordId(anyLong(), anyString(), any())).thenReturn(recordId);
        doNothing().when(fileRepository).deleteById(anyLong(), any(), any());
        
        actualResult = fileService.deleteFile(fileRequestDto);
        Assertions.assertEquals(expectedResult, actualResult);
    }
    
    @Test
    void deleteFileErrorTest() {
        long userId = 1L;
        long recordId = 0L;
        boolean expectedResult = false;
        boolean actualResult;
        
        when(userService.getUserId(anyString())).thenReturn(userId);
        when(fileRepository.findRecordId(anyLong(), anyString(), any())).thenReturn(recordId);
        doNothing().when(fileRepository).deleteById(anyLong(), any(), any());
        
        actualResult = fileService.deleteFile(fileRequestDto);
        Assertions.assertEquals(expectedResult, actualResult);
    }

    @Test
    void deleteGetRecoerdIdValidTest() {
        long recordId = 1L;
        long expectedResult = recordId;
        long actualResult;
        
        when(fileRepository.findRecordId(anyLong(), anyString(), any())).thenReturn(recordId);

        actualResult = fileService.getRecordId(1L, "TestFileName");
        Assertions.assertEquals(expectedResult, actualResult);
    }

    @Test
    void deleteGetRecoerdIdErrorTest() {
        long expectedResult = 0L;
        long actualResult;
        
        when(fileRepository.findRecordId(anyLong(), anyString(), any())).thenReturn(null);

        actualResult = fileService.getRecordId(1L, "TestFileName");
        Assertions.assertEquals(expectedResult, actualResult);
    }

    @Test
    void downloadFileValidTest() {
        long userId = 1L;
        long recordId = userId;
        Image image = new Image();
        image.setFileContent("TestConetnt".getBytes());
        DownloadFileDto downloadFile = null;

        when(userService.getUserId(anyString())).thenReturn(userId);
        when(fileRepository.findRecordId(anyLong(), anyString(), any())).thenReturn(recordId);
        when(fileRepository.findById(anyLong())).thenReturn(image);

        try {
            downloadFile = fileService.downloadFile(fileRequestDto);
        } catch (FileException e) {
            e.printStackTrace();
        }
        
        Assertions.assertNotNull(downloadFile);
        Assertions.assertNotNull(downloadFile.getHash());
        Assertions.assertNotNull(downloadFile.getFile());
        System.out.println(downloadFile);
    
    }

    @Test
    void downloadFileErrorTest() {
        long userId = 1L;
        long recordId = 0L;
        Image image = new Image();
        image.setFileContent("TestConetnt".getBytes());

        when(userService.getUserId(anyString())).thenReturn(userId);
        when(fileRepository.findRecordId(anyLong(), anyString(), any())).thenReturn(recordId);
        when(fileRepository.findById(anyLong())).thenReturn(image);

        Assertions.assertThrows(FileException.class, () -> {
                                               fileService.downloadFile(fileRequestDto);
                                               });
    }

    @Test
    void renameFileValidTest() {
        long userId = 1L;
        long recordId = userId;
        boolean expectedResult = true;
        boolean actualResult;
        RenameFileDto renameFileDto = new RenameFileDto();
        renameFileDto.setName("TestFile");
        renameFileDto.setRqUserToken("TestToken");
        renameFileDto.setOriginFileName("TestFile");

        when(userService.getUserId(anyString())).thenReturn(userId);
        when(fileRepository.findRecordId(anyLong(), anyString(), any())).thenReturn(recordId);
        doNothing().when(fileRepository).renameFile(anyLong(), anyString(), any());
        
        actualResult = fileService.renameFile(renameFileDto);
        Assertions.assertEquals(expectedResult, actualResult);
    }

    @Test
    void renameFileErrorTest() {
        long userId = 1L;
        long recordId = 0L;
        boolean expectedResult = false;
        boolean actualResult;
        RenameFileDto renameFileDto = new RenameFileDto();
        renameFileDto.setName("TestFile");
        renameFileDto.setRqUserToken("TestToken");
        renameFileDto.setOriginFileName("TestFile");

        when(userService.getUserId(anyString())).thenReturn(userId);
        when(fileRepository.findRecordId(anyLong(), anyString(), any())).thenReturn(recordId);
        
        actualResult = fileService.renameFile(renameFileDto);
        Assertions.assertEquals(expectedResult, actualResult);
    }

    @Test
    void getFilesListValidTest() {
        long userId = 1L;
        int actualResult;
        int expectedResult = 2;
        Image image1 = new Image();
        image1.setFileName("TestFile-1");
        image1.setFileSize(1L);
        Image image2 = new Image();
        image2.setFileName("TestFile-2");
        image2.setFileSize(2L);
        List<Image> images =  Arrays.asList(image1, image2);
        fileRequestDto.setLimit(3);

        when(userService.getUserId(anyString())).thenReturn(userId);
        when(fileRepository.getFileList(anyLong(), any(), any())).thenReturn(images);
        
        actualResult = fileService.getFilesList(fileRequestDto).size();

        Assertions.assertEquals(expectedResult, actualResult);
    }

    @Test
    void getFilesListErrorTest() {
        long userId = 1L;
        Image image1 = new Image();
        image1.setFileName("TestFile-1");
        image1.setFileSize(1L);
        Image image2 = new Image();
        image2.setFileName("TestFile-2");
        image2.setFileSize(2L);
        List<Image> images =  Arrays.asList(image1, image2);
        fileRequestDto.setLimit(0);
        when(userService.getUserId(anyString())).thenReturn(userId);
        when(fileRepository.getFileList(anyLong(), any(), any())).thenReturn(images);
        
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            fileService.getFilesList(fileRequestDto).size();
        });
    }

}
