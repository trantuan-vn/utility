::: mermaid
graph TD

  %% === User Layer ===
  A1[User Wallet / Tuấn's DApp]:::user --> A2[TuấnChain JSON-RPC Endpoint]:::user

  %% === Layer 2 Core (TuấnChain) ===
  subgraph L2[TuấnChain - ZK Stack - Layer 2 Rollup]
    A2 --> B1[Sequencer 1]:::seq
    A2 --> B2[Sequencer 2]:::seq
    A2 --> B3[Sequencer N]:::seq
    B1 & B2 & B3 --> B4[Transaction Pool / Consensus]:::core
    B4 --> C1[Executor - Smart Contract Runtime]:::core
    C1 --> C2[State Keeper - L2 State DB]:::core
    C2 --> C3[Block Producer]:::core
    C3 --> C4[Shared Prover - Prover-as-a-Service]:::prover
    C4 --> C5[zkProof - .proof file]:::prover
    C3 --> DA1[Data Availability Layer]:::da
    DA1 --> DA2[Celestia / Avail / EigenDA]:::da
  end

  %% === Ethereum Layer 1 ===
  subgraph L1[Ethereum - Layer 1]
    C5 --> D1[Verifier Contract on Ethereum]:::eth
    D1 --> D2[Finalize Valid Block]:::eth
    D1 --> D3[Update State Root on Ethereum]:::eth
    A1 --> E1[Bridge Contract - TuấnChain ↔ Ethereum]:::eth
    E1 --> E2[zkSync Bridge / Ethereum Bridge]:::eth
    E2 --> E3[Token Escrow / Withdraw Finality]:::eth
  end

  %% === Layer 3 Integration (via SDK) ===
  subgraph L3SDK[Tuấn SDK - Layer 3 Integration]
    F1[L3RollupClient]:::sdk --> B4
    F2[L3RPCProvider]:::sdk
    F3[L2BridgeAPI]:::sdk --> E1
    F4[StateSyncAPI]:::sdk --> D3
    F5[ProofAggregator]:::sdk --> D1
    F6[RelayerClient]:::sdk --> B4
  end

  G1[DApp on Layer 3]:::user --> F2

  %% === Styling ===
  classDef core fill:#f2f2ff,stroke:#aaa,stroke-width:1px;
  classDef prover fill:#fff0f6,stroke:#c26,stroke-width:1px;
  classDef da fill:#e6f7ff,stroke:#0aa,stroke-width:1px;
  classDef eth fill:#fffbe6,stroke:#999,stroke-width:1px;
  classDef sdk fill:#e6fffb,stroke:#0aa,stroke-width:1px;
  classDef seq fill:#e0f7fa,stroke:#088,stroke-width:1px;
  classDef user fill:#f6ffed,stroke:#6c3,stroke-width:1px;

  class A1,A2,G1 user;

:::
