::: mermaid
graph TD

  %% === User & DApp Layer ===
  A1[User Wallet / DApp] --> A2[TuấnChain JSON-RPC Endpoint]

  %% === Layer 2 Core (TuấnChain) ===
  subgraph L2[TuấnChain - ZK Stack - Layer 2 Rollup]
    B1[Sequencer 1]:::seq --> B4
    B2[Sequencer 2]:::seq --> B4
    B3[Sequencer N]:::seq --> B4
    B4[Tx Pool / Ordering Consensus]:::core --> C1
    C1[Executor - EVM Runtime]:::core --> C2
    C2[State Keeper - L2 DB]:::core --> C3
    C3[Block Producer]:::core
    C3 --> DA1[Data Availability Layer]:::da
    DA1 --> DA2[Celestia / Avail / EigenDA]:::da
    C3 --> C4[Shared Prover Service]:::prover --> C5
    C5[zkProof Output - .proof]:::prover --> D1
  end

  %% === Ethereum Layer 1 ===
  subgraph L1[Ethereum - Layer 1]
    D1[Verifier Contract]:::eth --> D2[Finalize Valid Block]:::eth
    D1 --> D3[Update L2 State Root]:::eth
    E1[Bridge Contract - L2 ↔ L1]:::eth
    E2[zkSync / Canonical Bridge]:::eth --> E3[Token Escrow / Withdraw]
  end

  %% === Layer 3 Integration ===
  subgraph L3SDK[Tuấn SDK - Layer 3 Integration]
    F1[L3RollupClient]:::sdk --> B4
    F2[L3RPCProvider]:::sdk
    F3[L2BridgeAPI]:::sdk --> E1
    F4[StateSyncAPI]:::sdk --> D3
    F5[ProofAggregator]:::sdk --> D1
    F6[RelayerClient]:::sdk --> B4
  end

  G1[DApp on Layer 3]:::user --> F2

  %% Style Definitions
  classDef core fill:#f2f2ff,stroke:#888,stroke-width:1px;
  classDef prover fill:#fff0f6,stroke:#c26,stroke-width:1px;
  classDef da fill:#e6f7ff,stroke:#0aa,stroke-width:1px;
  classDef eth fill:#fffbe6,stroke:#999,stroke-width:1px;
  classDef sdk fill:#e6fffb,stroke:#0aa,stroke-width:1px;
  classDef seq fill:#e0f7fa,stroke:#088,stroke-width:1px;
  classDef user fill:#f6ffed,stroke:#6c3,stroke-width:1px;

  class A1,A2,G1 user;

:::
