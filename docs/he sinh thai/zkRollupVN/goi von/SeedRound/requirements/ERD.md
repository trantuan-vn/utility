<h1>ERD: DEX + FARM + Governance</h1>
<h2>Sơ đồ: </h2>
<pre id="cannon" class="mermaid">
erDiagram

    USER {
        varchar id PK
        varchar wallet_address
        varchar email
        varchar username
        varchar role
        timestamp created_at
        timestamp updated_at
    }

    POOL {
        varchar id PK
        varchar chain
        varchar token_a
        varchar token_b
        int reserve_a
        int reserve_b
        int fee
        varchar status
        timestamp created_at
        timestamp updated_at
    }

    TRANSACTION {
        varchar id PK
        varchar user_id FK
        varchar pool_id FK
        varchar type
        int amount_in
        int amount_out
        varchar token_in
        varchar token_out
        varchar tx_hash
        varchar chain
        timestamp created_at
        varchar status
    }

    FARM {
        varchar id PK
        varchar pool_id FK
        varchar chain
        int total_staked
        int reward_rate
        varchar status
        timestamp created_at
        timestamp updated_at
    }

    STAKE {
        varchar id PK
        varchar user_id FK
        varchar farm_id FK
        int amount
        timestamp staked_at
        timestamp unstaked_at
        varchar status
    }

    GOVERNANCE_PROPOSAL {
        varchar id PK
        varchar title
        varchar description
        varchar proposer_id FK
        varchar status
        timestamp created_at
        timestamp updated_at
    }

    VOTE {
        varchar id PK
        varchar proposal_id FK
        varchar user_id FK
        varchar vote_type
        timestamp voted_at
    }

    USER ||--o{ TRANSACTION : "makes"
    USER ||--o{ STAKE : "stakes"
    USER ||--o{ VOTE : "votes"
    USER ||--o{ GOVERNANCE_PROPOSAL : "proposes"
    POOL ||--o{ TRANSACTION : "has"
    POOL ||--o{ FARM : "provides"
    FARM ||--o{ STAKE : "contains"
    GOVERNANCE_PROPOSAL ||--o{ VOTE : "receives"
</pre>
<h2>Giải thích các thực thể chính</h2>
<ul>
<li><strong>USER</strong>: Người dùng, quản lý thông tin ví, xác thực, phân quyền.</li>
<li><strong>POOL</strong>: Pool thanh khoản, lưu trữ thông tin token, chain, trạng thái.</li>
<li><strong>TRANSACTION</strong>: Giao dịch swap, add/remove liquidity, liên kết user và pool.</li>
<li><strong>FARM</strong>: Yield farming, staking, liên kết pool.</li>
<li><strong>STAKE</strong>: Thông tin staking của user vào farm.</li>
<li><strong>GOVERNANCE_PROPOSAL</strong>: Đề xuất quản trị, on-chain voting.</li>
<li><strong>VOTE</strong>: Phiếu bầu của user cho đề xuất quản trị.</li>
</ul>
<p><strong>Lưu ý:</strong> ERD này là nền tảng cho thiết kế DB (PostgreSQL/MongoDB) và tích hợp smart contracts đa chuỗi (Solidity/Rust).</p>
