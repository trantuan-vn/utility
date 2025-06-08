::: mermaid

graph TD
    %% CLIENT
    subgraph CLIENT
        C1[Flutter App]
    end

    %% LOAD BALANCER
    subgraph LB
        L1[HAProxy<br>or Envoy]
    end

    %% GATEWAY
    subgraph GATEWAY
        G1[Vert.x Gateway Pod 1]
        G2[Vert.x Gateway Pod N]
        G1 <--> G2
    end

    subgraph AUTOSCALING_GATEWAY
        HPA1[Gateway HPA<br>CPU 60 percent ➜ scale 2 to 50]
    end

    %% AUTH
    subgraph AUTH
        A1[Keycloak]
        A2[JWT Cache<br>Verification]
        CJ1[Cron<br>Refresh JWT Key]
    end

    %% SECURITY
    subgraph SECURITY
        S1[mTLS<br>Zero Trust]
        S2[Audit Logs]
        S3[Encryption<br>In Transit and At Rest]
        OPA1[OPA/Gatekeeper<br>Policy as Code]
        SM1[Service Mesh<br>Istio or Linkerd]
        SE1[Secrets Manager<br>Vault/SealedSecrets]
        WAF1[WAF/API Gateway<br>Advanced Layer]
    end

    %% PULSAR CLUSTER
    subgraph PULSAR
        B1[Pulsar Broker Pod 1]
        B2[Pulsar Broker Pod N]
        BK1[BookKeeper Pod 1]
        BK2[BookKeeper Pod N]
        ZK1[Zookeeper Pod 1]
        ZK2[Zookeeper Pod 2]
        P1[gw-requests]
        P2[gw-respon]
        P3[bigdata-requests]
        P4[dead-letter-queue]
        SR1[Schema Registry<br>Protobuf]
        GR1[Geo Replication]
    end

    subgraph AUTOSCALING_PULSAR
        HPA2[Pulsar Broker HPA<br>CPU or msg rate]
    end

    %% BUSINESS DAEMON
    subgraph BUSINESS
        D1[Worker 1<br>Key Shared]
        D2[Worker N]
        DB1[PostgreSQL]
        CJ2[Cron<br>Data Cleanup]
        CJ3[Cron<br>DLQ Retry]
        CB1[Retry<br>Circuit Breaker]
    end

    subgraph AUTOSCALING_BUSINESS
        KEDA1[Worker KEDA<br>Queue Lag ➜ scale 0 to 100]
    end

    %% POSTGRESQL AUTOSCALING
    subgraph AUTOSCALING_POSTGRES
        HPA3[PostgreSQL HPA<br>Based on CPU or Memory]
    end

    %% BIG DATA
    subgraph BIGDATA
        BD1[BigData Worker<br>Spark or NiFi]
        H1[HDFS Manager Node]
        CJ6[Cron<br>Hadoop Cleanup]
    end

    %% OBSERVABILITY
    subgraph OBSERVABILITY
        M1[Prometheus]
        M2[Grafana]
        M3[FluentBit or Loki]
        OTEL[OpenTelemetry]
        CJ4[Cron<br>Analytics Aggregation]
        JA1[Jaeger or Tempo<br>Tracing]
        ALT1[Alertmanager]
        RUM1[RUM - Real User Monitoring]
    end

    %% DEVOPS
    subgraph CI_CD
        CD1[GitOps<br>ArgoCD or Flux]
        CD2[Helm Deployments]
        CD3[Image Scan and SBOM]
        DR1[Backup and Recovery]
        CH1[Chaos Testing<br>Litmus]
        TEK1[Tekton / Argo Workflows]
        IB1[Image Build<br>Kaniko/BuildKit]
        SLSA1[Supply Chain Security<br>SLSA/Sigstore]
    end

    %% AUDIT & COMPLIANCE
    subgraph COMPLIANCE
        AUD1[Audit Trail]
        SCN1[Policy Scan<br>Trivy, Polaris]
    end

    %% RELIABILITY
    subgraph RELIABILITY
        LOAD1[Load Testing<br>k6 or Locust]
        DEP1[Canary/Blue-Green<br>Deployment Strategy]
    end

    %% AI OPS
    subgraph AIOPS
        AIA1[AI-based Alerting]
        HEAL1[Self-healing Controller]
    end

    %% DOCUMENTATION
    subgraph RUNBOOKS
        DOC1[Runbooks]
        DOC2[SOPs]
        DOC3[System Diagrams]
    end

    %% FLOWS
    C1 --> L1 --> G1
    G1 --> A1 --> A2
    A2 --> P1
    CJ1 --> A2

    P1 --> D1
    D1 -->|Store| DB1
    CJ2 --> DB1
    D1 --> P2 --> G2 --> C1
    G2 --> P3 --> BD1
    BD1 --> H1
    CJ6 --> H1

    D1 --> P4
    CJ3 --> P4 --> D1

    %% MONITORING PATHS
    G1 --> M1
    D1 --> M1
    BD1 --> M1
    M1 --> M2
    G1 --> M3
    D1 --> M3
    OTEL --> G1
    OTEL --> D1
    G1 --> JA1
    JA1 --> M2
    ALT1 --> M2
    RUM1 --> M2

    %% DEVOPS
    CD1 --> CD2 --> G1
    CD2 --> D1
    CD3 --> CD1
    TEK1 --> CD2
    IB1 --> TEK1
    SLSA1 --> CD3
    DR1 --> DB1
    DR1 --> PULSAR
    CH1 --> D1

    %% SECURITY
    S1 --> G1
    S2 --> M3
    S3 --> G1
    OPA1 --> G1
    SM1 --> G1
    SE1 --> A1
    WAF1 --> L1

    %% COMPLIANCE
    AUD1 --> M3
    SCN1 --> CD3

    %% RELIABILITY
    LOAD1 --> G1
    DEP1 --> CD2

    %% AIOPS
    AIA1 --> ALT1
    HEAL1 --> D1

    %% DOC
    DOC1 --> CI_CD
    DOC2 --> BUSINESS
    DOC3 --> OBSERVABILITY

    %% PULSAR EXTRA
    SR1 --> PULSAR
    GR1 --> PULSAR

    %% ANALYTICS
    CJ4 --> DB1
    CJ4 --> M1

    %% AUTOSCALING CONNECTIONS
    HPA1 --> G1
    KEDA1 --> D1
    HPA2 --> B1
    HPA3 --> DB1
:::