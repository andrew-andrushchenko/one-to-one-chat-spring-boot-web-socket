package com.alibou.websocket.chat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataMongoTest
class ChatMessageRepositoryTest {

    @Autowired
    public ChatMessageRepository chatMessageRepository;

    @BeforeEach
    public void setup() {
        chatMessageRepository.deleteAll();
    }

    @Test
    public void should_successfully_create_a_chat_message() {
        String senderId = "jack";
        String recipientId = "alex";
        String chatId = String.format("%s_%s", senderId, recipientId);

        ChatMessage chatMessage = new ChatMessage(
                "msg0",
                chatId,
                senderId,
                recipientId,
                "Hello, Alex!",
                new Date()
        );

        chatMessageRepository.save(chatMessage);

        assertThat(chatMessageRepository.findAll()).isNotEqualTo(0);
    }

    @Test
    public void should_successfully_find_a_chat_messages_by_chat_id() {
        String senderId = "jack";
        String recipientId = "alex";
        String chatId = String.format("%s_%s", senderId, recipientId);

        ChatMessage chatMessage1 = new ChatMessage(
                "msg0",
                chatId,
                senderId,
                recipientId,
                "Hello, Alex!",
                new Date()
        );

        ChatMessage chatMessage2 = new ChatMessage(
                "msg1",
                chatId,
                recipientId,
                senderId,
                "Hi, Jack!",
                new Date()
        );

        ChatMessage chatMessage3 = new ChatMessage(
                "msg2",
                "other_another",
                "other",
                "another",
                "Hello!",
                new Date()
        );

        chatMessageRepository.save(chatMessage1);
        chatMessageRepository.save(chatMessage2);
        chatMessageRepository.save(chatMessage3);

        List<ChatMessage> chatMessages = chatMessageRepository.findByChatId(chatId);
        assertThat(chatMessages.size()).isEqualTo(2);
    }
}