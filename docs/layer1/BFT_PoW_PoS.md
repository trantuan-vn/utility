::: mermaid
graph TD
    subgraph PoW["⚒️ Proof of Work"]
        A1[Node chuẩn bị block]
        A2[Giải bài toán hash nonce]
        A3{Giải đúng chưa?}
        A4[Broadcast block tới mạng]
        A5[Xác minh block]
        A6[Block được thêm vào chain]

        A1 --> A2 --> A3
        A3 -- "Sai" --> A2
        A3 -- "Đúng" --> A4 --> A5 --> A6
    end

    subgraph PoS["🟢 Proof of Stake"]
        B1[Node stake token]
        B2[Protocol chọn validator]
        B3[Validator tạo block]
        B4[Broadcast block]
        B5[Nhận vote / attestations từ các node khác]
        B6{Đủ 2/3 vote chưa?}
        B7[Block được chấp nhận]

        B1 --> B2 --> B3 --> B4 --> B5 --> B6
        B6 -- "Đủ" --> B7
        B6 -- "Thiếu" --> B5
    end

    subgraph BFT["🛡️ BFT Consensus (ví dụ: CometBFT)"]
        C1[Proposer đề xuất block]
        C2[Validator nhận block]
        C3[Vote: Pre-Vote]
        C4[Vote: Pre-Commit]
        C5{≥2/3 Pre-Commit?}
        C6[Block Finalized]

        C1 --> C2 --> C3 --> C4 --> C5
        C5 -- "Đạt" --> C6
        C5 -- "Không" --> C1
    end
:::