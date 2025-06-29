::: mermaid
gantt
    title Roadmap tối ưu xây dựng hệ sinh thái ví Smart Account và zkRollup (Việt Nam & Quốc tế)
    dateFormat  YYYY-MM-DD
    axisFormat  %b %Y
    excludes    weekends
    %% (`excludes` accepts specific dates in YYYY-MM-DD format, days of the week ("sunday") or "weekends", but not the word "weekdays".)

    section ⬤ Chú giải trạng thái (Legend)
        ✓ Đã hoàn thành (done): done_label, 2025-08-15, 3d
        ● Đang thực hiện (active): active_label, 2025-08-15, 3d
        ○ Chưa bắt đầu (default): default_label, 2025-08-15, 3d

    section Giai đoạn 0 - Chuẩn bị hệ sinh thái (Pre-core)
        R0.1 - Nghiên cứu thị trường & phân khúc người dùng: active, r0_1, 2025-06-15, 15d
        R0.2 - Phân tích pháp lý & định hướng tuân thủ: active, r0_2, 2025-06-15, 20d
        R0.3 - Thiết kế kiến trúc hệ sinh thái: crit, active, r0_3, 2025-06-20, 30d
        R0.4 - Ký hợp tác audit, KYC, đối tác pháp lý: r0_4, 2025-07-01, 60d
        R0.5 - Xây dựng tài liệu chuẩn + onboarding dev: r0_5, 2025-06-25, 20d
        R0.6 - Triển khai DevOps stack: r0_6, 2025-07-01, 30d

    section Giai đoạn 1 - Ví Smart Account (Core - Phase 1)
        R1.1 - Thiết kế ví Smart Account: r1_1, 2025-07-01, 30d
        R1.2 - Phát triển contract AA + bundler: crit, r1_2, after r1_1, 45d
        R1.3 - Giao diện UX + recovery flow: r1_3, after r1_1, 45d
        R1.4 - Tích hợp Paymaster, Guardian: crit, r1_4, after r1_2, 30d
        R1.5 - Audit & kiểm thử UX: r1_5, after r1_4, 20d
        R1.6 - Viết tài liệu SDK + OpenAPI: r1_6, after r1_2, 25d
        R1.7 - Testnet ví + simulate flow: r1_7, after r1_5, 20d
        R1.8 - Tổ chức user test nội bộ: r1_8, after r1_7, 10d

    section Giai đoạn 2 - zkRollup Core Infra (Core - Phase 2)
        R2.1 - Thiết kế zkRollup modular: r2_1, 2025-10-01, 30d
        R2.2 - Xây các module sequencer + DA layer: r2_2, after r2_1, 60d
        R2.3 - Xây các module Prover infra + zk circuits: crit, r2_3, after r2_1, 75d
        R2.4 - Tích hợp zkBridge với zkRollupVN: r2_4, after r2_2, 60d
        R2.5 - Benchmark + stress test prover: crit, r2_5, after r2_3, 30d
        R2.6 - Audit tổng thể Rollup: r2_6, after r2_5, 15d
        R2.7 - Observability + self-healing: r2_7, after r2_2, 45d
        R2.8 - Faucet, explorer, tx simulator: r2_8, after r2_2, 30d

    section Giai đoạn 3 - SDK & Devtools (Core - Phase 3)
        R3.1 - Thiết kế SDK chuẩn hoá: r3_1, 2026-03-01, 30d
        R3.2 - SDK (TypeScript, Dart, Rust): crit, r3_2, after r3_1, 30d
        R3.3 - Tài liệu + CI contributor: r3_3, after r3_2, 20d
        R3.4 - Hackathon Vietnam cùng với  grant: r3_4, after r3_3, 60d
        R3.5 - Template dApp mẫu: r3_5, after r3_2, 30d
        R3.6 - Portal dev + forum + metrics: r3_6, after r3_2, 30d

    section Giai đoạn 4 - Token hóa & ICO (Tiện ích - Phase 1)
        R4.1 - Legal readiness ở Singapore: r4_1, 2026-05-01, 20d
        R4.2 - Whitelist + testnet incentive: r4_2, after r4_1, 30d
        R4.3 - Token Launch (ICO/IEO): crit, r4_3, after r4_2, 20d
        R4.4 - Airdrop + staking campaign: r4_4, after r4_3, 20d
        R4.5 - Tokenomics dashboard: r4_5, 2026-04-15, 40d
        R4.6 - Mở rộng cộng đồng đa ngôn ngữ: r4_6, after r4_3, 20d
        R4.7 - Truyền thông lớn + AMA + podcast: r4_7, after r4_3, 30d

    section Giai đoạn 5 - Sàn giao dịch Rollup-native (Tiện ích - Phase 2)
        R5.1 - Thiết kế DEX Rollup-native: r5_1, 2026-06-16, 30d
        R5.2 - Xây AMM/Orderbook + routing: r5_2, after r5_1, 45d
        R5.3 - Cross-rollup bridge: crit, r5_3, after r5_2, 30d
        R5.4 - DEX Launch + chiến dịch testing: r5_4, after r5_3, 15d
        R5.6 - Attack simulation & audit kinh tế: r5_6, after r5_3, 15d
        R5.5 - Listing token DEX/CEX: r5_5, after r5_4, 20d

    section Giai đoạn 6 - DAO & mở rộng hệ sinh thái
        R6.1 - Xây dựng DAO cộng đồng + governance: crit, r6_1, after r5_4, 60d
        R6.2 - Incentive dApp tích hợp Rollup: r6_2, after r6_1, 60d
        R6.3 - Ví chính thức production: r6_3, after r6_1, 30d
        R6.4 - Bug bounty + Rollup Grants: r6_4, after r6_1, 45d
        R6.5 - Multi-chain support Cosmos/Solana: r6_5, after r6_1, 45d

    section Giai đoạn 7 - Quốc tế hóa & Layer 3
        R7.1 - Triển khai node tại Singapore, EU: r7_1, 2026-10-01, 45d
        R7.2 - Launch Layer 3 zkApp đầu tiên: crit, r7_2, after r7_1, 60d
        R7.3 - Giao thức tài chính L3 + incentive: r7_3, after r7_2, 60d
        R7.4 - Ra mắt Rollup-as-a-Service: r7_4, after r7_3, 45d
:::
