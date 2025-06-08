::: mermaid
flowchart TB
    subgraph Clients [User Devices / Browsers]
        C1[Client 1<br>userId: u123]
        C2[Client 2<br>userId: u456]
        Cn[...]
    end

    subgraph Ingress Layer
        DNS[DNS smartconsultor.com]
        HA[HAProxy<br>SSL Termination<br>SNI Routing<br>443]
    end

    subgraph Kubernetes Cluster
        direction LR

        subgraph API Gateway
            API0[api-pod-0<br>Handles HTTPS /api<br>Auth: Keycloak]
        end

        subgraph WebSocket Gateway
            GW0[ws-gateway-0<br>Handles u%N==0<br>Topic 0-9]
            GW1[ws-gateway-1<br>Handles u%N==1<br>Topic 10-19]
            GWn[...]
        end

        subgraph ResumeReader Pool
            RR0[ResumeReader-0]
            RR1[ResumeReader-1]
        end

        subgraph Checkpoint Store
            CS0[Redis/DB<br>Shard 0]
            CS1[Shard 1]
        end

        subgraph Apache Pulsar
            direction LR
            TP0["user-topic-000"]
            TP1["user-topic-001"]
            TPn["..."]
            Pulsar[(Pulsar Brokers + Bookies)]
        end
    end

    %% Routing
    C1 --> DNS --> HA
    C2 --> DNS --> HA

    HA -->|SNI: www.smartconsultor.com| API0
    HA -->|SNI: ws-0.smartconsultor.com| GW0
    HA -->|SNI: ws-1.smartconsultor.com| GW1

    API0 -->|Auth + Return ws-domain| C1
    API0 -->|Auth + Return ws-domain| C2

    %% Session Handling
    GW0 --> CS0
    GW1 --> CS1

    GW0 --> RR0 --> TP0 --> Pulsar
    GW1 --> RR1 --> TP1 --> Pulsar

    %% Resume logic
    C1 -->|Resume seqId| GW0 -->|Optional Rehash| GW1
:::