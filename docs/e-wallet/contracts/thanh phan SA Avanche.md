::: mermaid
flowchart TD
    subgraph AvalancheC["Avalanche C-Chain"]
        EntryPoint["📍 EntryPoint.sol<br>Smart Contract trung tâm"]
        SA["🧠 Smart Account<br>(ERC-4337 Wallet Contract)"]
        Factory["🏗️ Account Factory<br>(Tạo Smart Account mới)"]
        Paymaster["💰 Paymaster (tuỳ chọn)<br>Tài trợ phí gas"]
    end

    subgraph Infra["Off-chain Infrastructure"]
        Bundler["📦 Bundler<br>(Ký hợp lệ & gửi UserOperation)"]
        Frontend["🌐 Frontend DApp<br>(user interface)"]
        User["🙋‍♂️ User<br>(ký off-chain bằng WebAuthn/passkey)"]
    end

    %% Interaction lines
    User -->|Sign UserOperation| Frontend
    Frontend -->|Gửi UserOperation| Bundler
    Bundler -->|Gửi tx đến EntryPoint| EntryPoint
    EntryPoint -->|Gọi execute| SA
    EntryPoint -->|Check gas tài trợ| Paymaster
    Frontend -->|Tạo SA| Factory
    Factory -->|Triển khai ví| SA

    %% Arrow style
    classDef contract fill:#f6f6f6,stroke:#333,stroke-width:1px;
    class EntryPoint,SA,Factory,Paymaster contract;
:::