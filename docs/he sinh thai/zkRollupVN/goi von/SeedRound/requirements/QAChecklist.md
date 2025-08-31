# QA Checklist cho Nền tảng DEX Đa Chuỗi

## 1. Kiểm thử chức năng

- [ ] Swap token giữa các chuỗi hoạt động đúng, báo giá và trượt giá chính xác (Web, Mobile, Backend, Smart contracts)
- [ ] UI báo giá hiển thị đúng, dữ liệu thời gian thực cập nhật (Web, Mobile, Backend)
- [ ] Thêm/xóa token, xác thực nhập token hợp lệ (Web, Mobile, Backend)
- [ ] Xác thực giao dịch swap, thông báo lỗi rõ ràng (Web, Mobile, Backend)
- [ ] Thêm/bớt thanh khoản, xác thực LP token (Web, Mobile, Backend, Smart contracts)
- [ ] UI thống kê pool hiển thị đúng, dữ liệu pool chính xác (Web, Mobile, Backend)
- [ ] Stake/rút thưởng LP token, kiểm tra APR (Web, Mobile, Backend, Smart contracts)
- [ ] Stake/rút thưởng token nền tảng, kiểm tra lịch sử giao dịch (Web, Mobile, Backend, Smart contracts)
- [ ] Dashboard phân tích, xuất dữ liệu đầy đủ (Web, Backend)
- [ ] Kết nối ví, xác thực giao dịch thành công (Web, Mobile)
- [ ] Tham gia launchpad, kiểm tra lịch sử tham gia (Web, Mobile, Backend)
- [ ] Vote, kiểm tra trạng thái đề xuất (Web, Backend, Smart contracts)
- [ ] Xác thực hồ sơ, kiểm tra lịch sử giao dịch (Web, Mobile, Backend)
- [ ] Quản lý pool, farm, launchpad, phân quyền đúng (Web, Backend)

## 2. Kiểm thử phi chức năng

- [ ] Hiệu năng, độ trễ đáp ứng yêu cầu (Backend, Smart contracts)
- [ ] Tải, mô-đun hóa hệ thống tốt (Backend, Smart contracts)
- [ ] Bảo mật, mã hóa dữ liệu, kiểm tra lỗ hổng (Backend, Smart contracts, Web, Mobile)
- [ ] Giao diện, đa ngôn ngữ đầy đủ (Web, Mobile)
- [ ] SEO, WCAG tuân thủ tiêu chuẩn (Web)

## 3. Loại kiểm thử

- [ ] Unit Test cho từng module
- [ ] Integration Test giữa các thành phần
- [ ] End-to-End Test cho luồng nghiệp vụ chính
- [ ] Performance Test cho backend và smart contracts
- [ ] Security Test toàn hệ thống
- [ ] Usability Test cho giao diện người dùng

## 4. Tiêu chí chấp nhận

- [ ] Đáp ứng đầy đủ các yêu cầu trong RTM
- [ ] Không có lỗi nghiêm trọng/blocker
- [ ] Đảm bảo bảo mật, hiệu năng, khả năng mở rộng

## 5. Quản lý lỗi & báo cáo

- [ ] Quản lý lỗi bằng GitHub Issues
- [ ] Báo cáo test theo từng sprint/module

---

*Checklist này giúp kiểm soát chất lượng toàn diện cho nền tảng DEX đa chuỗi, bám sát Test Plan và best practices hiện đại.*