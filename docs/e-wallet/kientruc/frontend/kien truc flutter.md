::: mermaid
graph TD
    subgraph Flutter App
        UI[UI Giao Diện]
        IsarDB[Isar DB]
        PendingQueue[Pending Mutations]
        WebSocketMgr[WebSocket Manager]
        SyncService[Sync Service]
        RetryManager[Retry + Backoff Handler]
        SecureStorage[Secure Storage]
    end

    subgraph Backend
        API[REST API]
        WS[WebSocket Server]
        DB[Database]
    end

    %% UI tương tác
    UI -->|đọc / stream| IsarDB
    UI -->|ghi mới| IsarDB
    UI -->|ghi giao dịch| PendingQueue

    %% Đồng bộ 2 chiều
    WebSocketMgr -->|gửi giao dịch| WS
    WebSocketMgr -->|nhận data mới| IsarDB
    WebSocketMgr -->|báo lỗi gửi| RetryManager

    RetryManager -->|lên lịch retry| SyncService
    SyncService -->|gửi fallback qua REST| API
    SyncService -->|update trạng thái| PendingQueue

    API --> DB
    API -->|gửi dữ liệu mới| SyncService
    WS -->|push realtime| WebSocketMgr

    SecureStorage --> WebSocketMgr
    SecureStorage --> SyncService

:::