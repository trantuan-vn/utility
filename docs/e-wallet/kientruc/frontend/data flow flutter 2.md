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

    subgraph WebSocket Lifecycle
        WebSocketMgr[🌐 WebSocket Manager]
        Connect[🔌 Connect WS]
        Reconnect[♻️ Auto Reconnect]
        Heartbeat[❤️ Heartbeat ping/pong]
        WSConnected[✅ WS Connected]
        WSFailed[❌ WS Failed]
    end

    subgraph Sync & Retry
        Retry[🔁 Retry + Backoff]
        SyncService[🔄 REST Fallback SyncService]
    end

    subgraph Server
        WS[📡 WebSocket Server]
        REST[🔧 REST API]
        ServerDB[🗄️ Server DB]
    end

    %% UI Flow
    User --> UI
    UI --> Isar
    UI --> Pending

    %% WebSocket lifecycle
    Connect --> WS
    WS --> WSConnected
    WebSocketMgr --> Heartbeat
    Heartbeat -->|ping| WS
    WS -->|pong| Heartbeat
    WSFailed --> Reconnect
    Reconnect --> Connect

    %% Gửi mutation
    Pending --> WebSocketMgr
    WebSocketMgr -->|message out| WS
    WS --> ServerDB

    %% Nhận dữ liệu realtime
    WS -->|message in| WebSocketMgr
    WebSocketMgr --> Isar

    %% Xử lý khi WS lỗi
    WebSocketMgr --> WSFailed
    WebSocketMgr --> Retry
    Retry --> SyncService
    SyncService --> REST
    REST --> ServerDB
    REST --> SyncService
    SyncService --> Pending

    %% Đồng bộ từ REST
    UI -->|pull dữ liệu| SyncService
    SyncService --> REST
    REST --> SyncService
    SyncService --> Isar
:::