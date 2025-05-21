::: mermaid
sequenceDiagram
    autonumber
    participant Client
    participant Gateway
    participant SessionTracker
    participant RateLimiter
    participant MessageRouter
    participant Pulsar
    participant Consumer
    participant PulsarFunction
    participant DB
    participant Observability
    participant Prometheus
    participant KEDA
    participant Robusta
    participant Keptn
    participant Opni
    participant LangChainAgent

    Client->>Gateway: Connect (WebSocket)
    Gateway->>SessionTracker: Restore/Create session
    SessionTracker-->>Gateway: Return seqId

    loop Heartbeat / Keep-Alive
        Client-->>Gateway: Ping
        Gateway-->>Client: Pong
    end

    Client->>Gateway: Send Message
    Gateway->>RateLimiter: Check quota
    alt Over quota
        RateLimiter-->>Gateway: Reject
        Gateway-->>Client: Error
    else Within quota
        RateLimiter-->>Gateway: OK
        Gateway->>MessageRouter: Route by userId
        MessageRouter->>Pulsar: Publish
    end

    Consumer->>Pulsar: Pull (key_shared)
    Pulsar-->>Consumer: Deliver
    Consumer->>PulsarFunction: Process
    PulsarFunction->>DB: Query/Update
    DB-->>PulsarFunction: ACK
    PulsarFunction-->>Gateway: Push to Client

    par Observability
        All->>Prometheus: Metrics
    end

    %% --- AI Agent Interventions on Load Surge ---
    Prometheus-->>Opni: Detect user growth trend (1M → 1B)
    Opni->>LangChainAgent: Anomaly: exponential growth

    LangChainAgent->>KEDA: Request aggressive scaling
    KEDA->>Gateway: Increase replicas (auto-scale)
    KEDA->>Consumer: Add more partitions & replicas
    KEDA->>PulsarFunction: Add instances / threads

    LangChainAgent->>RateLimiter: Adjust token policy (per user)
    LangChainAgent->>Pulsar: Auto-split topic into N partitions
    LangChainAgent->>Pulsar: Increase backlog quota, retention
    LangChainAgent->>Pulsar: Tune dispatch rate & batch size

    LangChainAgent->>DB: Expand connection pool size
    LangChainAgent->>DB: Offload readonly queries to replica

    LangChainAgent->>Robusta: Throttle inactive users
    Robusta->>Gateway: Limit reconnect storm

    LangChainAgent->>Keptn: Trigger remediation workflow
    Keptn->>PulsarFunction: Enable async + buffer overflow control
    Keptn->>Gateway: Apply connection queue priorities

    LangChainAgent-->>DevOps: Report: 1B user scaling success
:::