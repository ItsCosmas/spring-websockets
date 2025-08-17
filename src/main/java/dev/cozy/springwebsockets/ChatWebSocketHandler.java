package dev.cozy.springwebsockets;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Component
public class ChatWebSocketHandler implements WebSocketHandler {

    private final Sinks.Many<ChatMessage> messageSink = Sinks.many().replay().all();
    private final Flux<ChatMessage> broadcastStream = messageSink.asFlux();
    private final ObjectMapper objectMapper;

    public ChatWebSocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    @NonNull
    public Mono<Void> handle(WebSocketSession session) {

        // Incoming: Parse and broadcast
        Mono<Void> incoming = session.receive()
                .map(this::toChatMessage)
                .doOnNext(chatMessage -> {
                    System.out.println("Received: " + chatMessage);
                    messageSink.tryEmitNext(chatMessage);
                })
                .doOnTerminate(() -> System.out.println("Client disconnected"))
                .then();

        // Outgoing: Per-session message creation
        Flux<WebSocketMessage> outgoing = broadcastStream
                .flatMap(chatMessage -> Mono.fromCallable(() -> {
                    String json = objectMapper.writeValueAsString(chatMessage);
                    return session.textMessage(json);
                })
                        .onErrorResume(e -> {
                            System.err.println("Failed to serialize message: " + e.getMessage());
                            return Mono.just(session.textMessage(
                                    "{\"from\":\"System\",\"message\":\"[Invalid content]\"}"));
                        }));

        // Combine send and receive
        return session.send(outgoing).and(incoming);

    }

    private ChatMessage toChatMessage(WebSocketMessage webSocketMessage) {
        try {
            return objectMapper.readValue(webSocketMessage.getPayloadAsText(), ChatMessage.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON format", e);
        }
    }
}
