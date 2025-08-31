<h1>Data Flow Diagram (DFD) cho Nền tảng DEX Đa Chuỗi</h1>
  <h2>Level 1 DFD</h2>

  <pre class="mermaid" id="cannon">
flowchart TD
    User((Người dùng))
    Admin((Admin))
    WebApp[Web App - Next.js]
    MobileApp[Mobile App - React Native]
    Backend[Backend - Rust]
    DB[(Database)]
    SC[Smart Contracts]
    Wallet[Ví Web3]
    WS[WebSocket]

    User -- Giao dịch, quản lý --> WebApp
    User -- Giao dịch, quản lý --> MobileApp
    Admin -- Quản trị --> WebApp

    WebApp -- API REST/WS --> Backend
    MobileApp -- API REST/WS --> Backend

    WebApp -- Kết nối --> Wallet
    MobileApp -- Kết nối --> Wallet

    Backend -- RPC/Web3 --> SC
    Backend -- Lưu trữ/truy vấn --> DB
    Backend -- Real-time --> WS

    WebApp -- Nhận sự kiện --> WS
    MobileApp -- Nhận sự kiện --> WS

    SC -- Emit events --> Backend
  </pre>

  <h2>Mô tả luồng chính</h2>
  <ul>
    <li><strong>Swap / Thanh khoản / Farming / Staking:</strong><br>
      Người dùng thao tác trên Web/Mobile → Gửi yêu cầu qua API → Backend xác thực, xử lý → Gọi Smart Contract → Nhận kết quả, cập nhật trạng thái qua WebSocket.
    </li>
    <li><strong>Quản trị / Admin:</strong><br>
      Admin đăng nhập → Quản lý pool/token/farm/launchpad qua Web → Backend cập nhật DB → Phân quyền truy cập.
    </li>
    <li><strong>Phân tích / Thống kê:</strong><br>
      Backend tổng hợp dữ liệu từ DB → Gửi dữ liệu phân tích cho Web/Mobile → Hiển thị biểu đồ, bảng.
    </li>
    <li><strong>Tích hợp Ví:</strong><br>
      Web/Mobile kết nối ví → Xác thực giao dịch → Gửi giao dịch tới Smart Contract.
    </li>
    <li><strong>Bảo mật:</strong><br>
      Backend xác thực JWT, mã hóa dữ liệu → Smart Contract kiểm toán, kiểm tra lỗi.
    </li>
  </ul>