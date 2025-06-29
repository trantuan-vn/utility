::: mermaid
flowchart TD
    %% Ethereum Mainnet Layer 1
    subgraph Ethereum_L1["🟦 Ethereum Layer 1 Network"]
        direction TB

        %% Consensus & Execution Layer
        BEACON[Beacon Chain<br>PoS Consensus - Finality & Validator Coordination]
        EL[Execution Layer<br>Geth, Nethermind, Besu...]

        %% Core Contracts
        SC[System Contracts<br>Withdrawals, Staking, ETH Supply]
        DA[Data Availability<br>Calldata for Rollups on L1 blocks]

        %% Node Roles
        VN[Validator Node<br>Proposes/Attests Blocks]
        BN[Builder Node<br>Block Builder PBS/MEV-Boost]
        RP[Relay Node<br>Relays Built Blocks PBS infra]

        %% Rollup & Layer 2 Interaction
        R1[Rollup A e.g. Optimism]
        R2[Rollup B e.g. zkSync]
        R3[Rollup C e.g. Arbitrum]

        %% Cross-chain Bridge
        BRIDGE[Bridge Contracts<br>Canonical or External Bridges]
    end

    %% External Components
    subgraph L2_Infra["📦 Layer 2 & External Infra"]
        PROVER[ZK Prover off-chain]
        SEQ[Sequencer Optimistic / ZK]
        DA2[External DA e.g. Celestia, EigenDA]
    end

    %% Relationships
    BEACON --> VN
    VN -->|Block Proposals & Attestations| BEACON
    VN --> EL
    BN --> VN
    RP --> BN

    %% Rollup Flows
    R1 -->|Post State & Proof| EL
    R2 -->|Post zkProof| EL
    R3 -->|Post Batch| EL

    PROVER --> R2
    SEQ --> R1
    SEQ --> R3

    DA2 --> R2
    DA2 --> R3

    BRIDGE --> EL
    BRIDGE --> R1
    BRIDGE --> R2
    BRIDGE --> R3

    DA --> EL
:::