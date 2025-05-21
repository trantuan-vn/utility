::: mermaid
sequenceDiagram
    participant Client
    participant Gateway
    participant Redis

    Note over Client, Gateway: Đăng ký kết nối mới
    Client->>Gateway: Open WebSocket (socketID)
    Gateway->>Redis: HSET session:{userId}:{socketID}={podId}<br>HSET conn:{socketID}={userId}:{deviceId}

    Note over Client, Gateway: Resume xử lý
    Gateway->>Redis: HGET seq:{socketID}
    Redis-->>Gateway: lastSeqId (nếu có)
    Gateway->>Client: Resume from lastSeqId

    Note over Client, Gateway: Xử lý message mới
    Client->>Gateway: Send message with seqId
    Gateway->>Redis: HSET seq:{socketID}={seqId}

    Note over Client, Gateway: Huỷ kết nối
    Client-->>Gateway: Disconnect WebSocket (socketID)
    Gateway->>Redis: HGET conn socketID
    alt socketID tồn tại
        Gateway->>Redis: HDEL session:{userId} {socketID}<br>HDEL conn {socketID}<br>HDEL seq {socketID}
    else socketID không tồn tại
        Note over Gateway: Bỏ qua
    end

:::