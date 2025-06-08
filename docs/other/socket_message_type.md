::: mermaid
sequenceDiagram
    participant Client
    participant Gateway
    participant Pulsar
    participant Worker

    %% --- Handshake & Auth ---
    Note right of Client: WebSocket handshake + Authorization

    Client->>Gateway: WebSocket handshake (Authorization header)
    alt Token hợp lệ
        Gateway-->>Client: HTTP 101 Switching Protocols

        %% --- INIT ---
        Client->>Gateway: type: "init", payload: { seqId }
        Gateway-->>Client: type: "init_ack", payload: { seqId }

        %% --- Resume sau disconnect ---
        alt Client reconnect + resume
            Client->>Gateway: type: "resume", payload: { seqId }
            Gateway-->>Client: type: "synced", payload: { lastSeqId }
        end

        loop Session Active

            %% --- Message từ Client ---
            Client->>Gateway: type: "message", payload: { content }

            %% --- Push vào queue (e.g. Pulsar) ---
            Gateway->>Pulsar: publish message
            alt Thành công
                Gateway-->>Client: type: "ack_queued", payload: { messageId, receivedAt }

                %% --- Worker xử lý ---
                Pulsar-->>Worker: deliver message
                Worker-->>Gateway: xử lý xong (messageId + result)
                Gateway-->>Client: type: "ack_processed", payload: { messageId, processedAt, result }

            else Gửi queue thất bại
                Gateway-->>Client: type: "error", payload: { reason: "pulsar_error" }
            end

            %% --- Push event từ server (ví dụ: noti, state update) ---
            Gateway-->>Client: type: "event", payload: { eventType, data }

            %% --- Ping-pong giữ kết nối ---
            Client->>Gateway: type: "ping"
            Gateway-->>Client: type: "pong"

        end

        %% --- Client đóng kết nối ---
        Client->>Gateway: type: "close", payload: { reason }
        Gateway--X Client: Đóng kết nối

    else Token không hợp lệ
        Gateway-->>Client: type: "unauthorized", payload: { message }
        Gateway--X Client: Đóng kết nối
    end
:::