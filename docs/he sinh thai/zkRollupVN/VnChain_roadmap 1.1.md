::: mermaid
gantt
    title Roadmap zkRollupVN từ MVP đến Mainnet (bắt đầu từ 2025-07-01)
    dateFormat  YYYY-MM-DD
    axisFormat  %b %Y
    excludes    weekends

    section ⬤ Chú giải trạng thái (Legend)
        ✓ Đã hoàn thành (done): done, 2025-08-15, 7d
        ● Đang thực hiện (active): active, 2025-08-15, 7d
        ○ Chưa bắt đầu (default): default, 2025-08-15, 7d
        x Quan trọng (critical): crit, 2025-08-15, 7d

    section Giai đoạn 0 - Khởi động
        ✓ Nghiên cứu giải pháp zk phù hợp (zkEVM, STARK, R1CS): active, task0a, 2025-07-01, 30d
        ✓ Lập team & kiến trúc hạ tầng: active, task0b, 2025-07-15, 20d

    section Giai đoạn 1 - MVP Rollup cơ bản
        ● Viết prover + submit batch zk lên mạng Ethereum: crit, active, task1a, 2025-08-01, 30d
        ● Tạo sequencer đơn giản + state Merkle Tree trên L2: crit, active, task1b, 2025-08-15, 30d
        ○ Triển khai zkVerifier contract + Rollup.sol: crit, task1c, 2025-09-01, 20d
        ○ Thiết lập DEX Logic (AMM / Orderbook L2): task1d, 2025-09-10, 15d

    section Giai đoạn 2 - Smart Contract + Bridge + DA
        ○ Hỗ trợ Solidity zkEVM + zkCompiler cho lớp L3: crit, task2a, 2025-09-25, 30d
        ○ Viết cầu L1-L2 (L1Bridge.sol + L2Bridge): task2b, 2025-10-10, 25d
        ○ Tích hợp DA layer (Celestia/EigenDA): crit, task2c, 2025-10-20, 20d
        ○ Triển khai LayerZero Endpoint (ETH, ChainX): task2d, 2025-10-25, 20d
        ○ Tích hợp Oracle + Relayer LayerZero: task2e, 2025-10-30, 15d
        ○ Thiết lập DEX Proxy Contract trên các chain (BNB, Arbitrum, Avalanche...): task2f, 2025-11-01, 15d
        ○ Tích hợp cầu WBTC Custodian (BitGo): task2g, 2025-11-05, 10d

    section Giai đoạn 3 - Alpha Testnet (internal)
        ○ Viết smart wallet (AA) & paymaster trên testnet: crit, task3a, 2025-11-15, 30d
        ○ Cung cấp SDK cho DApp trên mạng testnet: task3b, 2025-11-20, 25d
        ○ Tích hợp circuit transfer, swap, register: crit, task3c, 2025-11-25, 25d
        ○ Test luồng cross-chain call từ ChainX → zkDEX: task3d, 2025-12-05, 15d
        ○ Test phản hồi L2 → ChainX (event log, proof): task3e, 2025-12-10, 10d

    section Giai đoạn 4 - Public Testnet (open builder)
        ○ Mở mạng testnet + explorer + faucet: crit, task4a, 2025-12-20, 20d
        ○ Rotating sequencer / DA testing trên testnet : task4b, 2025-12-25, 25d
        ○ DAO & proposal voting test trên testnet : task4c, 2026-01-10, 20d
        ○ Public Testnet LayerZero Relay + BTC wrapped test: task4d, 2026-01-15, 15d

    section Giai đoạn 5 - Mainnet Launch
        ○ Tokenomics + audit toàn bộ stack trên mainnet: crit, task5a, 2026-02-01, 25d
        🚀 Ra mắt zkRollupVN Mainnet: milestone, task5b, 2026-03-01, 1d
        ○ Hỗ trợ builder & L3 deploy lên VnChain: task5c, 2026-03-02, 30d
:::