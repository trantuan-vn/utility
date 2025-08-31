# User Guide – Nền tảng DEX Đa Chuỗi

Hướng dẫn này dành cho người dùng cuối, nhà phát triển, và đội vận hành nhằm sử dụng, bảo trì và phát triển nền tảng DEX đa chuỗi một cách hiệu quả, an toàn và tuân thủ.

---

## 1. Giới thiệu

Nền tảng DEX đa chuỗi cho phép người dùng thực hiện hoán đổi token, cung cấp thanh khoản, farming, staking, tham gia launchpad, và quản trị DAO trên nhiều blockchain như Ethereum, BSC, Solana và Cosmos.

Hệ thống bao gồm:
- Ứng dụng Web (Next.js)
- Ứng dụng Mobile (React Native)
- Backend tốc độ cao (Rust)
- Hợp đồng thông minh đa chuỗi (Solidity, Rust)
- Hạ tầng bảo mật & giám sát chuyên nghiệp

---

## 2. Cách sử dụng

### 2.1. Truy cập & Đăng nhập

- **Web**: Truy cập qua tên miền chính thức (https, SSL).
- **Mobile**: Tải từ App Store / Google Play.
- **Đăng nhập**: Qua ví Web3 hoặc tài khoản OAuth2 (Google, Apple...).
- **Bảo mật**: Hệ thống sử dụng xác thực JWT, mã hóa dữ liệu cá nhân và private key.

### 2.2. Tính năng chính

#### a. Hoán đổi Token (Swap)
- Nhận báo giá hoán đổi theo thời gian thực (`/api/swap/quote`)
- Thực hiện swap với kiểm soát trượt giá (`/api/swap/execute`)

#### b. Thanh khoản (Liquidity)
- Thêm/Rút thanh khoản vào các pool (`/api/liquidity/add`, `/api/liquidity/remove`)
- Theo dõi TVL, APR, khối lượng pool (`/api/pools/stats`)

#### c. Yield Farming
- Stake token LP để nhận phần thưởng (`/api/farming/stake`)
- Rút & claim phần thưởng (`/api/farming/unstake`, `/api/farming/claim`)

#### d. Staking Token
- Stake/Rút token nền tảng (`/api/staking/stake`, `/api/staking/unstake`)
- Claim phần thưởng staking (`/api/staking/claim`)

#### e. Launchpad Token
- Xem và tham gia các sự kiện IDO/IFO (`/api/launchpad/events`, `/api/launchpad/participate`)
- Xem lịch sử tham gia (`/api/launchpad/history`)

#### f. Quản trị DAO
- Xem đề xuất quản trị (`/api/governance/proposals`)
- Bỏ phiếu, xem kết quả (`/api/governance/vote`, `/api/governance/results`)

#### g. Hồ sơ người dùng
- Quản lý thông tin, bảo mật tài khoản (`/api/user/profile`, `/api/user/security`)

#### h. Phân tích & Thống kê
- Xem biểu đồ giá, thanh khoản, khối lượng giao dịch (`/api/analytics/dashboard`, `/api/analytics/history`)
- Xuất dữ liệu phân tích (`/api/analytics/export`)

---

## 3. Môi trường Triển khai

### a. **Staging**
- Kiểm thử chức năng, bảo mật, hiệu năng.
- Được dùng nội bộ trước khi lên Production.

### b. **Production**
- Được giám sát liên tục qua Prometheus, Grafana, Sentry.
- Hỗ trợ rollback và backup tự động.

---

## 4. Hướng dẫn Kỹ thuật cho Nhà phát triển

### a. Web App (Next.js)
- Triển khai: Vercel, GCP, AWS (hỗ trợ SSG/ISR/SSR)
- CI/CD: GitHub Actions, kiểm thử tự động, bảo mật (Next Auth, CSP)

### b. Mobile App (React Native)
- Build: Expo/EAS hoặc Fastlane
- Phân phối: App Store, Google Play, TestFlight
- Bảo mật: Xác thực ví, mã hóa dữ liệu người dùng

### c. Backend (Rust)
- Deploy: Docker, Kubernetes, hoặc Serverless (Lambda)
- Kết nối: REST API & WebSocket (`wss://api.dexplatform.com/ws`)
- DB: PostgreSQL hoặc MongoDB, backup định kỳ

### d. Smart Contracts
- Solidity (Ethereum/BSC) qua Hardhat
- Rust (Solana/Cosmos) qua Anchor hoặc CosmWasm
- Hỗ trợ multisig, upgradeable, audit và kiểm thử tự động

---

## 5. Giám sát & Vận hành

- **Monitoring**: Prometheus, Grafana, Sentry, Datadog
- **Alerting**: Thiết lập cảnh báo qua webhook/email
- **Backup**: Toàn bộ DB và dữ liệu quan trọng

---

## 6. Quản lý Phiên bản

- Gắn thẻ phiên bản theo module (`web@v1.0.0`, `backend@v2.1.3`, ...)
- Lưu lịch sử deploy để hỗ trợ rollback nhanh khi cần

---

## 7. Bảo mật & Tuân thủ

- **Audit Smart Contracts**: Trước & sau triển khai
- **Mã hóa nhạy cảm**: Private key, dữ liệu người dùng
- **Chuẩn tuân thủ**: GDPR, Web3 security, WCAG (accessibility)

---

## 8. Tài liệu & Đào tạo

- **Tài liệu deploy**: Có tại thư mục `docs/deploy/`
- **Hướng dẫn xử lý sự cố**: Tại `docs/troubleshooting.md`
- **Đào tạo**: Video hướng dẫn vận hành, bảo mật, test cases

---

## 9. Thời gian thực & WebSocket

- WebSocket endpoint: `wss://api.dexplatform.com/ws`
- Nhận dữ liệu thời gian thực: Swap, Pool, Analytics cập nhật liên tục.

---

## 10. Công nghệ sử dụng

| Thành phần       | Công nghệ chính                        |
|------------------|----------------------------------------|
| Backend          | Rust, WebSocket, REST, PostgreSQL      |
| Web Frontend     | Next.js, Zustand, SWR, wagmi, viem     |
| Mobile App       | React Native, TypeScript               |
| Smart Contracts  | Solidity (Ethereum/BSC), Rust (Solana/Cosmos) |
| DevOps           | Docker, Kubernetes, Vercel, CI/CD GitHub Actions |
| Giám sát         | Prometheus, Grafana, Sentry            |

---

## 11. Liên hệ & Hỗ trợ

- **Kênh hỗ trợ người dùng**: support@dexplatform.com
- **Báo lỗi hệ thống**: qua GitHub Issues hoặc Sentry Alert
- **Tài liệu API đầy đủ**: [docs/api.md](./api.md)

---

*Hướng dẫn này đảm bảo người dùng và nhà phát triển có thể sử dụng nền tảng DEX đa chuỗi một cách an toàn, hiệu quả, và linh hoạt.*
