::: mermaid
graph TD
    subgraph User Interaction
        User[👤 User thao tác UI]
        UI[🖼️ UI Layer]
    end

    subgraph Local Data Layer
        Isar[📦 Isar DB]
        Pending[📌 PendingQueue]
    end

    subgraph Sync + Retry
        WebSocketMgr[🌐 WebSocket Manager]
        Retry[🔁 Retry + Backoff]
        SyncService[🔄 REST Fallback SyncService]
    end

    subgraph Server
        WS[📡 WebSocket Server]
        REST[🔧 REST API]
        ServerDB[🗄️ Server Database]
    end

    %% UI Flow
    User --> UI
    UI --> Isar
    UI --> Pending

    %% Sync ưu tiên WebSocket
    Pending --> WebSocketMgr
    WebSocketMgr --> WS
    WS --> ServerDB

    %% Nếu WebSocket lỗi → Retry
    WebSocketMgr --> Retry
    Retry --> SyncService
    SyncService --> REST
    REST --> ServerDB
    REST -->|Trả trạng thái| SyncService
    SyncService --> Pending

    %% Kéo dữ liệu từ Server
    UI -->|Yêu cầu đồng bộ| SyncService
    SyncService -->|Pull dữ liệu| REST
    REST -->|Data mới| SyncService
    SyncService --> Isar

    %% Nhận dữ liệu realtime
    WS -->|Push message| WebSocketMgr
    WebSocketMgr --> Isar
:::