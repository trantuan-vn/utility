::: mermaid
flowchart TD
    %% Main Layer 1 Network
    subgraph Layer1_Network["🟦 Layer 1 Network (YourChain)"]
        direction TB

        %% Validators and Core Infra
        V1[Validator Set<br>BFT/PoS consensus]
        SC[System Contracts<br>Staking, Rewards, Governance]
        DA[Data Availability Layer<br>Native or External like Celestia]
        MSG[Cross-chain Messaging Module<br>IBC-like / zkBridge]

        %% Node Roles
        SQ[Sequencer Node<br>Receives tx, builds rollup blocks]
        PR[Prover Node<br>Generates zkProof for rollup blocks]
        RP[Replayer Node<br>Executes verified messages on dest chains]

        %% Rollup Management
        RFactory[Rollup Factory<br>Smart Contract or On-chain VM]
        R1[Rollup A<br>dApp-specific Rollup]
        R2[Rollup B<br>CEX-as-a-rollup]
        R3[Rollup C<br>DeFi Appchain]

        %% Reward Flow
        TOKEN[Reward Pool / Inflation<br>Token Incentive]
        TOKEN --> SQ
        TOKEN --> PR
        TOKEN --> RP

        %% Execution Relationship
        SQ -->|Submit Tasks| PR
        PR -->|Submit zkProof| SC
        RP -->|Execute Verified Msgs| SC

        %% Rollup Deployment
        RFactory --> R1
        RFactory --> R2
        RFactory --> R3

        %% Rollups Interact With Layer1
        R1 --> SQ
        R1 --> PR
        R2 --> SQ
        R2 --> PR
        R3 --> SQ
        R3 --> PR

        %% Cross-chain Flow
        MSG --> RP
        MSG --> SC
    end

    %% External Environment
    subgraph ExternalChains["🌐 External Chains"]
        ETH[Ethereum / L1 khác]
        ZKS[zkRollups khác]
        COSMOS[Cosmos Chains]
    end

    MSG --> ETH
    MSG --> ZKS
    MSG --> COSMOS
:::