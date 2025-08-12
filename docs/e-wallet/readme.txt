1. Tổng kết chọn định dạng cho DEX trên Avalanche
| Tầng                       | Format tốt nhất       | Ghi chú                    |
| -------------------------- | --------------------- | -------------------------- |
| **Smart Contract**         | ✅ ABI encoding        | Mặc định của Solidity      |
| **Rust ↔ Smart Contract**  | ✅ ABI encode/decode   | Qua `ethers-rs`            |
| **Service nội bộ (Rust)**  | ✅ `borsh`             | Rất nhanh, đơn giản        |
| **Service ↔ Client (web)** | ✅ JSON hoặc Protobuf  | Tuỳ bạn dùng REST hay gRPC |
| **Storage (DB / File)**    | ✅ `borsh` hoặc `json` | tuỳ mức cần optimize       |
2. Trong Blockchain
| Ngữ cảnh            | Chuẩn de facto là gì?                         |
| ------------------- | --------------------------------------------- |
| Viết smart contract | **Solidity** là chuẩn de facto                |
| Dữ liệu RPC         | **JSON-RPC** là de facto (dù không chuẩn hóa) |
| Ví chuẩn token      | **ERC-20** là de facto token fungible         |
| Dữ liệu encode EVM  | **ABI encoding** là de facto                  |
