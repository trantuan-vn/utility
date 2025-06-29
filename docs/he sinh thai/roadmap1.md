::: mermaid
gantt
title Roadmap xây dựng hệ sinh thái ví Smart Account và zkRollup cho Việt Nam
dateFormat  YYYY-MM-DD
axisFormat  %b %Y

section ⬤ Chú giải trạng thái (Legend)
    ✓ Đã hoàn thành (done): done, desclabel1, 2025-08-15, 3d
    ● Đang thực hiện (active): active, desclabel2, 2025-08-15, 3d
    ○ Chưa bắt đầu (default): desclabel3, 2025-08-15, 3d

section Giai đoạn 0 - Chuẩn bị
    R0.1 - Nghiên cứu thị trường & nhu cầu người dùng: active, 2025-06-15, 15d
    R0.2 - Phân tích pháp lý & định hướng tuân thủ: active, 2025-06-15, 20d
    R0.3 - Thiết kế kiến trúc hệ sinh thái: active, 2025-06-20, 30d
    R0.4 - Khởi động hợp tác KYC, audit, đối tác pháp lý: 2025-07-01, 60d
    R0.5 - Xây dựng tài liệu chuẩn & repo chuẩn bị dev: 2025-06-25, 20d
    R0.6 - Chuẩn bị CI/CD, monitoring, devops: 2025-07-01, 30d

section Giai đoạn 1 - Ví Smart Account (MVP)
    R1.1 - Thiết kế ví Smart Account (ERC-4337 / EIP-7702): 2025-07-01, 30d
    R1.2 - Xây dựng contract AA + Bundler: 2025-07-15, 45d
    R1.3 - Phát triển frontend + UX ví: 2025-07-20, 45d
    R1.4 - Tích hợp AA, Paymaster, Social Recovery: 2025-08-10, 30d
    R1.5 - Audit bảo mật và kiểm thử ví: 2025-09-15, 15d
    R1.6 - Viết tài liệu SDK + hướng dẫn tích hợp ví: 2025-08-20, 25d
    R1.7 - Chuẩn bị testnet ví + simulate giao dịch: 2025-09-01, 20d

section Giai đoạn 2 - zkRollup Core Infrastructure
    R2.1 - Thiết kế kiến trúc Rollup (sequencer, prover, verifier): 2025-10-01, 30d
    R2.2 - Xây dựng sequencer & Data Availability layer: 2025-10-15, 60d
    R2.3 - Phát triển module zkProver + module zkVerifier: 2025-11-15, 75d
    R2.4 - Triển khai zkBridge kết nối Ethereum: 2025-12-01, 60d
    R2.5 - Testnet nội bộ & benchmarking: 2026-01-15, 30d
    R2.6 - Audit tổng thể zkRollup: 2026-02-15, 15d
    R2.7 - Giám sát performance + tối ưu prover infra: 2026-01-01, 45d
    R2.8 - Phát triển faucet + explorer cơ bản: 2026-01-10, 30d

section Giai đoạn 3 - SDK & Devtools
    R3.1 - Thiết kế SDK cho dApp tương thích Rollup: 2026-03-01, 30d
    R3.2 - Phát triển SDK (JavaScript, Flutter...): 2026-03-15, 30d
    R3.3 - Tài liệu & ví dụ tích hợp cho dev: 2026-04-01, 20d
    R3.4 - Tổ chức Grant/Hackathon thu hút developer: 2026-04-15, 60d
    R3.5 - Tạo template dApp mẫu open-source: 2026-04-01, 30d
    R3.6 - Thiết lập developer portal + forum: 2026-03-25, 30d

section Giai đoạn 4 - Token hóa và cộng đồng
    R4.1 - Chuẩn bị tài liệu pháp lý cho token: 2026-05-01, 20d
    R4.2 - Phát hành token dạng ICO/IEO hợp pháp: 2026-05-20, 25d
    R4.3 - Tích hợp token vào ví + zkRollup: 2026-06-10, 20d
    R4.4 - Ra mắt chương trình airdrop & staking: 2026-06-15, 20d
    R4.5 - Xây dựng whitepaper + tokenomics dashboard: 2026-04-15, 40d
    R4.6 - Mở cộng đồng Telegram/Discord chính thức: 2026-04-20, 10d
    R4.7 - Chạy chiến dịch truyền thông & AMA: 2026-06-01, 30d

section Giai đoạn 5 - Sàn giao dịch zkRollup
    R5.1 - Thiết kế DEX hỗ trợ Rollup (matching + liquidity): 2026-06-16, 30d
    R5.2 - Phát triển AMM/OrderBook DEX: 2026-07-01, 45d
    R5.3 - Tích hợp cross-rollup bridge: 2026-07-15, 30d
    R5.4 - Ra mắt DEX + chiến dịch cộng đồng: 2026-08-15, 15d
    R5.5 - Listing token trên các DEX/CEX khác: 2026-08-10, 20d
    R5.6 - Thử nghiệm attack simulation & stress test: 2026-08-01, 15d

section Giai đoạn 6 - Mở rộng hệ sinh thái
    R6.1 - Triển khai DAO quản trị hệ sinh thái: 2026-08-01, 60d
    R6.2 - Hỗ trợ dApp ngoài tích hợp Rollup: 2026-08-15, 60d
    R6.3 - Ra mắt ví chính thức + marketing lớn: 2026-09-15, 30d
    R6.4 - Chạy chương trình Bug Bounty + L2 Grants: 2026-09-10, 45d
    R6.5 - Triển khai multi-chain support: 2026-09-20, 45d
:::
