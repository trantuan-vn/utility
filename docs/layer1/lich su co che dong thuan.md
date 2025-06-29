::: mermaid
graph TD
    A[PoW Proof of Work<br>1990s-2008<br>Bitcoin]
    B[PoS Proof of Stake<br>2011–nay<br>Peercoin, Ethereum 2.0]
    C[PBFT Practical BFT<br>1999<br>Academic → Enterprise]
    D[Tendermint / CometBFT<br>2017–nay<br>Cosmos]
    E[HotStuff<br>2019<br>Aptos, Sui]
    F[Tower BFT<br>2020–nay<br>Solana]
    G[Snowman / Avalanche Consensus\n2020–nay]
    H[Nakamoto Consensus<br>2009<br>Bitcoin, L1 PoW]
    I[GRANDPA + BABE<br>2019–nay<br>Polkadot]
    J[PoH Proof of History<br>Solana<br>Kết hợp với BFT]
    K[MoveBFT LibraBFT<br>2021–nay<br>Diem → Aptos]
    L[Dag-based Consensus<br>2020–nay<br>IOTA, Tangle]

    H --> A
    A --> B
    B --> I
    B --> K

    C --> D
    C --> E
    C --> I
    D --> CometBFT[CometBFT rename từ Tendermint<br>2023]

    E --> K
    F --> J
    G --> AvalancheSubnet[Avalanche Subnets]

    L --> DAGNew[DAG mới<br>Sui, Tusk, Narwhal]

    style A fill:#f9f,stroke:#333,stroke-width:2px
    style D fill:#bbf,stroke:#333
    style E fill:#bbf,stroke:#333
    style F fill:#bbf,stroke:#333
    style G fill:#bfb,stroke:#333
    style B fill:#ff9,stroke:#333
    style H fill:#f99,stroke:#333
    style J fill:#acf,stroke:#333
:::