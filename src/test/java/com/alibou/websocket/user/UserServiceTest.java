package com.alibou.websocket.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private AutoCloseable autoClosable;

    @BeforeEach
    void setUp() {
        autoClosable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoClosable.close();
    }

    @Test
    public void should_successfully_create_user() {
        // Given
        User user = new User();
        user.setNickName("john_smith");
        user.setFullName("John Smith");

        // When
        Mockito.when(userRepository.save(user)).thenReturn(user);
        userService.saveUser(user);

        // Then
        assertEquals(Status.ONLINE, user.getStatus());
    }

    @Test
    public void should_successfully_disconnect_user() {
        // Given
        User user = new User();
        user.setNickName("john_smith");
        user.setFullName("John Smith");

        // When
        Mockito.when(userRepository.save(user)).thenReturn(user);
        Mockito.when(userRepository.findById(user.getNickName())).thenReturn(Optional.of(user));

        userService.saveUser(user);
        userService.disconnect(user);

        // Then
        assertEquals(Status.OFFLINE, user.getStatus());
    }

    @Test
    public void should_successfully_get_online_users() {
        // Given
        User user1 = new User();
        user1.setNickName("john_smith");
        user1.setFullName("John Smith");

        User user2 = new User();
        user2.setNickName("alex_nixon");
        user2.setFullName("Alex Nixon");

        // When
        Mockito.when(userRepository.save(user1)).thenReturn(user1);
        Mockito.when(userRepository.save(user2)).thenReturn(user2);

        userService.saveUser(user1);
        userService.saveUser(user2);

        var arrayList = new ArrayList<User>();
        arrayList.add(user1);
        arrayList.add(user2);

        Mockito.when(userRepository.findAllByStatus(Status.ONLINE)).thenReturn(arrayList);

        // Then
        var listOfConnectedUsers = userService.findConnectedUsers();
        assertEquals(2, listOfConnectedUsers.size());
        listOfConnectedUsers.forEach(user -> assertEquals(Status.ONLINE, user.getStatus()));
    }
}
