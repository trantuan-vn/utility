::: mermaid
graph TD
    A[User Wallet]
    A --> B[Swap UI / Bridge UI]
    B --> C[Smart Contract / Aggregator]
    C --> D[DEX / Bridge Protocol LayerZero, Orbiter, etc]
    C --> E[Fee Collector Contract]
    E --> F[Treasury / Revenue Account]
:::