package com.alibou.websocket.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataMongoTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
    }

    @Test
    public void should_successfully_save_and_retrieve_a_user() {
        User user = new User();
        user.setNickName("jack");
        user.setFullName("Jack Sparrow");
        user.setStatus(Status.ONLINE);

        userRepository.save(user);

        assertThat(userRepository.findAll().size()).isNotEqualTo(0);
    }

    @Test
    public void should_successfully_find_users_by_given_status() {
        User user1 = new User();
        user1.setNickName("jack");
        user1.setFullName("Jack Sparrow");
        user1.setStatus(Status.OFFLINE);

        User user2 = new User();
        user2.setNickName("alex");
        user2.setFullName("Alex Lindsey");
        user2.setStatus(Status.OFFLINE);

        User user3 = new User();
        user3.setNickName("bill");
        user3.setFullName("Bill Smith");
        user3.setStatus(Status.ONLINE);

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        List<User> onlineUsers = userRepository.findAllByStatus(Status.ONLINE);
        assertThat(onlineUsers.size()).isEqualTo(1);
        assertThat(onlineUsers.get(0).getNickName()).isEqualTo("bill");

        List<User> offlineUsers = userRepository.findAllByStatus(Status.OFFLINE);
        assertThat(offlineUsers.size()).isEqualTo(2);
        assertThat(offlineUsers.get(0).getNickName()).isEqualTo("jack");
        assertThat(offlineUsers.get(1).getNickName()).isEqualTo("alex");
    }
}
