### Spring Webflux Websockets

https://docs.spring.io/spring-framework/reference/web/webflux-websocket.html

1. Dependencies
    - Just `spring-boot-starter-webflux` 

2. First Create a websocket handler
     ```
    @Component
    public class ChatWebSocketHandler implements WebSocketHandler {
        ...
        @Override
        @NonNull
        public Mono<Void> handle(WebSocketSession session) {
            .. do stuff 
        }
    }
    ```

    For more see https://github.com/ItsCosmas/spring-websockets/blob/main/src/main/java/dev/cozy/springwebsockets/ChatWebSocketHandler.java

3. Map the handler to a URL 
    ```
   @Configuration
    public class WebSocketConfig {
    
        // Register the WebSocket handler mapping
        @Bean
        public SimpleUrlHandlerMapping webSocketHandlerMapping(ChatWebSocketHandler chatHandler) {
            return new SimpleUrlHandlerMapping(Map.of(
                    "/ws/chat", chatHandler // Map path to handler
            ), 1); // order
        }
        ...
    }
   ```
4. Run your app and Connect to the websocket server using `ws://localhost:8080/ws/chat` or your configured URL(see 3. above)