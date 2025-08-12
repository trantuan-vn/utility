::: mermaid
graph TD

    %% ========== PoW ==========
    subgraph PoW Proof of Work
        A1[🎯 Miner chuẩn bị block]
        A2[🔢 Tìm nonce hợp lệ hash]
        A3{✅ Đúng nonce?}
        A4[📡 Broadcast block]
        A5[✅ Node khác xác minh block]
        A6[📦 Block added to chain]

        %% Tấn công PoW
        A7[💀 51% Attack<br>Miner chiếm >50% hashrate]
        A8[⛏ Tạo chain riêng<br>ẩn với mạng]
        A9[⚠️ Gửi giao dịch thật trên chain chính]
        A10[🪓 Đợi xác nhận → công bố chain riêng]
        A11[🔁 Chain fork<br>→ Double spend thành công]
        A12[🛡 Phòng ngừa: Hashpower phân tán, merged mining]

        A1 --> A2 --> A3
        A3 -- "❌ Sai" --> A2
        A3 -- "✅ Đúng" --> A4 --> A5 --> A6

        A2 --> A7
        A7 --> A8 --> A9 --> A10 --> A11 --> A12
    end

    %% ========== PoS ==========
    subgraph PoS Proof of Stake
        B1[💰 Staker stake token]
        B2[🎲 Protocol chọn validator]
        B3[🧱 Validator tạo block]
        B4[📡 Broadcast block]
        B5[🗳 Nhận vote / attestation]
        B6{✅ Đủ 2/3 vote?}
        B7[📦 Block được finalize]

        %% Tấn công PoS
        B8[💀 51% Stake Attack]
        B9[⚔️ Tạo block gian lận<br>hoặc không vote]
        B10[📉 Mạng thiếu finality,<br>hoặc fork]
        B11[🔥 Bị Slash: mất stake]
        B12[🛡 Phòng ngừa: Slashing, lock-time, decentralization]

        B1 --> B2 --> B3 --> B4 --> B5 --> B6
        B6 -- "✅ Có" --> B7
        B6 -- "❌ Thiếu" --> B5

        B2 --> B8
        B8 --> B9 --> B10 --> B11 --> B12
    end

    %% ========== BFT ==========
    subgraph BFT CometBFT / HotStuff
        C1[📨 Proposer đề xuất block]
        C2[🔎 Validator nhận block]
        C3[🗳 Pre-Vote round]
        C4[🗳 Pre-Commit round]
        C5{✅ ≥2/3 Pre-Commit?}
        C6[📦 Finalize block]

        %% Tấn công BFT
        C7[💀 ≥1/3 Validator bị lỗi hoặc phản bội]
        C8[🤐 Không vote / gửi vote sai]
        C9[⏸ Consensus stalled,<br>không tạo block mới]
        C10[⚒ Governance can kick/rotate validator]
        C11[🛡 Phòng ngừa: staking + rotation + governance]

        C1 --> C2 --> C3 --> C4 --> C5
        C5 -- "✅ Đạt" --> C6
        C5 -- "❌ Không đạt" --> C1

        C4 --> C7 --> C8 --> C9 --> C10 --> C11
    end
:::