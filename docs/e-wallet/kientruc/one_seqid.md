::: mermaid
sequenceDiagram
    participant Client
    participant Gateway as WebSocket Gateway
    participant Pulsar_Input as input-topic (Pulsar)
    participant Pulsar_Done as done-topic (Pulsar)
    participant MsgRecvWorker as MessageReceiverWorker
    participant MsgProcWorker as MessageProcessorWorker
    participant DoneProcWorker as DoneProcessorWorker
    participant Redis as Redis/StateStore (optional)

    %% Step 1: Gửi message
    Client->>Gateway: send message(seqId=1)
    Gateway->>Gateway: state = SENDING
    Gateway->>Pulsar_Input: publish message(seqId=1)

    %% Step 2: Server nhận message
    Pulsar_Input->>MsgRecvWorker: message(seqId=1)
    MsgRecvWorker->>Redis: save state = RECEIVED
    MsgRecvWorker->>Gateway: ack(seqId=1, state=RECEIVED)
    Gateway->>Client: ack(seqId=1, state=RECEIVED)
    Gateway->>Gateway: state = RECEIVED
    Note right of Client: starts retry timer
    alt client doesn't receive RECEIVED
        Client-->>Gateway: resend message(seqId=1)
        Gateway-->>Client: ack(seqId=1, state=RECEIVED)
    end
    

    %% Step 3: Server xử lý
    MsgRecvWorker->>MsgProcWorker: forward message(seqId=1)
    MsgProcWorker->>Redis: update state = PROCESSING
    MsgProcWorker->>Redis: update state = SENT
    MsgProcWorker->>Gateway: ack(seqId=1, state=SENT)
    Gateway->>Client: ack(seqId=1, state=SENT)
    Gateway->>Gateway: state = SENT

    Note right of Client: starts retry timer
    alt client doesn't receive SENT
        Client-->>Gateway: resend message(seqId=1)
        Gateway-->>Client: ack(seqId=1, state=SENT)
    end

    Client->>Client: state = ACKED
    Client->>Client: process locally
    Client->>Gateway: send done(seqId=1)
    Gateway->>Pulsar_Done: publish done(seqId=1)

    %% Step 4: Server xử lý done
    Pulsar_Done->>DoneProcWorker: done(seqId=1)
    DoneProcWorker->>Redis: update state = DONE
    DoneProcWorker->>Gateway: ack(seqId=1, state=DONE_CONFIRMED)
    Gateway->>Client: ack(seqId=1, state=DONE_CONFIRMED)
    Gateway->>Gateway: state = DONE
    Note right of Client: starts retry timer
    alt client doesn't receive DONE_CONFIRMED
        Client-->>Gateway: resend done(seqId=1)
        Gateway-->>Client: ack(seqId=1, state=DONE_CONFIRMED)
    end

    
    Client->>Client: state = DONE


:::