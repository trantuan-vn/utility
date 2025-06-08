::: mermaid
graph TD

  %% === User Layer ===
  A1[User Wallet / Tuấn's DApp] --> A2[TuấnChain JSON-RPC Endpoint]

  %% === Layer 2 Core ===
  A2 --> B1[Sequencer 1]
  A2 --> B2[Sequencer 2]
  A2 --> B3[Sequencer N]

  B1 & B2 & B3 --> B4[Transaction Pool / Consensus]

  B4 --> C1[Executor - Smart Contract Runtime]
  C1 --> C2[State Keeper - L2 State DB]
  C2 --> C3[Block Producer]
  C3 --> C4[Shared Prover - Prover-as-a-Service]
  C4 --> C5[zkProof - .proof file]

  %% === Data Availability Layer ===
  C3 --> DA1[Data Availability Layer]
  DA1 --> DA2[Celestia / Avail / EigenDA]

  %% === Ethereum Layer 1 ===
  C5 --> D1[Verifier Contract on Ethereum]
  D1 --> D2[Finalize Valid Block]
  D1 --> D3[Update State Root on Ethereum]

  %% === Bridge ===
  A1 --> E1[Bridge Contract - TuấnChain ↔ Ethereum]
  E1 --> E2[zkSync Bridge / Ethereum Bridge]
  E2 --> E3[Token Escrow / Withdraw Finality]

  %% === Grouping and Styling ===
  classDef layer2 fill:#f2f2ff,stroke:#aaa,stroke-width:1px;
  classDef layer1 fill:#fffbe6,stroke:#999,stroke-width:1px;
  classDef external fill:#e6fffa,stroke:#666,stroke-width:1px;
  classDef da fill:#e6f7ff,stroke:#0aa,stroke-width:1px;

  class A2,B1,B2,B3,B4,C1,C2,C3 layer2;
  class C4,C5,D1,D2,D3,E1,E2,E3 layer1;
  class DA1,DA2 da;
  class A1 external;
:::
