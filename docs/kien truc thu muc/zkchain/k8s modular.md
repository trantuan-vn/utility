::: mermaid
flowchart TD
    subgraph K8sCluster["Kubernetes Cluster (Namespace: l2-offchain)"]
    
        %% Core components
        S1[Sequencer Deployment<br>stateless, scale-out]
        P1[Prover Flink Job<br>GPU-enabled]
        R1[Relayer Deployment<br>event-driven]
        Q1[Task Queue Pulsar]
        DB1[(PostgreSQL<br>Sequencer DB)]

        %% Infra + Observability
        LOG1[Logging: Loki/EFK]
        MON1[Monitoring: Prometheus]
        SEC1[Secrets: Vault / K8s Secrets]
        CD1[GitOps: ArgoCD / FluxCD]

        %% Connections
        S1 -->|Submit Task| Q1
        Q1 -->|Consume Proof Job| P1
        P1 -->|Submit zkProof| R1
        

        S1 --> DB1
        R1 --> DB1

        S1 --> MON1
        P1 --> MON1
        R1 --> MON1

        S1 --> LOG1
        P1 --> LOG1
        R1 --> LOG1

        S1 --> SEC1
        P1 --> SEC1
        R1 --> SEC1

        CD1 --> S1
        CD1 --> P1
        CD1 --> R1
    end

    %% External Chains
    subgraph ChainNguon["Chain Nguồn"]
        UserTX[User TX]
        SourceContract[App Contract / MessageOutbox]
    end

    subgraph ChainDich["Chain Đích"]
        VerifierContract[Verifier Contract<br>zkProof validation]
        Executor[Replayer / Executor Contract]
    end

    %% Cross-chain flow
    UserTX --> SourceContract
    SourceContract -->|Emit Event| S1
    R1 -->|Relay Proof+Msg| VerifierContract
    VerifierContract --> Executor
:::