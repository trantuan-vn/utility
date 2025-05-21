::: mermaid
graph TD
    A[E-wallet + Trading Engine]

    A --> B1(Phí giao dịch)
    B1 --> B1a[Taker/Maker Fee]
    B1 --> B1b[Phí theo phần trăm]

    A --> B2(Phí nạp/rút tiền)
    B2 --> B2a[Phí rút fiat]
    B2 --> B2b[Phí blockchain]

    A --> B3(Spread lợi nhuận)
    B3 --> B3a[Market Making]
    B3 --> B3b[Chênh lệch giá mua/bán]

    A --> B4(Phí dịch vụ nâng cao)
    B4 --> B4a[Ví lưu ký an toàn]
    B4 --> B4b[Tài khoản VIP/Premium]

    A --> B5(Gói subscription)
    B5 --> B5a[Cảnh báo giá]
    B5 --> B5b[Bot giao dịch / Auto-trade]

    A --> B6(Earn / Lending / Staking)
    B6 --> B6a[Thu phí quản lý tài sản]
    B6 --> B6b[Cho vay và thu lãi]

    A --> B7(Tokenomics)
    B7 --> B7a[Phát hành token riêng]
    B7 --> B7b[Giảm phí, thưởng, staking]

    A --> B8(Dịch vụ API cho B2B)
    B8 --> B8a[Thu phí tích hợp API]
    B8 --> B8b[Hỗ trợ ví/trading-as-a-service]

    A --> B9(Bridge/Cross-chain Fee)
    B9 --> B9a[Phí swap tài sản]
    B9 --> B9b[Phí sử dụng bridge]

    style A fill:#f9f,stroke:#333,stroke-width:2px

:::