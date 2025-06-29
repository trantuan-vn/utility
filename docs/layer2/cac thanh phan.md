::: mermaid
flowchart TB
  User[👤 Người dùng Wallet]
  WebApp[💻 Frontend interface.uniswap.org]
  SDK[📦 JS SDK]
  RPC[🌐 JSON-RPC Infura, Alchemy]
  Subgraph[🔍 Subgraph The Graph]
  Contract[v3-core & periphery<br>Smart Contracts]
  Chain[🧱 Ethereum / Layer 2]
  Backend[🛠 API Proxy / Analytics]
  
  User --> WebApp --> SDK --> RPC --> Chain
  Chain --> Contract
  SDK --> Subgraph --> Backend
:::
