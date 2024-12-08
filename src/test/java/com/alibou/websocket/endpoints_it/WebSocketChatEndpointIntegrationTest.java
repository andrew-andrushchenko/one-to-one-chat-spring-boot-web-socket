package com.alibou.websocket.endpoints_it;

import com.alibou.websocket.chat.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.lang.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketChatEndpointIntegrationTest {
    @LocalServerPort
    private int port;
    private String URL;

    private CompletableFuture<ChatMessage> completableFuture;

    private static final String SEND_MESSAGE_ENDPOINT = "/app/chat";

    @BeforeEach
    public void setup() {
        completableFuture = new CompletableFuture<>();
        URL = "ws://localhost:" + port + "/ws";
    }

    @Test
    public void test_send_message_endpoint() throws InterruptedException, ExecutionException, TimeoutException {
        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));

        DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
        resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(new ObjectMapper());
        converter.setContentTypeResolver(resolver);

        stompClient.setMessageConverter(converter);

        StompSession stompSession = stompClient
                .connectAsync(URL, new StompSessionHandlerAdapter() {
                })
                .get(1, TimeUnit.SECONDS);

        String recipientId = "alex";
        stompSession.subscribe(getSubscribeMessageEndpoint(recipientId), new AddChatMessageStompFrameHandler());

        ChatMessage chatMessage = ChatMessage.builder()
                .id("id0")
                .chatId("john_alex")
                .senderId("john")
                .recipientId(recipientId)
                .content("Hello, Alex!")
                .timestamp(new Date())
                .build();

        stompSession.send(SEND_MESSAGE_ENDPOINT, chatMessage);

        ChatMessage receivedMessage = completableFuture.get(5, TimeUnit.SECONDS);

        assertNotNull(receivedMessage);
    }

    @NonNull
    private String getSubscribeMessageEndpoint(String recipientId) {
        return "/user/" + recipientId + "/queue/messages";
    }

    @NonNull
    private List<Transport> createTransportClient() {
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        return transports;
    }

    private class AddChatMessageStompFrameHandler implements StompFrameHandler {
        @Override
        @NonNull
        public Type getPayloadType(@NonNull StompHeaders stompHeaders) {
            System.out.println(stompHeaders);
            return ChatMessage.class;
        }

        @Override
        public void handleFrame(@NonNull StompHeaders stompHeaders, Object o) {
            System.out.println(o);
            completableFuture.complete((ChatMessage) o);
        }
    }
}
