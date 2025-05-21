::: mermaid
flowchart TB
    subgraph Ingress
        C[Client: Web App]
        GW[Vert.x Gateway + Auth Keycloak]
        IN[Pulsar: input topic]
    end

    subgraph Flink Processing
        F1[Flink Job: Phase 1<br>- Xử lý logic<br>- Update RocksDB state<br>- Emit event]
        STATE[RocksDB State<br> user balance, trading state...]
        F2[Flink Job: Phase 2<br>- Đọc kết quả từ topic<br>- Ghi xuống PostgreSQL<br>- Notify client]
    end

    subgraph Storage
        PG[PostgreSQL<br>sổ cái chính thức]
        OUT[Pulsar: processed_topic]
        ERR[Pulsar: error_topic]
        CKPT[S3 / HDFS Checkpoint storage]
    end

    C --> GW --> IN
    IN --> F1 --> STATE
    F1 --> OUT
    F1 --> ERR
    OUT --> F2 --> PG
    F2 --> C
    F1 --> CKPT

:::