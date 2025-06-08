::: mermaid
sequenceDiagram
    participant Client as Client (Socket1)
    participant Client2 as Client (Socket2)
    participant Gateway
    participant Redis
    participant Pulsar
    participant Worker

    Note right of Client: 1. Handshake + Auth (Token Verification)
    Client->>Gateway: WebSocket Connection { wsUrl, headers: { Authorization: "Bearer ${token}" } }
    alt Token hợp lệ
        Gateway-->>Client: HTTP 101 Switching Protocols

        Client->>Gateway: Envelope { type: SYNC, payload: SyncRequest { seqId } }
        Gateway->>Redis: Get session state (userId)
        Redis-->>Gateway: Session state found

        alt reconnect không resume
            Client->>Gateway: Envelope { type: SYNC, payload: SyncRequest { seqId } }
        end

        alt resume sau reconnect
            Client->>Gateway: Envelope { type: ACK, payload: AckMessage { seqId, ackStatus } }
        end

        par Client2 also connects            
            Client2->>Gateway: WebSocket Connection { wsUrl, headers: { Authorization: "Bearer ${token}" } }
            Gateway-->>Client2: HTTP 101 Switching Protocols
            Client2->>Gateway: Envelope { type: SYNC, payload: SyncRequest { seqId } }
            Gateway-->>Client2: Envelope { type: ACK, payload: AckMessage { seqId, ackStatus } }
        end

        loop Live session

            %% -- Message send flow
            Client->>Gateway: Envelope { type: SEND, payload: SendMessage { content } }
            Gateway->>Pulsar: publish message
            alt success
                Gateway-->>Client: Envelope { type: ACK, payload: AckMessage { messageId, receivedAt } }
                Pulsar-->>Worker: deliver
                Worker-->>Gateway: result

                Gateway->>Redis: SET seq:user:{userId} = newSeqId
                Redis-->>Gateway: OK

                Gateway-->>Client2: Envelope { type: SYNC_UPDATE, payload: SyncRequest { seqId } }
                Client2->>Gateway: Envelope { type: SYNC, payload: SyncRequest { seqId } }
                Gateway-->>Client2: Envelope { type: ACK, payload: AckMessage { seqId, ackStatus } }

                Gateway-->>Client: Envelope { type: ACK, payload: AckMessage { messageId, processedAt, result } }
                Client->>Gateway: Envelope { type: SEND, payload: SendMessage { messageId, receipt } }
            else
                Gateway-->>Client: Envelope { type: NACK, payload: ErrorMessage { reason: "Failed to publish message" } }
            end

            %% -- Group 1: High-priority events (send immediately)
            alt Every message event (immediate dispatch)
                Gateway-->>Client: Envelope { type: EVENT, payload: EventMessage { MESSAGE_RECEIVED } }
                Gateway-->>Client: Envelope { type: EVENT, payload: EventMessage { MESSAGE_SENT } }
                Gateway-->>Client: Envelope { type: EVENT, payload: EventMessage { MESSAGE_FAILED } }
                Gateway-->>Client: Envelope { type: EVENT, payload: EventMessage { MESSAGE_DELETED } }
            end

            %% -- Group 2: Session events
            alt On session change
                Gateway-->>Client: Envelope { type: EVENT, payload: EventMessage { SESSION_KICKED } }
                Gateway-->>Client: Envelope { type: EVENT, payload: EventMessage { SESSION_EXPIRED } }
                Gateway-->>Client: Envelope { type: EVENT, payload: EventMessage { SESSION_CONFLICT } }
            end

            %% -- Group 3: Security events
            alt On security violation
                Gateway-->>Client: Envelope { type: EVENT, payload: EventMessage { SECURITY_TOKEN_EXPIRED } }
                Gateway-->>Client: Envelope { type: EVENT, payload: EventMessage { SECURITY_SUSPICIOUS_LOGIN } }
                Gateway-->>Client: Envelope { type: EVENT, payload: EventMessage { SECURITY_PERMISSION_DENIED } }
            end

            %% -- Group 4: Business events
            opt Every 1-5 seconds
                Gateway-->>Client: Envelope { type: EVENT, payload: EventMessage { ORDER_PLACED } }
                Gateway-->>Client: Envelope { type: EVENT, payload: EventMessage { ORDER_CANCELLED } }
                Gateway-->>Client: Envelope { type: EVENT, payload: EventMessage { NOTIFICATION_COUNT_UPDATED } }
            end

            %% -- Group 5: UI/Sync events
            opt Every 5-10 seconds
                Gateway-->>Client: Envelope { type: EVENT, payload: EventMessage { UI_THEME_UPDATE } }
                Gateway-->>Client: Envelope { type: EVENT, payload: EventMessage { CONFIG_RELOAD } }
                Gateway-->>Client: Envelope { type: EVENT, payload: EventMessage { METRIC_UPDATE } }
            end

            %% -- Group 6: System status events
            opt Every 10-30 seconds or triggered
                Gateway-->>Client: Envelope { type: EVENT, payload: EventMessage { SYSTEM_RECONNECTED } }
                Gateway-->>Client: Envelope { type: EVENT, payload: EventMessage { SYSTEM_DISCONNECTED } }
                Gateway-->>Client: Envelope { type: EVENT, payload: EventMessage { SYSTEM_MAINTENANCE } }
                Gateway-->>Client: Envelope { type: EVENT, payload: EventMessage { SYSTEM_UPGRADE } }
                Gateway-->>Client: Envelope { type: EVENT, payload: EventMessage { UNKNOWN_EVENT } }
            end

            %% -- Group 7: Throttle/warning
            opt On condition
                Gateway-->>Client: Envelope { type: WARNING, payload: ControlMessage { code: 1001, message: "Connection slow" } }
                Gateway-->>Client: Envelope { type: THROTTLE, payload: ControlMessage { limit: 100, retryAfterMs: 5000 } }
            end

            %% -- Group 8: Heartbeat
            opt Every 15 seconds
                Client->>Gateway: Envelope { type: HEARTBEAT, payload: HeartbeatMessage { ping: true } }
                Gateway-->>Client: Envelope { type: HEARTBEAT, payload: HeartbeatMessage { pong: true } }
                Client->>Gateway: Envelope { type: HEARTBEAT, payload: HeartbeatMessage { noop: true } }
                Gateway-->>Client: Envelope { type: HEARTBEAT, payload: HeartbeatMessage { noop: true } }
            end

        end

        %% -- Manual close or kick
        Client->>Gateway: Envelope { type: ERROR, payload: ErrorMessage { reason: "Client request" } }
        Gateway--X Client: close
        Gateway-->>Client: Envelope { type: DISCONNECT, payload: ControlMessage { reason, code } }

    else Token invalid
        Gateway-->>Client: Envelope { type: ERROR, payload: ErrorMessage { message: "Unauthorized" } }
        Gateway--X Client: close
    end
:::