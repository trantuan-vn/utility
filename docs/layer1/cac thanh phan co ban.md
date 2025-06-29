::: mermaid
graph TB
    A[Client/Wallet] --> B[RPC/API Node]
    B --> C[Full Node]
    C --> D[Networking Layer]
    C --> E[Consensus Layer]
    C --> F[Execution Layer]
    C --> G[State Storage]
    F --> G
    C --> H[Data Availability Layer]
    C --> I[Block Producer / Validator]
    I --> E
    I --> H
    E --> J[Slashing & Incentive Module]
    J --> K[Staking / Delegation Module]
    K --> L[Accounts & Balances]

    subgraph Developer Tools
        M[Smart Contract VM EVM, WASM...]
        N[Contract Deployment Tooling]
    end
    M --> F
    N --> M

:::