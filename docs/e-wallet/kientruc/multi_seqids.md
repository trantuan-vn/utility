::: mermaid
sequenceDiagram
    autonumber
    participant Client
    participant Gateway as WebSocket Gateway
    participant Pulsar as Pulsar Topic
    participant ServerWorker as Message Processor

    %% Batching ở client
    Note over Client: Bắt đầu thu thập message vào buffer<br>Trigger gửi: mỗi 50ms hoặc đủ 25 message
    Client->>Gateway: sendBatch([msg1, msg2, ..., msg25], ack_seq_id=100)
    Gateway->>Pulsar: publish(msg1)...publish(msg25)

    %% Server xử lý từng message
    Note over ServerWorker: Xử lý liên tục từng message
    Pulsar->>ServerWorker: consume(msg101)
    ServerWorker->>Gateway: ack_progress = 101
    Pulsar->>ServerWorker: consume(msg102)
    ServerWorker->>Gateway: ack_progress = 102
    Pulsar->>ServerWorker: ...
    ServerWorker->>Gateway: ...
    Pulsar->>ServerWorker: consume(msg125)
    ServerWorker->>Gateway: ack_progress = 125

    %% Gửi ACK gộp định kỳ mỗi 150ms
    Note over Gateway: Cumulative ACK scheduler (150ms)
    Gateway->>Client: ack(ack_seq_id=125)

    %% Client nhận ack và xoá message đã gửi thành công
    Client->>Client: clear buffer up to ack_seq_id=125

    %% Gửi tiếp batch mới
    Client->>Gateway: sendBatch([msg126...], ack_seq_id=125)
:::