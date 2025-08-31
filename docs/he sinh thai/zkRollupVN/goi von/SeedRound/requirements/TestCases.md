# Test Cases cho Nền tảng DEX Đa Chuỗi

## FR-01: Swap Token Đa Chuỗi
- **TC01.01:** Thực hiện swap token giữa Ethereum và BSC, kiểm tra báo giá và trượt giá.
- **TC01.02:** Swap token với số lượng lớn, xác thực báo giá cập nhật đúng.
- **TC01.03:** Swap token khi mạng chậm, kiểm tra thông báo lỗi.

## FR-02: UI Báo Giá & Dữ Liệu Thời Gian Thực
- **TC02.01:** Kiểm tra UI hiển thị báo giá token chính xác.
- **TC02.02:** Kiểm tra cập nhật giá token theo thời gian thực.
- **TC02.03:** Kiểm tra UI khi dữ liệu báo giá bị lỗi.

## FR-03: Thêm/Xóa Token
- **TC03.01:** Thêm token mới vào danh sách, xác thực nhập hợp lệ.
- **TC03.02:** Xóa token khỏi danh sách, kiểm tra cập nhật UI.
- **TC03.03:** Nhập token không hợp lệ, kiểm tra thông báo lỗi.

## FR-04: Xác Thực Giao Dịch Swap
- **TC04.01:** Thực hiện giao dịch swap thành công, kiểm tra xác thực.
- **TC04.02:** Swap với số dư không đủ, kiểm tra thông báo lỗi.
- **TC04.03:** Swap với token không hỗ trợ, kiểm tra thông báo lỗi.

## FR-05: Thêm/Bớt Thanh Khoản
- **TC05.01:** Thêm thanh khoản vào pool, xác thực nhận LP token.
- **TC05.02:** Rút thanh khoản, kiểm tra LP token bị trừ đúng.
- **TC05.03:** Thêm thanh khoản với số dư không đủ, kiểm tra thông báo lỗi.

## FR-06: UI Thống Kê Pool
- **TC06.01:** Kiểm tra UI hiển thị thống kê pool chính xác.
- **TC06.02:** Kiểm tra cập nhật dữ liệu pool theo thời gian thực.
- **TC06.03:** Kiểm tra UI khi dữ liệu pool bị lỗi.

## FR-07: Stake/Rút Thưởng LP Token
- **TC07.01:** Stake LP token thành công, kiểm tra APR hiển thị đúng.
- **TC07.02:** Rút thưởng LP token, kiểm tra số dư cập nhật.
- **TC07.03:** Stake LP token với số dư không đủ, kiểm tra thông báo lỗi.

## FR-08: Stake/Rút Thưởng Token Nền Tảng
- **TC08.01:** Stake token nền tảng thành công, kiểm tra lịch sử giao dịch.
- **TC08.02:** Rút thưởng token nền tảng, kiểm tra cập nhật lịch sử.
- **TC08.03:** Stake token nền tảng với số dư không đủ, kiểm tra thông báo lỗi.

## FR-09: Dashboard Phân Tích & Xuất Dữ Liệu
- **TC09.01:** Kiểm tra dashboard hiển thị dữ liệu phân tích chính xác.
- **TC09.02:** Xuất dữ liệu dashboard ra file CSV, kiểm tra nội dung.
- **TC09.03:** Dashboard khi dữ liệu phân tích bị lỗi.

## FR-10: Kết Nối Ví & Xác Thực Giao Dịch
- **TC10.01:** Kết nối ví MetaMask thành công, xác thực giao dịch.
- **TC10.02:** Kết nối ví Trust Wallet, kiểm tra xác thực.
- **TC10.03:** Kết nối ví thất bại, kiểm tra thông báo lỗi.

## FR-11: Tham Gia Launchpad
- **TC11.01:** Tham gia launchpad thành công, kiểm tra lịch sử.
- **TC11.02:** Tham gia launchpad với số dư không đủ, kiểm tra thông báo lỗi.
- **TC11.03:** Kiểm tra lịch sử tham gia launchpad.

## FR-12: Vote & Trạng Thái Đề Xuất
- **TC12.01:** Thực hiện vote đề xuất thành công, kiểm tra trạng thái cập nhật.
- **TC12.02:** Vote khi đã hết hạn, kiểm tra thông báo lỗi.
- **TC12.03:** Kiểm tra trạng thái đề xuất sau khi vote.

## FR-13: Xác Thực Hồ Sơ & Lịch Sử Giao Dịch
- **TC13.01:** Xác thực hồ sơ người dùng thành công.
- **TC13.02:** Kiểm tra lịch sử giao dịch hiển thị đúng.
- **TC13.03:** Xác thực hồ sơ với thông tin không hợp lệ, kiểm tra thông báo lỗi.

## FR-14: Quản Lý Pool, Farm, Launchpad, Phân Quyền
- **TC14.01:** Quản lý pool thành công, kiểm tra phân quyền.
- **TC14.02:** Quản lý farm, kiểm tra cập nhật trạng thái.
- **TC14.03:** Quản lý launchpad, kiểm tra phân quyền.

---

## NFR-01: Hiệu Năng & Độ Trễ
- **TCNFR01.01:** Đo độ trễ API khi swap token.
- **TCNFR01.02:** Đo hiệu năng smart contract khi thêm thanh khoản.

## NFR-02: Kiểm Thử Tải & Mô-đun Hóa
- **TCNFR02.01:** Kiểm thử tải backend với 1000 request/s.
- **TCNFR02.02:** Kiểm thử tải smart contract với nhiều giao dịch đồng thời.

## NFR-03: Bảo Mật & Mã Hóa
- **TCNFR03.01:** Kiểm tra lỗ hổng XSS trên web app.
- **TCNFR03.02:** Kiểm tra lỗ hổng reentrancy trên smart contract.
- **TCNFR03.03:** Kiểm tra bảo mật API backend.

## NFR-04: Giao Diện & Đa Ngôn Ngữ
- **TCNFR04.01:** Kiểm tra UI trên các thiết bị khác nhau.
- **TCNFR04.02:** Kiểm tra chuyển đổi ngôn ngữ trên web/mobile.

## NFR-05: SEO & WCAG
- **TCNFR05.01:** Kiểm tra SEO cho trang chủ.
- **TCNFR05.02:** Kiểm tra tuân thủ WCAG cho các thành phần UI.

---

*Các test case trên đảm bảo kiểm thử toàn diện cho nền tảng DEX đa chuỗi, bám sát Test Plan và các best practices hiện đại.*