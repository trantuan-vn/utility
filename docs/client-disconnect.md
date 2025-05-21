::: mermaid
sequenceDiagram
    autonumber
    participant Client1 as Client-1 (WebSocket)
    participant Gateway
    participant Redis as ClientStateStore (Redis)
    participant Pulsar
    participant Processor as Microservice
    participant Client2 as Client-2 (Reconnect)

    %% Kết nối ban đầu
    Client1->>Gateway: Connect WS (userId = U123)
    Gateway->>Redis: Save clientId = C1, userId = U123
    Gateway->>Pulsar: Publish Input { userId: U123, msgId: M1 }

    %% Xử lý message
    Pulsar->>Processor: Consume M1
    Processor->>Pulsar: Publish Output { userId: U123, msgId: M1, result }

    %% Gateway đẩy kết quả
    Gateway->>Pulsar: Subscribe to Output topic (shared or per-user sub)
    Pulsar-->>Gateway: Message M1 (for userId = U123)
    Gateway->>Redis: Save lastProcessedMessageId = M1 for userId = U123
    Gateway-->>Client1: Send result M1

    %% Mất kết nối
    Client1-->>Gateway: Disconnect
    Gateway->>Redis: Mark C1 offline

    %% Kết nối mới
    Client2->>Gateway: Connect WS (userId = U123)
    Gateway->>Redis: Lookup lastProcessedMessageId = M1 for U123
    Gateway->>Pulsar: Seek consumer to M1+1 (or auto resume if per-user sub)
    Gateway->>Redis: Save clientId = C2, userId = U123
    Pulsar-->>Gateway: Message M2
    Gateway-->>Client2: Send result M2
:::