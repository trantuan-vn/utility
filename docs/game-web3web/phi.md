::: mermaid
graph TD
    Game[Game Web3]
    Player[Người chơi]

    subgraph Phí thu của nhà phát hành
        NFTSales[Bán NFT vật phẩm]
        TxFees[Phí giao dịch trên Marketplace]
        TokenSales[Bán Token riêng của game]
        Ads[Quảng cáo & Tài trợ]
        Premium[Phí tính năng Premium]
        EventTickets[Bán vé tham gia sự kiện]
        WithdrawalFees[Phí gas / phí rút tiền]
    end

    Player -->|Chơi miễn phí| Game
    Player -->|Mua NFT vật phẩm| NFTSales
    Player -->|Mua Token| TokenSales
    Player -->|Giao dịch NFT trên marketplace| TxFees
    Player -->|Xem quảng cáo / tham gia event| Ads
    Player -->|Trả phí cho tính năng VIP| Premium
    Player -->|Mua vé sự kiện| EventTickets
    Player -->|Rút NFT/token ra ngoài| WithdrawalFees
:::