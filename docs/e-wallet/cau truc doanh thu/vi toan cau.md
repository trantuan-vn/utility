::: mermaid
flowchart LR

  %% MetaMask Block
  subgraph MetaMask [MetaMask hiện tại]
    A1[User gửi giao dịch] --> B1[Swap Token]
    B1 --> C1[Aggregator 1inch, 0x...]
    C1 --> D1[MetaMask thu phí swap<br>0.875% built-in]
    A1 --> E1[Buy Crypto Ramp, Moonpay]
    E1 --> F1[Phí fiat on-ramp 2~5% chia sẻ]
    A1 --> G1[Bridge cross-chain beta]
    G1 --> H1[Phí bridge + MetaMask mark-up]
  end

  %% Global Smart Wallet Block
  subgraph GlobalSmartWallet [Ví Toàn Cầu mở rộng của bạn]
    A2[User tương tác ví]
    
    A2 --> B2[Swap/Bridge đa chuỗi<br>Aggregator + LayerZero]
    B2 --> C2[Phí platform 0.3~1% chia sẻ]

    A2 --> D2[Premium Features]
    D2 --> D2a[Multisig<br>Kế thừa<br>Recovery nâng cao]
    D2a --> D2b[Phí đăng ký<br>$5–10/month]

    A2 --> E2[Smart Recovery cardId + SMS]
    E2 --> E2a[Phí mỗi lần phục hồi<br>$5~20/lần]

    A2 --> F2[gToken Issuance]
    F2 --> F2a[Mint/Burn Fee<br>0.1%]
    F2 --> F2b[Staking, Lending, TVL-based score]

    A2 --> G2[Plugin Marketplace]
    G2 --> G2a[DApp trả phí để tích hợp]
    G2 --> G2b[Chia phí với nền tảng]

    A2 --> H2[Insurance-as-a-Service]
    H2 --> H2a[Phí bảo hiểm Web3<br>Flat fee / % TVL]

    A2 --> I2[Fiat onramp Ramp, Transak]
    I2 --> I2a[Hoa hồng chia với đối tác]

    A2 --> J2[On-chain behavioral analytics]
    J2 --> J2a[Ẩn danh hoá<br>Dữ liệu → insight]
    J2a --> J2b[Doanh thu từ phân tích<br>tổng hợp dòng vốn]
  end

  %% Legend
:::