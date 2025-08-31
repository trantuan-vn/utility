# RTM (Requirements Traceability Matrix) cho Nền tảng DEX Đa Chuỗi

| Mã Yêu Cầu | Mô tả Yêu Cầu | Yêu cầu chức năng/phụ trợ | Thành phần hệ thống | Phương pháp kiểm thử |
|------------|--------------|---------------------------|---------------------|----------------------|
| FR-01 | Swap token giữa các chuỗi (ETH, BSC, Solana, Cosmos) | Chức năng | Web, Mobile, Backend, Smart contracts | Kiểm thử chức năng swap, kiểm tra báo giá, trượt giá |
| FR-02 | Hiển thị báo giá, kiểm soát trượt giá | Chức năng | Web, Mobile, Backend | Kiểm thử UI, kiểm tra dữ liệu thời gian thực |
| FR-03 | Quản lý danh sách token, nhập token mới | Chức năng | Web, Mobile, Backend | Kiểm thử thêm/xóa token, xác thực nhập token |
| FR-04 | Xác thực giao dịch swap, xử lý lỗi | Chức năng | Web, Mobile, Backend | Kiểm thử xác thực, kiểm tra thông báo lỗi |
| FR-05 | Thêm/bớt thanh khoản vào pool | Chức năng | Web, Mobile, Backend, Smart contracts | Kiểm thử thêm/bớt thanh khoản, xác thực LP token |
| FR-06 | Hiển thị thống kê pool (TVL, APR, volume) | Chức năng | Web, Mobile, Backend | Kiểm thử UI, kiểm tra dữ liệu pool |
| FR-07 | Stake LP token nhận thưởng | Chức năng | Web, Mobile, Backend, Smart contracts | Kiểm thử stake/rút thưởng, kiểm tra APR |
| FR-08 | Stake token nền tảng nhận thưởng | Chức năng | Web, Mobile, Backend, Smart contracts | Kiểm thử stake/rút thưởng, kiểm tra lịch sử |
| FR-09 | Phân tích & thống kê pool, token, người dùng | Chức năng | Web, Backend | Kiểm thử dashboard, xuất dữ liệu |
| FR-10 | Tích hợp ví (MetaMask, Trust Wallet, WalletConnect) | Chức năng | Web, Mobile | Kiểm thử kết nối ví, xác thực giao dịch |
| FR-11 | Token Launchpad (IFO/IDO) | Chức năng | Web, Mobile, Backend | Kiểm thử tham gia launchpad, kiểm tra lịch sử |
| FR-12 | Quản trị On-chain (bỏ phiếu, đề xuất) | Chức năng | Web, Backend, Smart contracts | Kiểm thử vote, kiểm tra trạng thái đề xuất |
| FR-13 | Hồ sơ người dùng, bảo mật | Chức năng | Web, Mobile, Backend | Kiểm thử xác thực, kiểm tra lịch sử giao dịch |
| FR-14 | Bảng điều khiển quản trị | Chức năng | Web, Backend | Kiểm thử quản lý pool, farm, launchpad, phân quyền |
| NFR-01 | Hiệu năng (giao dịch nhanh, độ trễ thấp) | Phi chức năng | Backend, Smart contracts | Kiểm thử hiệu năng, đo độ trễ |
| NFR-02 | Khả năng mở rộng | Phi chức năng | Backend, Smart contracts | Kiểm thử tải, kiểm tra mô-đun hóa |
| NFR-03 | Bảo mật (kiểm toán, mã hóa, tuân thủ Web3) | Phi chức năng | Backend, Smart contracts, Web, Mobile | Kiểm thử bảo mật, kiểm tra mã hóa |
| NFR-04 | Tính dễ sử dụng (UI/UX, đa ngôn ngữ) | Phi chức năng | Web, Mobile | Kiểm thử giao diện, kiểm tra đa ngôn ngữ |
| NFR-05 | SEO & Khả năng tiếp cận | Phi chức năng | Web | Kiểm thử SEO, kiểm tra WCAG |

---

*RTM này giúp truy vết từng yêu cầu SRS tới thành phần hệ thống và phương pháp kiểm thử, đảm bảo phát triển và kiểm thử đầy đủ cho nền tảng DEX đa chuỗi.*