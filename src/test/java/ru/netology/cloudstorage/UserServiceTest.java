package ru.netology.cloudstorage;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;

import ru.netology.cloudstorage.model.User;
import ru.netology.cloudstorage.repository.UserRepository;
import ru.netology.cloudstorage.security.jwt.JwtTokenProvider;
import ru.netology.cloudstorage.service.UserService;

@SpringBootTest
class UserServiceTest {
    private static UserRepository userRepository = mock(UserRepository.class);
    private static JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
    private static UserService userService;

    @BeforeAll
    static void initUserServiceBean() {
        userService = new UserService(userRepository, jwtTokenProvider);
    }

    @Test
    void findByUsernameTest() {
        String userName = "Oleg";
        String lastName = "Ivanov";
        User expectedUser = new User();
        expectedUser.setFirstName(userName);
        expectedUser.setLastName(lastName);
        
        when(userRepository.findByUsername(anyString())).thenReturn(expectedUser);
        
        User actualUser = userService.findByUsername(userName);
        Assertions.assertEquals(expectedUser, actualUser);
    }

    @Test
    void getUserIdTest() {
        String actualUserId;
        String userName = "Ivan";
        String expectedUserId = "12345";
        String jwt = "1221";

        when(jwtTokenProvider.getUsername(anyString())).thenReturn(userName);
        when(userRepository.findUserIdByUsername(userName)).thenReturn(expectedUserId);

        actualUserId = Long.toString(userService.getUserId(jwt));
        Assertions.assertEquals(expectedUserId, actualUserId);
    }

}