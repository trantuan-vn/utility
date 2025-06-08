::: mermaid
sequenceDiagram
    participant Investor1 as Nhà đầu tư 1
    participant Investor2 as Nhà đầu tư 2
    participant InvestorN as Nhà đầu tư n
    participant zkSync as zkSync Layer 2
    participant L1 as Layer 1 Ethereum (Mainnet)
    participant TokenContract as Smart Contract Token BĐS

    Note over Investor1, InvestorN: N nhà đầu tư thực hiện trade lô token trên zkSync trong ngày

    Investor1->>zkSync: Gửi lệnh mua/bán token
    Investor2->>zkSync: Gửi lệnh mua/bán token
    InvestorN->>zkSync: Gửi lệnh mua/bán token

    zkSync->>zkSync: Khớp lệnh, ghi nhận thay đổi quyền sở hữu token trên Layer 2 (off-chain)

    Note over zkSync: Trading diễn ra nhanh, phí thấp, gần như tức thời

    alt Cuối ngày
        zkSync->>L1: Tổng hợp trạng thái cuối ngày (state root + proof)
        zkSync->>TokenContract: Gửi zk-proof xác nhận trạng thái token mới
        TokenContract->>L1: Cập nhật quyền sở hữu token theo trạng thái zkSync
    end

    Note over TokenContract, Investor1: Quyền sở hữu token trên Layer 1 được cập nhật chính thức

:::