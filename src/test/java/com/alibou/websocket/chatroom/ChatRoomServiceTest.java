package com.alibou.websocket.chatroom;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChatRoomServiceTest {

    @InjectMocks
    private ChatRoomService chatRoomService;

    @Mock
    private ChatRoomRepository chatRoomRepository;

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
    public void should_successfully_create_a_chat_room() {
        // Given
        String senderId = "john";
        String recipientId = "alex";

        String chatId = String.format("%s_%s", senderId, recipientId);

        ChatRoom senderRecipient = ChatRoom
                .builder()
                .chatId(chatId)
                .senderId(senderId)
                .recipientId(recipientId)
                .build();

        ChatRoom recipientSender = ChatRoom
                .builder()
                .chatId(chatId)
                .senderId(recipientId)
                .recipientId(senderId)
                .build();

        // When
        Mockito.when(chatRoomRepository.save(senderRecipient)).thenReturn(senderRecipient);
        Mockito.when(chatRoomRepository.save(recipientSender)).thenReturn(recipientSender);

        // Then

        String newChatId = chatRoomService.createChatId(senderId, recipientId);

        assertEquals(newChatId, chatId);
    }

    @Test
    public void should_successfully_obtain_a_chat_room() {
        // Given
        String senderId = "john";
        String recipientId = "alex";

        String chatId = String.format("%s_%s", senderId, recipientId);
        ChatRoom chatRoom = ChatRoom
                .builder()
                .chatId(chatId)
                .senderId(senderId)
                .recipientId(recipientId)
                .build();

        // When
        Mockito.when(chatRoomRepository.findBySenderIdAndRecipientId(senderId, recipientId))
                .thenReturn(Optional.of(chatRoom));

        // Then
        Optional<String> expectedChatIdOptional = chatRoomService.getChatRoomId(senderId, recipientId, false);
        expectedChatIdOptional.ifPresent(s -> assertEquals(s, chatId));
    }
}