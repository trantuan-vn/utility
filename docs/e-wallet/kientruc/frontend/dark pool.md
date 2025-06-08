::: mermaid
flowchart TD
  subgraph User Side
    A[User Wallet]
    B[Order Encryptor + Commit Generator]
  end

  subgraph Gateway Layer
    C[Private Transaction Relay<br>API or P2P]
    X[API Gateway]
  end

  subgraph Dark Pool Core
    D[Order Manager<br>commit-reveal logic]
    E[Matching Engine<br>Batch + Rule-based Matching]
    F[ZK Circuit Builder<br>zkMatch circuit]
    G[Fee Manager<br>Gas subsidy, Anti-spam]
    H[Audit Logger Encrypted]
  end

  subgraph ZKP Layer
    I[ZK Verifier<br>Prove matching correctness]
  end

  subgraph Settlement Layer
    J[Settlement Contract<br/>on Ethereum / L2]
    K[Reveal Proof + Settle]
  end

  A --> B --> C --> D
  C --> X --> D
  D --> E
  E -->|Batch Order| F
  F --> I
  I --> K
  G --> D
  E --> H
  K --> J

  style A fill:#e3f2fd,stroke:#1565c0
  style B fill:#b3e5fc,stroke:#0277bd
  style C fill:#fff3e0,stroke:#ef6c00
  style X fill:#ede7f6,stroke:#673ab7
  style D fill:#dcedc8,stroke:#689f38
  style E fill:#f0f4c3,stroke:#afb42b
  style F fill:#ede7f6,stroke:#512da8
  style G fill:#ffe0b2,stroke:#ef6c00
  style H fill:#f3e5f5,stroke:#8e24aa
  style I fill:#e1bee7,stroke:#7b1fa2
  style J fill:#c8e6c9,stroke:#2e7d32
  style K fill:#d1c4e9,stroke:#5e35b1

:::