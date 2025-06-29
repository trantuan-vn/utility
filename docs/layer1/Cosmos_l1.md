::: mermaid
flowchart TB
    %% Core Stack
    subgraph Core_Framework["🧱 Core Stack"]
        CosmosSDK["cosmos-sdk<br>Cosmos SDK<br>Xây dựng chain"]
        CometBFT["cometbft<br>Đồng thuận BFT"]
        IBCGo["ibc-go<br>IBC Implementation Go"]
    end

    %% Smart Contract
    subgraph Smart_Contract["🧠 Smart Contracts"]
        CosmWasm["cosmwasm<br>Wasm-based Contract Engine"]
        Wasmd["wasmd<br>Chain tích hợp CosmWasm"]
    end

    %% Interchain Layer
    subgraph Cross_Chain["🌐 Interchain Communication"]
        IBCSpec["cosmos/ibc<br>IBC Specs"]
        ICA["interchain-accounts<br>Điều khiển account chain khác"]
        ICS["interchain-standards<br>ICS specs like ICS-20"]
        Axelar["axelar-core<br>Cross-chain bridge"]
    end

    %% Application Chains
    subgraph App_Projects["🏗️ Application Chains"]
        Gaia["gaia<br>Cosmos Hub ATOM"]
        Osmosis["osmosis<br>AMM DEX"]
        dydxV4["dYdX v4<br>Chain riêng trên Cosmos SDK"]
        Kujira["kujira<br>DeFi Chain"]
    end

    %% External Chains
    subgraph External_Blockchains["🌍 External Blockchains"]
        Ethereum["Ethereum<br>Smart Contract EVM"]
        Solana["Solana<br>Smart Contract BPF"]
    end

    %% Tools & Standards
    subgraph Tools["🛠️ Tools & Standards"]
        Awesome["awesome<br>Tổng hợp tài nguyên Cosmos"]
        TendermintRS["tendermint-rs<br>Rust Client"]
    end

    %% Relationships
    CosmosSDK --> CometBFT
    CosmosSDK --> IBCGo
    CosmosSDK --> CosmWasm
    CosmWasm --> Wasmd
    IBCGo --> IBCSpec
    IBCGo --> ICA
    IBCSpec --> ICS

    Gaia --> CosmosSDK
    Osmosis --> CosmosSDK
    dydxV4 --> CosmosSDK
    Kujira --> CosmosSDK

    Awesome --> CosmosSDK
    Awesome --> CosmWasm
    Awesome --> IBCGo
    Awesome --> CometBFT

    %% Cross-chain to external
    Axelar --> Ethereum
    Axelar --> Solana
    Axelar --> IBCGo
    Axelar --> CosmWasm

    CosmWasm --> Ethereum

:::