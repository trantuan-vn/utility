# Tài liệu API Nền tảng DEX Đa Chuỗi

## 1. Giới thiệu

API này cung cấp các chức năng cốt lõi cho nền tảng sàn giao dịch phi tập trung (DEX) đa chuỗi, hỗ trợ Ethereum, BSC, Solana và Cosmos. Bao gồm hoán đổi token, quản lý thanh khoản, yield farming, staking, phân tích, tích hợp ví, launchpad, quản trị, hồ sơ người dùng và bảng điều khiển quản trị.

---

## 2. Xác thực

- **OAuth2 / JWT** qua Next Auth để xác thực người dùng an toàn.
- Các endpoint API yêu cầu xác thực trừ khi được chỉ định là công khai.

---

## 3. Endpoint

### 3.1. Hoán đổi Token

- `POST /api/swap/quote`
    - Nhận báo giá hoán đổi theo thời gian thực, kiểm soát trượt giá.
    - Tham số: `fromToken`, `toToken`, `amount`, `chain`
- `POST /api/swap/execute`
    - Thực hiện hoán đổi token.
    - Tham số: `fromToken`, `toToken`, `amount`, `chain`, `walletAddress`
- `GET /api/tokens/list`
    - Liệt kê các token được hỗ trợ, token tùy chỉnh.

---

### 3.2. Quản lý Thanh khoản

- `POST /api/liquidity/add`
    - Thêm thanh khoản vào pool.
    - Tham số: `tokenA`, `tokenB`, `amountA`, `amountB`, `chain`
- `POST /api/liquidity/remove`
    - Rút thanh khoản khỏi pool.
    - Tham số: `lpToken`, `amount`, `chain`
- `GET /api/pools/stats`
    - Thống kê pool: TVL, APR, khối lượng.

---

### 3.3. Yield Farming

- `GET /api/farming/pools`
    - Liệt kê các pool farming, APR, phần thưởng.
- `POST /api/farming/stake`
    - Stake token LP.
    - Tham số: `lpToken`, `amount`, `poolId`
- `POST /api/farming/unstake`
    - Rút token LP đã stake.
- `POST /api/farming/claim`
    - Nhận phần thưởng farming.

---

### 3.4. Staking

- `GET /api/staking/pools`
    - Liệt kê các pool staking.
- `POST /api/staking/stake`
    - Stake token nền tảng.
- `POST /api/staking/unstake`
    - Rút token đã stake.
- `POST /api/staking/claim`
    - Nhận phần thưởng staking.

---

### 3.5. Phân tích & Thống kê

- `GET /api/analytics/dashboard`
    - Phân tích pool, token, người dùng.
- `GET /api/analytics/history`
    - Biểu đồ lịch sử: giá, khối lượng, thanh khoản.
- `GET /api/analytics/export`
    - Xuất dữ liệu phân tích.

---

### 3.6. Tích hợp Ví

- `POST /api/wallet/connect`
    - Kết nối ví (MetaMask, Trust Wallet, WalletConnect).
- `GET /api/wallet/status`
    - Trạng thái kết nối ví.

---

### 3.7. Launchpad Token

- `GET /api/launchpad/events`
    - Liệt kê các sự kiện ra mắt token (IFO/IDO).
- `POST /api/launchpad/participate`
    - Tham gia sự kiện launchpad.
- `GET /api/launchpad/history`
    - Lịch sử tham gia launchpad của người dùng.

---

### 3.8. Quản trị On-chain

- `GET /api/governance/proposals`
    - Liệt kê các đề xuất quản trị.
- `POST /api/governance/vote`
    - Bỏ phiếu cho đề xuất.
- `GET /api/governance/results`
    - Kết quả bỏ phiếu đề xuất.

---

### 3.9. Hồ sơ Người dùng & Bảo mật

- `GET /api/user/profile`
    - Lấy hồ sơ người dùng, số dư, lịch sử giao dịch.
- `PUT /api/user/profile`
    - Cập nhật thông tin hồ sơ.
- `GET /api/user/security`
    - Cài đặt bảo mật tài khoản.

---

### 3.10. Bảng điều khiển Quản trị

- `GET /api/admin/overview`
    - Thống kê nền tảng, hoạt động người dùng.
- `POST /api/admin/pool/manage`
    - Quản lý pool, token, farm, launchpad.
- `POST /api/admin/access`
    - Kiểm soát quyền truy cập quản trị.

---

## 4. Thời gian thực & WebSocket

- `wss://api.dexplatform.com/ws`
    - Cập nhật thời gian thực: hoán đổi, pool, phân tích.

---

## 5. Yêu cầu Phi chức năng

- **Hiệu năng:** Độ trễ thấp, xử lý giao dịch nhanh.
- **Khả năng mở rộng:** Kiến trúc module, hỗ trợ đa chuỗi, đồng thời cao.
- **Bảo mật:** Audit hợp đồng thông minh, mã hóa dữ liệu nhạy cảm, tiêu chuẩn Web3.
- **Khả năng sử dụng:** API đáp ứng cho web/mobile, hỗ trợ đa ngôn ngữ.
- **SEO & Truy cập:** API hỗ trợ dữ liệu thân thiện SEO cho web client.

---

## 6. Công nghệ sử dụng

- **Backend:** Rust (API, WebSocket, DB)
- **Smart Contracts:** Solidity (Ethereum/BSC), Rust (Solana/Cosmos)
- **Web:** Next.js, Zustand, SWR, React Query, wagmi, viem, ethers
- **Mobile:** React Native, TypeScript

---

## 7. Tích hợp

- Thư viện Web3 cho ví, quản lý đa chuỗi.
- Xác thực và xử lý giao dịch an toàn.

---

*Tài liệu API này dựa trên SRS và là nền tảng để phát triển, kiểm thử, triển khai nền tảng DEX đa chuỗi.*

