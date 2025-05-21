::: mermaid
sequenceDiagram
    autonumber
    participant Client1 as Client-1 (WebSocket C1)
    participant Client2 as Client-2 (WebSocket C2)
    participant Gateway
    participant Redis as ClientStateStore (Redis)
    participant Pulsar
    participant Processor as Microservice
    participant Client3 as Client-3 (Reconnect)

    %% User kết nối từ 2 thiết bị
    Client1->>Gateway: Connect WS (userId = U123)
    Gateway->>Redis: Save C1 → U123
    Client2->>Gateway: Connect WS (userId = U123)
    Gateway->>Redis: Save C2 → U123

    %% Gửi message input
    Gateway->>Pulsar: Publish Input { userId: U123, msgId: M1 }

    %% Microservice xử lý
    Pulsar->>Processor: Consume M1
    Processor->>Pulsar: Publish Output { userId: U123, msgId: M1, result }

    %% Gateway nhận message output
    Gateway->>Pulsar: Subscribe to Output (shared or per-user sub)
    Pulsar-->>Gateway: Message M1 for U123
    Gateway->>Redis: Save lastProcessedMessageId = M1 for U123

    %% Gửi đến tất cả clientId của U123
    Gateway-->>Client1: Send result M1
    Gateway-->>Client2: Send result M1

    %% Client-1 mất kết nối
    Client1-->>Gateway: Disconnect
    Gateway->>Redis: Remove C1 from U123 client list

    %% Gửi message tiếp theo
    Gateway->>Pulsar: Wait for M2
    Pulsar-->>Gateway: Message M2 for U123
    Gateway-->>Client2: Send result M2
    Gateway->>Redis: Update lastProcessedMessageId = M2 for U123

    %% Client-2 mất kết nối
    Client2-->>Gateway: Disconnect
    Gateway->>Redis: Remove C2 from U123 client list

    %% Reconnect từ thiết bị khác
    Client3->>Gateway: Connect WS (userId = U123)
    Gateway->>Redis: Get lastProcessedMessageId = M2
    Gateway->>Pulsar: Seek to M2 + 1 (or auto resume)
    Gateway->>Redis: Save C3 → U123
    Pulsar-->>Gateway: Message M3
    Gateway-->>Client3: Send result M3
:::