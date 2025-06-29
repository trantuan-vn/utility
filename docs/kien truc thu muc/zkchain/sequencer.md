::: mermaid
graph TD
    subgraph User Interaction
        A1[🧑‍💻 User Wallet Metamask]
        A2[🌐 RPC Endpoint Infura, Alchemy]
    end

    subgraph Sequencer Node
        B1[📥 Transaction API Server]
        B2[📦 Mempool Tx Pool]
        B3[⚙️ zkEVM Executor]
        B4[🧾 State Merkle Tree Updater]
        B5[📊 Fee / Gas Estimator]
        B6[🧱 Batch Builder]
        B7[📤 Committer Send to Prover]
    end

    subgraph External Systems
        C1[🧠 Prover Node]
        C2[📜 Verifier Smart Contract on Ethereum]
    end

    A1 --> A2 --> B1 --> B2
    B2 --> B3 --> B4
    B3 --> B5
    B4 --> B6 --> B7
    B7 --> C1
    C1 --> C2
:::