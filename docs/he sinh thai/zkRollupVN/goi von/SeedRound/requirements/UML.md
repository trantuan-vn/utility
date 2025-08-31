<pre id="cannon" class="mermaid">
classDiagram
    %% Web App (Next.js)
    class WebApp {
        +SwapPage
        +LiquidityPage
        +FarmingPage
        +StakingPage
        +AnalyticsPage
        +LaunchpadPage
        +GovernancePage
        +ProfilePage
        +AdminDashboard
        +StateManagement(Zustand)
        +DataFetching(SWR/ReactQuery)
        +Web3Integration(wagmi, viem, ethers)
        +Auth(NextAuth)
        +SEO(SSG/ISR/SSR)
        +WebSocket
    }

    %% Mobile App (React Native)
    class MobileApp {
        +Navigation(ReactNavigation)
        +StateManagement(Zustand/Redux)
        +Web3Integration(WalletConnect, MetaMask, viem)
        +ResponsiveUI
        +MultiLanguage
        +WebSocket
    }

    %% Backend (Rust)
    class Backend {
        +API(REST/WebSocket)
        +DB(PostgreSQL/MongoDB)
        +Security(JWT, Encryption)
        +Services(Swap, Liquidity, Farming, Staking, Analytics, Governance, Launchpad)
        +BlockchainIntegration(RPC/Web3)
    }

    %% Smart Contracts
    class SmartContracts {
        +Solidity(Ethereum/BSC)
        +Rust(Solana/Cosmos)
        +Security(Audit, Reentrancy, Overflow, AccessControl)
        +Events
    }

    %% Database Entities
    class User {
        +id
        +address
        +email
        +profile
        +settings
    }
    class Pool {
        +id
        +chain
        +tokens
        +TVL
        +APR
        +volume
    }
    class Transaction {
        +id
        +user_id
        +type
        +status
        +amount
        +timestamp
    }
    class Farm {
        +id
        +pool_id
        +APR
        +rewards
    }
    class Proposal {
        +id
        +title
        +status
        +votes
    }
    class Launchpad {
        +id
        +token
        +info
        +participants
    }

    %% Relationships
    WebApp "1" -- "1" Backend : API/WebSocket
    MobileApp "1" -- "1" Backend : API/WebSocket
    Backend "1" -- "1..*" SmartContracts : RPC/Web3
    Backend "1" -- "1..*" User : manages
    Backend "1" -- "1..*" Pool : manages
    Backend "1" -- "1..*" Transaction : manages
    Backend "1" -- "1..*" Farm : manages
    Backend "1" -- "1..*" Proposal : manages
    Backend "1" -- "1..*" Launchpad : manages
    User "1" -- "0..*" Transaction : performs
    User "1" -- "0..*" Pool : providesLiquidity
    User "1" -- "0..*" Farm : stakes
    User "1" -- "0..*" Proposal : votes
    User "1" -- "0..*" Launchpad : participates
    Pool "1" -- "0..*" Farm : has
</pre>