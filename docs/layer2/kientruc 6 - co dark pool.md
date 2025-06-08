::: mermaid
graph TD

  %% === User Layer ===
  A0[Tuấn's e-Wallet - Mobile/Web]:::wallet
  A0 --> A2[TuấnChain JSON-RPC Endpoint]:::user
  A0 --> E1[Bridge Contract - TuấnChain ↔ Ethereum]:::eth
  A0 --> F2[L3RPCProvider]:::sdk
  A0 --> S1[Onchain Identity / ENS]:::infra

  A1[Other DApp / Dashboard]:::user --> A2

  %% === External Apps ===
  EX1[L3 GameFi App]:::ext --> F2
  EX2[ZK Wallet]:::ext --> F3
  EX3[Oracle Service]:::ext --> F4
  EX4[Explorer / Indexer]:::ext --> F2
  EX5[Analytics Platform]:::ext --> F2
  EX6[CEX / On-ramp]:::ext --> E1
  EX7[L3 Rollup-as-a-Service]:::ext --> F1

  %% === Layer 2 Core ===
  subgraph L2[TuấnChain - ZK Stack - Layer 2 Rollup]
    A2 --> B1[Sequencer 1]:::seq
    A2 --> B2[Sequencer 2]:::seq
    A2 --> B3[Sequencer N]:::seq
    B1 & B2 & B3 --> B4[Tx Pool / Consensus]:::core
    B4 --> C1[Executor - EVM / WASM]:::core
    C1 --> C2[State Keeper - L2 DB]:::core
    C2 --> C3[Block Producer]:::core
    C3 --> C4[Shared Prover - Prover-as-a-Service]:::prover
    C4 --> C5[zkProof - .proof]:::prover

    C3 --> DA1[Data Availability Layer]:::da
    DA1 --> DA2[Celestia / Avail / EigenDA]:::da

    %% Dark Pool Integration
    A0 --> DP1[Dark Pool Matching Engine]:::darkpool
    DP1 --> B4
    DP1 --> C2
    DP1 --> F2
  end

  %% === L1 Ethereum ===
  subgraph L1[Ethereum - Layer 1]
    C5 --> D1[Verifier Contract - on Ethereum]:::eth
    D1 --> D2[Finalize Valid Block]:::eth
    D1 --> D3[Update State Root]:::eth
    E1 --> E2[zkSync Bridge / Canonical Bridge]:::eth
    E2 --> E3[Token Escrow / Withdraw Finality]:::eth
  end

  %% === Layer 3 SDK ===
  subgraph SDK[TuấnChain SDK / Layer 3 Rollup Tools]
    F1[L3RollupClient]:::sdk --> B4
    F2[L3RPCProvider]:::sdk
    F3[L2BridgeAPI]:::sdk --> E1
    F4[StateSyncAPI]:::sdk --> D3
    F5[ProofAggregator]:::sdk --> D1
    F6[RelayerClient]:::sdk --> B4
    F7[tuanchain-cli / Dev Tools]:::sdk
    G1[DApp on Layer 3]:::user --> F2
    G1 --> DP1
  end

  %% === Security & Governance ===
  subgraph SEC[Security / Upgradeability / Identity]
    S1[Onchain Identity / ENS]:::infra
    S2[Sig Verifier / Anti-Replay]:::infra
    S3[Upgrade Manager / Governance DAO]:::infra
  end

  %% === Observability ===
  subgraph OBS[Monitoring & Metrics]
    O1[Prometheus Exporter]:::infra
    O2[OpenTelemetry Tracing]:::infra
    O3[Log Aggregator - Loki/ELK]:::infra
  end

  %% === Networking ===
  subgraph NET[Networking / Interop]
    N1[IBC / xMessage Layer]:::infra
    N2[Cross-rollup Bridge - zkBridge]:::infra
    N3[Relayer Mesh Network]:::infra
  end

  %% === Internal Connections ===
  B4 --> S2
  D1 --> S3
  C1 --> O1
  C4 --> O2
  B4 --> N1
  F6 --> N3

  %% === Style defs ===
  classDef core fill:#f2f2ff,stroke:#aaa,stroke-width:1px;
  classDef prover fill:#fff0f6,stroke:#c26,stroke-width:1px;
  classDef da fill:#e6f7ff,stroke:#0aa,stroke-width:1px;
  classDef eth fill:#fffbe6,stroke:#999,stroke-width:1px;
  classDef sdk fill:#e6fffb,stroke:#0aa,stroke-width:1px;
  classDef seq fill:#e0f7fa,stroke:#088,stroke-width:1px;
  classDef user fill:#f6ffed,stroke:#6c3,stroke-width:1px;
  classDef wallet fill:#e6f7ff,stroke:#2b8a3e,stroke-width:2px,font-weight:bold;
  classDef infra fill:#eaeaff,stroke:#559,stroke-width:1px;
  classDef ext fill:#fdf6e3,stroke:#b58900,stroke-width:1.5px;
  classDef darkpool fill:#fff4e6,stroke:#d9822b,stroke-width:2px,font-weight:bold;
:::
