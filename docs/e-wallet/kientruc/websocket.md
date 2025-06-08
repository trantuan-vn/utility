::: mermaid
sequenceDiagram
    participant Client as Client (Socket1)
    participant Client2 as Client (Socket2)
    participant Gateway
    participant Redis
    participant Pulsar
    participant Worker

    Note right of Client: 1. Handshake + Auth
    Client->>Gateway: WebSocket Connection { wsUrl, headers: { Authorization: "Bearer ${token}" } }
    alt Token hợp lệ
        Gateway-->>Client: GatewayMessage { type: HANDSHAKE }

        Client->>Gateway: GatewayMessage { type: RESUME, payload: ResumeMessage { lastReceivedSeq, topic, pod } }
        Gateway->>Redis: Get session state by userId
        Redis-->>Gateway: Session found

        par Client2 also connects
            Client->>Gateway: WebSocket Connection { wsUrl, headers: { Authorization: "Bearer ${token}" } }
            Gateway-->>Client2: GatewayMessage { type: HANDSHAKE }

            Client2->>Gateway: GatewayMessage { type: RESUME, payload: ResumeMessage { lastReceivedSeq } }
            Gateway-->>Client2: GatewayMessage { type: STATE_UPDATE, payload: StateUpdateMessage { currentSeq, status } }
        end

        loop Live Session

            %% Gửi message
            Client->>Gateway: GatewayMessage { type: BUSINESS, payload: BusinessMessage { topic, seqId, eventType, data } }
            Gateway->>Pulsar: publish message
            alt success
                Gateway-->>Client: GatewayMessage { type: STATE_UPDATE, payload: StateUpdateMessage { currentSeq, status = "RECEIVED" } }

                Pulsar-->>Worker: deliver message
                Worker-->>Gateway: processing result
                Gateway->>Redis: SET seq:user:{userId} = newSeqId
                Redis-->>Gateway: OK

                Gateway-->>Client2: GatewayMessage { type: STATE_UPDATE, payload: StateUpdateMessage { currentSeq } }
                Client2->>Gateway: GatewayMessage { type: RESUME, payload: ResumeMessage { lastReceivedSeq } }
                Gateway-->>Client2: GatewayMessage { type: STATE_UPDATE, payload: StateUpdateMessage { currentSeq } }

                Gateway-->>Client: GatewayMessage { type: BUSINESS, payload: BusinessMessage { eventType = "MESSAGE_ACK", data } }
            else
                Gateway-->>Client: GatewayMessage { type: ERROR, payload: ErrorMessage { code, reason = "Publish failed" } }
            end

            %% Event nhóm 1: High-priority
            Gateway-->>Client: GatewayMessage { type: BUSINESS, payload: BusinessMessage { eventType = "MESSAGE_RECEIVED" } }
            Gateway-->>Client: GatewayMessage { type: BUSINESS, payload: BusinessMessage { eventType = "MESSAGE_SENT" } }

            %% Event nhóm 2: Session
            alt Session change
                Gateway-->>Client: GatewayMessage { type: BUSINESS, payload: BusinessMessage { eventType = "SESSION_KICKED" } }
            end

            %% Event nhóm 3: Security
            alt Token expired
                Gateway-->>Client: GatewayMessage { type: BUSINESS, payload: BusinessMessage { eventType = "SECURITY_TOKEN_EXPIRED" } }
            end

            %% Event nhóm 4: Business
            opt Mỗi 5s
                Gateway-->>Client: GatewayMessage { type: BUSINESS, payload: BusinessMessage { eventType = "ORDER_PLACED" } }
            end

            %% Event nhóm 5: UI
            opt Mỗi 10s
                Gateway-->>Client: GatewayMessage { type: BUSINESS, payload: BusinessMessage { eventType = "UI_THEME_UPDATE" } }
            end

            %% Event nhóm 6: System status
            opt Mỗi 30s
                Gateway-->>Client: GatewayMessage { type: BUSINESS, payload: BusinessMessage { eventType = "SYSTEM_RECONNECTED" } }
            end

            %% Event nhóm 7: Warning
            opt Khi slow
                Gateway-->>Client: GatewayMessage { type: BUSINESS, payload: BusinessMessage { eventType = "CONNECTION_SLOW" } }
            end

            %% Event nhóm 8: Heartbeat
            opt Mỗi 15s
                Client->>Gateway: GatewayMessage { type: HEARTBEAT, payload: HeartbeatMessage { status = "ping" } }
                Gateway-->>Client: GatewayMessage { type: HEARTBEAT, payload: HeartbeatMessage { status = "pong" } }
            end

        end

        %% Manual disconnect
        Client->>Gateway: GatewayMessage { type: ERROR, payload: ErrorMessage { reason = "Manual disconnect" } }
        Gateway-->>Client: GatewayMessage { type: ERROR, payload: ErrorMessage { reason = "DISCONNECTED" } }
        Gateway--X Client: close

    else Token không hợp lệ
        Gateway-->>Client: GatewayMessage { type: ERROR, payload: ErrorMessage { code = 401, reason = "Unauthorized" } }
        Gateway--X Client: close
    end
:::