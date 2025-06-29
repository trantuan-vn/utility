User Action
  ├─ Deposit → L1Bridge.sol
  ├─ Transfer → Sequencer → SMT
  ├─ Withdraw → Sequencer → Rollup
  ├─ ForcedExit → Rollup
  └─ RegisterAccount → State Tree

System Engine
  ├─ Batcher
  ├─ Prover
  ├─ Submitter
  └─ DA Logger

Ethereum L1
  ├─ Rollup.sol
  ├─ ZKVerifier.sol
  ├─ L1Bridge.sol
  └─ DataCommitment.sol

Extensions
  ├─ Oracle (State Sync)
  ├─ zkBridge
  └─ Challenge / Delay Queue

1. Danh sách các repo zkSync cần dùng và mục đích tùy biến
| Repository                                                                                         | Mục đích                                                                                  | Customize gì để phù hợp mô hình bạn?                                                                                                                                            |
| -------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| [`zksync-era`](https://github.com/matter-labs/zksync-era)                                          | **Repo chính**: orchestrate full Rollup infra (node, sequencer, state, prover, contracts) | ✳️ Gắn thêm `ZKDEX` logic của bạn (AMM/orderbook) vào phần `contracts/` <br>✳️ Sửa `Sequencer` để mở nhiều node join (cluster B) <br>✳️ Sửa cấu hình để deploy modular trên K8s |
| [`zksync-crypto`](https://github.com/matter-labs/zksync-crypto)                                    | Thư viện crypto cơ sở: Poseidon hash, circuit config                                      | ✳️ Giữ nguyên nếu không thay đổi logic chứng minh <br>✳️ Chỉnh nếu bạn tạo custom AMM circuit                                                                                   |
| [`zksync-prover`](https://github.com/matter-labs/zksync-era/tree/main/prover) (trong `zksync-era`) | Sinh ZK proof từ witness                                                                  | ✳️ Có thể tách ra để chạy riêng cụm `Prover A/B` trên GPU k8s khác                                                                                                              |
| [`zksync-contracts`](https://github.com/matter-labs/zksync-era/tree/main/contracts)                | Smart contract trên Ethereum (L1 Verifier, Mailbox)                                       | ✳️ Tùy biến `Verifier` để hỗ trợ luồng `ZKDEX` <br>✳️ Có thể gắn thêm logic xác thực WBTC Pool                                                                                  |
| [`foundry-zksync`](https://github.com/matter-labs/foundry-zksync)                                  | Devtool cho viết và test smart contract trên zkSync                                       | ✳️ Dùng để test AMM logic của bạn trước khi đưa vào `zksync-era/contracts`                                                                                                      |
| [`era-consensus`](https://github.com/matter-labs/era-consensus)                                    | (mới) consensus cho sequencer permissionless                                              | ✳️ Sử dụng nếu muốn mở rộng `Sequencer A/B` thành BFT hoặc rotation-based cluster                                                                                               |

2. Mapping từng phần trong sơ đồ với repo cụ thể
| Thành phần trong sơ đồ    | Dùng từ repo nào?                              | Cách sửa                                              |
| ------------------------- | ---------------------------------------------- | ----------------------------------------------------- |
| `ZKDEX` (AMM / Orderbook) | `zksync-era/contracts`                         | Viết contract riêng hoặc module mới tương thích zkEVM |
| `Sequencer A/B`           | `zksync-era/core/bin/zksync_server`            | Mở multi-instance + load balancing / leader election  |
| `Prover A/B`              | `zksync-era/prover` + `zksync-crypto`          | Tách cluster, thêm job queue, GPU support             |
| `State Keeper`            | `zksync-era/core`                              | Giữ nguyên hoặc modular hoá việc tạo state root       |
| `ZK Verifier (L1)`        | `zksync-era/contracts/zksync`                  | Thêm lệnh xác thực cho cross-chain DEX result         |
| `Oracle`, `Relayer`       | **Bạn tự triển khai** (dựa theo LayerZero SDK) | Không nằm trong zkSync repo, cần tích hợp riêng       |

3. Dev và triển khai
| Việc cần làm                         | Gợi ý                                               |
| ------------------------------------ | --------------------------------------------------- |
| **Fork repo `zksync-era`**           | Giữ nguyên cấu trúc, dùng Docker/K8s để modular hóa |
| **Viết contract DEX của bạn**        | Dùng `foundry-zksync` hoặc `Hardhat` + Solidity     |
| **Viết LayerZero Oracle/Relayer**    | Sử dụng LayerZero Docs + TypeScript SDK             |
| **Triển khai K8s**                   | Tách Sequencer / Prover theo namespace hoặc cluster |
| **Mở staking hoặc chọn Sequencer B** | Tích hợp `era-consensus` hoặc cơ chế chọn leader    |

4. Nếu bạn muốn tối giản (MVP nhanh)
Bạn có thể fork zksync-era, giữ nguyên phần core:
- Thêm ZKDEX logic ở contract + executor.
- Chạy Sequencer và Prover theo môi trường K8s bạn dựng.
- Tích hợp LayerZero riêng như gateway ngoài, relay tx về ZKDEX.
- Mở rộng sequencer để cho phép đối tác chạy node phụ.

5. Phat trien Oracle, Replayer
| Thành phần    | Có repo không?                  | Làm gì với nó                                                                                                                                                | Link GitHub                                                                                                                | Tài liệu tương ứng                                                                                                |
| ------------- | ------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------ | -------------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------- |
| **Oracle**    | ✅ Vừa qua smart contract + docs | Fork `Oracle.sol`, test theo interface [`ILayerZeroOracle`](https://github.com/LayerZero-Labs/LayerZero/blob/main/contracts/interfaces/ILayerZeroOracle.sol) | [🔗 ILayerZeroOracle.sol](https://github.com/LayerZero-Labs/LayerZero/blob/main/contracts/interfaces/ILayerZeroOracle.sol) | [📘 Configuring Custom Oracle](https://docs.layerzero.network/v1/developers/evm/oracle/configuring-custom-oracle) |
| **Relayer**   | ✅ Có cả contract + service mẫu  | Fork `Relayer.sol`, dùng docs để viết off-chain listener                                                                                                     | [🔗 Relayer.sol](https://github.com/LayerZero-Labs/LayerZero/blob/main/contracts/Relayer.sol)                              | [📘 Develop a Custom Relayer](https://docs.layerzero.network/v1/developers/evm/relayer/develop-a-relayer)         |
| **Endpoints** | ✅ Có hợp đồng `UltraLightNode`  | Dùng trực tiếp hoặc tuỳ biến `UltraLightNodeV2.sol`                                                                                                          | [🔗 UltraLightNodeV2.sol](https://github.com/LayerZero-Labs/LayerZero/blob/main/contracts/UltraLightNodeV2.sol)            | [📘 Endpoint Overview](https://docs.layerzero.network/v1/developers/evm/endpoint/endpoint-overview)               |
