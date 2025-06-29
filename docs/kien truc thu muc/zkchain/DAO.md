::: mermaid
graph TD
    subgraph DAO Layer
        DAO[🏛️ DAO Contract Governance]
        GOV_Token[🔑 Governance Token ZKS?]
    end

    subgraph Core zkSync Contracts L1
        Rollup[📦 Rollup Contract]
        Verifier[✅ Proof Verifier]
        Bridge[🌉 L1 ↔ L2 Bridge]
        Config[⚙️ Protocol Config Contract]
        UpgradeMgr[🔁 Upgrade Manager]
    end

    subgraph Treasury & Incentives
        Treasury[💰 Treasury Wallet / Grants Contract]
        Airdrop[🎁 Airdrop Distributor]
        FeeConfig[💸 Fee Model]
    end

    GOV_Token --> DAO
    DAO --> UpgradeMgr
    DAO --> Treasury
    DAO --> Airdrop
    DAO --> FeeConfig

    UpgradeMgr --> Rollup
    UpgradeMgr --> Verifier
    UpgradeMgr --> Bridge
    UpgradeMgr --> Config
:::