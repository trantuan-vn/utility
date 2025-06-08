::: mermaid
sequenceDiagram
    participant User
    participant UI
    participant IsarDB
    participant PendingQueue
    participant WebSocketMgr
    participant RetryManager
    participant SyncService
    participant RESTAPI
    participant WebSocketServer
    participant ServerDB

    %% Flow khi user thao tác
    User->>UI: Tạo giao dịch (nhập liệu)
    UI->>IsarDB: Ghi bản ghi mới (giao diện phản hồi ngay)
    UI->>PendingQueue: Đẩy vào hàng chờ sync

    %% Ưu tiên sync qua WebSocket
    PendingQueue->>WebSocketMgr: Lấy giao dịch mới
    WebSocketMgr->>WebSocketServer: Gửi mutation (WS)
    WebSocketServer->>ServerDB: Ghi xuống DB

    %% Nếu WS thất bại
    WebSocketMgr-->>RetryManager: Thông báo lỗi gửi
    RetryManager->>SyncService: Kích hoạt retry (backoff)
    SyncService->>RESTAPI: Fallback gửi mutation qua REST
    RESTAPI->>ServerDB: Ghi xuống DB
    RESTAPI-->>SyncService: Trả về trạng thái thành công/thất bại
    SyncService->>PendingQueue: Cập nhật trạng thái mutation

    %% Kéo dữ liệu mới (periodic hoặc khi mở app)
    UI->>SyncService: Yêu cầu load bảng (pull sync)
    SyncService->>RESTAPI: Gọi API load dữ liệu
    RESTAPI->>ServerDB: Truy vấn bảng
    RESTAPI-->>SyncService: Trả về dữ liệu
    SyncService->>IsarDB: Ghi đè hoặc merge vào local DB

    %% Nhận broadcast realtime từ server
    WebSocketServer-->>WebSocketMgr: Push giao dịch mới
    WebSocketMgr->>IsarDB: Cập nhật Isar (UI tự phản hồi)
:::