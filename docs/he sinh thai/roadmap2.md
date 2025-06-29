::: mermaid
gantt
    title Roadmap tối ưu xây dựng hệ sinh thái ví Smart Account và zkRollup (Việt Nam & Quốc tế)
    dateFormat  YYYY-MM-DD
    axisFormat  %b %Y

    section ⬤ Chú giải trạng thái (Legend)
        ✓ Đã hoàn thành (done): done, desclabel1, 2025-08-15, 3d
        ● Đang thực hiện (active): active, desclabel2, 2025-08-15, 3d
        ○ Chưa bắt đầu (default): desclabel3, 2025-08-15, 3d

    section Giai đoạn 0 - Chuẩn bị
        R0.1 - Nghiên cứu thị trường & phân khúc người dùng: active, 2025-06-15, 15d
        R0.2 - Phân tích pháp lý & định hướng tuân thủ: active, 2025-06-15, 20d
        R0.3 - Thiết kế kiến trúc hệ sinh thái (modular + multi-chain): active, 2025-06-20, 30d
        R0.4 - Ký hợp tác audit, KYC, đối tác pháp lý: 2025-07-01, 60d
        R0.5 - Xây dựng tài liệu chuẩn + repo + flow onboarding dev: 2025-06-25, 20d
        R0.6 - Triển khai DevOps stack (CI/CD, monitoring, Vault, alert): 2025-07-01, 30d

    section Giai đoạn 1 - Ví Smart Account (MVP)
        R1.1 - Thiết kế ví Smart Account (ERC-4337 / EIP-7702): 2025-07-01, 30d
        R1.2 - Phát triển contract AA + bundler core: 2025-07-15, 45d
        R1.3 - Giao diện UX ví + luồng recovery + onboarding: 2025-07-20, 45d
        R1.4 - Tích hợp Paymaster, Guardian, Social Recovery: 2025-08-10, 30d
        R1.5 - Audit bảo mật & kiểm thử UX thực tế người dùng: 2025-09-15, 20d
        R1.6 - Viết tài liệu SDK + chuẩn tích hợp open API: 2025-08-20, 25d
        R1.7 - Testnet ví + simulate các flow cơ bản: 2025-09-01, 20d
        R1.8 - Tổ chức user test nội bộ với feedback real user: 2025-09-20, 10d

    section Giai đoạn 2 - zkRollup Core Infrastructure
        R2.1 - Thiết kế zkRollup (modular có sequencer, prover, DA): 2025-10-01, 30d
        R2.2 - Build sequencer + Data Availability layer (DAS/celestia...): 2025-10-15, 60d
        R2.3 - Prover infra + zk circuits tối ưu cho EVM tx: 2025-11-15, 75d
        R2.4 - Tích hợp zkBridge (Ethereum, LayerZero...): 2025-12-01, 60d
        R2.5 - Benchmark nội bộ + stress test prover: 2026-01-15, 30d
        R2.6 - Audit tổng thể Rollup + bugfix: 2026-02-15, 15d
        R2.7 - Triển khai observability & self-healing infra: 2026-01-01, 45d
        R2.8 - Build faucet, explorer, tx simulator: 2026-01-10, 30d

    section Giai đoạn 3 - SDK & Devtools
        R3.1 - Thiết kế SDK chuẩn hoá flow dApp & dUser: 2026-03-01, 30d
        R3.2 - SDK (TypeScript, Dart/Flutter, Rust...): 2026-03-15, 30d
        R3.3 - Tài liệu chi tiết + CI cho open source contributor: 2026-04-01, 20d
        R3.4 - Grant & Hackathon Vietnam (target sinh viên/dev web2): 2026-04-15, 60d
        R3.5 - Template dApp mẫu voting, swap, NFT minting...: 2026-04-01, 30d
        R3.6 - Portal dev + forum + metrics public dashboard: 2026-03-25, 30d

    section Giai đoạn 4 - Token & cộng đồng
        R4.1 - Legal readiness + đăng ký pháp lý ở Singapore: 2026-05-01, 20d
        R4.2 - Chiến dịch whitelist + testnet incentive: 2026-05-10, 30d
        R4.3 - Token Launch (ICO/IEO + LBP nếu cần): 2026-06-10, 20d
        R4.4 - Airdrop + staking campaign: 2026-06-15, 20d
        R4.5 - Tokenomics dashboard, vesting explorer: 2026-04-15, 40d
        R4.6 - Mở rộng cộng đồng đa ngôn ngữ (vi/en/zh): 2026-04-20, 20d
        R4.7 - Chiến dịch truyền thông lớn, series AMA, podcast: 2026-06-01, 30d

    section Giai đoạn 5 - DEX Rollup
        R5.1 - Thiết kế DEX Rollup-native (orderbook + AMM hybrid): 2026-06-16, 30d
        R5.2 - Xây dựng AMM/Orderbook, multi-fee routing: 2026-07-01, 45d
        R5.3 - Cross-rollup bridge (zkBridge/L0/IBC): 2026-07-15, 30d
        R5.4 - DEX launch + battle-tested campaign: 2026-08-15, 15d
        R5.5 - Listing token trên DEX/CEX (Gate, OKX...): 2026-08-10, 20d
        R5.6 - Attack simulation & economic audit DEX: 2026-08-01, 15d

    section Giai đoạn 6 - Hệ sinh thái mở rộng
        R6.1 - DAO vận hành cộng đồng + governance đầu tiên: 2026-08-01, 60d
        R6.2 - Incentive dApp bên thứ ba tích hợp Rollup: 2026-08-15, 60d
        R6.3 - Ví chính thức production + UX đa ngôn ngữ: 2026-09-15, 30d
        R6.4 - Bug bounty + Rollup Grants: 2026-09-10, 45d
        R6.5 - Multi-chain support Cosmos, Solana, L2 khác: 2026-09-20, 45d

    section Giai đoạn 7 - Quốc tế hóa & Layer 3
        R7.1 - Triển khai node Rollup tại Singapore, HK, EU: 2026-10-01, 45d
        R7.2 - Hợp tác launch Layer 3 zkApp trên hệ sinh thái: 2026-11-01, 60d
        R7.3 - Giao thức tài chính L3 + DeFi L3 incentive: 2026-11-15, 60d
        R7.4 - Ra mắt “Rollup-as-a-Service” cho Việt Nam & Đông Nam Á: 2026-12-01, 45d

:::
