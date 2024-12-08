package com.alibou.websocket.chat;

import com.alibou.websocket.chatroom.ChatRoomService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChatMessageServiceTest {

    @InjectMocks
    private ChatMessageService chatMessageService;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private ChatRoomService chatRoomService;

    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    public void should_successfully_send_message() {
        // Given
        String senderId = "john";
        String recipientId = "alex";

        String chatId = String.format("%s_%s", senderId, recipientId);

        ChatMessage chatMessage = new ChatMessage(
                "msg0",
                "",
                senderId,
                recipientId,
                "Hello, Alex!",
                new Date()
        );

        // When
        Mockito.when(chatRoomService.getChatRoomId(senderId, recipientId, true))
                .thenReturn(Optional.of(chatId));

        Mockito.when(chatMessageRepository.save(chatMessage)).thenReturn(null);

        // Then
        var savedMessage = chatMessageService.save(chatMessage);

        assertEquals(savedMessage.getChatId(), chatId);
        assertEquals(savedMessage.getRecipientId(), chatMessage.getRecipientId());
        assertEquals(savedMessage.getContent(), chatMessage.getContent());
    }

    @Test
    public void should_successfully_retrieve_chat_messages() {
        // Given
        String senderId = "john";
        String recipientId = "alex";

        String chatId = String.format("%s_%s", senderId, recipientId);

        ChatMessage chatMessage1 = new ChatMessage(
                "msg1",
                "",
                senderId,
                recipientId,
                "Hello, Alex!",
                new Date()
        );

        ChatMessage chatMessage2 = new ChatMessage(
                "msg2",
                "",
                recipientId,
                senderId,
                "Hello, John!",
                new Date()
        );

        // When
        Mockito.when(chatRoomService.getChatRoomId(senderId, recipientId, true))
                .thenReturn(Optional.of(chatId));

        Mockito.when(chatRoomService.getChatRoomId(recipientId, senderId, true))
                .thenReturn(Optional.of(chatId));

        Mockito.when(chatRoomService.getChatRoomId(senderId, recipientId, false))
                .thenReturn(Optional.of(chatId));

        Mockito.when(chatRoomService.getChatRoomId(recipientId, senderId, false))
                .thenReturn(Optional.of(chatId));

        Mockito.when(chatMessageRepository.save(chatMessage1)).thenReturn(chatMessage1);
        Mockito.when(chatMessageRepository.save(chatMessage2)).thenReturn(chatMessage2);

        var savedMessage1 = chatMessageService.save(chatMessage1);
        var savedMessage2 = chatMessageService.save(chatMessage2);

        List<ChatMessage> expectMessages = new ArrayList<>();
        expectMessages.add(savedMessage1);
        expectMessages.add(savedMessage2);

        Mockito.when(chatMessageRepository.findByChatId(chatId)).thenReturn(expectMessages);

        // Then
        List<ChatMessage> actualMessages = chatMessageService.findChatMessages(senderId, recipientId);

        assertEquals(actualMessages.size(), expectMessages.size());

        actualMessages.forEach(msg -> assertEquals(msg.getChatId(), chatId));

        assertEquals(expectMessages.get(0).getContent(), actualMessages.get(0).getContent());
        assertEquals(expectMessages.get(1).getContent(), actualMessages.get(1).getContent());
    }
}
