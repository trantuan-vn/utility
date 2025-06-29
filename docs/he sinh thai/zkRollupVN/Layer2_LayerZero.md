::: mermaid
flowchart TB
    %% USER SIDE - OTHER CHAINS
    subgraph OtherChains["🌍 User từ Chain khác (Avalanche, BNB, Arbitrum...)"]
        UserWallet["User Wallet"]
        EndpointX["LayerZero Endpoint (Chain X)"]
        UserAppX["User DEX Proxy<br>Smart Contract<br>(Chain X)"]
        UserWallet --> UserAppX
        UserAppX --> EndpointX
    end

    %% CORE - ETHEREUM L1 + zkRollup
    subgraph EthereumL1["⛓️ Ethereum Mainnet (L1)"]
        LZEndpointEth["LayerZero Endpoint (Ethereum)"]
        ZKVerifier["ZK Verifier<br>(Smart Contract)"]
        BridgeWBTC["WBTC ERC-20<br>Custodian Bridge"]
        LZEndpointEth --> ZKDEX
    end

    subgraph ZKRollup["⚙️ zkRollup Layer 2 (DEX Layer)"]
        ZKDEX["DEX Logic<br>AMM / Orderbook"]
        PoolETH["Pool ETH / USDC"]
        PoolWBTC["Pool WBTC"]
        ZKDEX --> PoolETH
        ZKDEX --> PoolWBTC
    end

    %% OFF-CHAIN INFRA
    subgraph OffChainInfra["🛰️ Off-chain Infra"]
        Oracle["LayerZero Oracle<br>Block Header Relay"]
        Relayer["LayerZero Relayer<br>Merkle Proof Relay"]
    end

    %% BTC SIDE
    subgraph Bitcoin["₿ Bitcoin Network"]
        BTCUser["BTC Holder"]
        Custodian["BitGo Custodian<br>(mint WBTC)"]
        BTCUser --> Custodian
        Custodian --> BridgeWBTC
    end

    %% FLOWS
    UserAppX -- message --> EndpointX
    EndpointX -- log event --> Oracle
    EndpointX -- event proof --> Relayer
    Oracle --> LZEndpointEth
    Relayer --> LZEndpointEth
    LZEndpointEth --> ZKDEX

    ZKDEX -- update result --> LZEndpointEth
    LZEndpointEth --> EndpointX
    EndpointX --> UserAppX
    UserAppX --> UserWallet
:::