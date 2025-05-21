::: mermaid
sequenceDiagram
    autonumber
    participant C as Client
    participant G as Gateway Ingress (LoadBalancer)
    participant W as WS Handler (Pod i (i=1..N))
    participant SM as Session Manager
    participant RH as Resume Handler
    participant R as Resume Reader
    participant P as Pulsar (user-group-* topics)
    participant CS as Checkpoint Store (sharded)

    Note over C,G: Phase 1 - Connect

    C->>G: Connect WebSocket (userId, deviceId)
    G->>W: Route to Pod i (i=1..N) (via i=((groupid=hash(userId) % topicCount) % N)

    W->>SM: Create Session (sessionId, deviceId)
    SM->>CS: Store session info (userId, deviceId, podIndex)
    
    W->>RH: Resume Request (userId, deviceId)
    RH->>CS: Lookup last seqId
    RH->>R: ResumeReader.resume(userId, seqId)

    Note over R,P: Phase 2 - Resume message

    R->>P: Scan user-group-{groupid} from seqId
    P->>R: Return messages
    R->>RH: Deliver messages to RH
    RH->>W: Send messages to WebSocket

    W->>CS: Save latest seqId

    Note over C,W: Phase 3 - Active session

    C-->>W: Client disconnect
    W->>SM: Save session state
    SM->>CS: Store seqId

    Note over C,W: Phase 4 - Reconnect

    C->>G: WebSocket reconnect (userId, deviceId)
    G->>W: Route to correct pod (same logic)

    W->>SM: Lookup session
    SM->>CS: Load seqId
    SM->>RH: Resume from seqId
    RH->>R: ResumeReader.resume(userid, seqid)
    R->>P: Scan from seqId
    P->>R: Return messages
    R->>RH: Deliver messages
    RH->>W: Send to WebSocket
:::