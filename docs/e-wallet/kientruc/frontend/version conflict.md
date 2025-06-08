::: mermaid
graph TD

    %% User & UI
    User[👤 User thao tác UI] --> UI[🖼️ UI Layer]
    UI --> Isar[📦 Isar DB]
    UI --> Pending[📌 PendingQueue mutation]

    %% WebSocket lifecycle
    Connect[🔌 Connect WS] --> WS[📡 WS Server]
    WS --> WSConnected[✅ WS Connected]
    Heartbeat[❤️ Ping/Pong] -->|ping| WS
    WS -->|pong| Heartbeat
    WSFailed[❌ WS Failed] --> Reconnect[♻️ Auto Reconnect]
    Reconnect --> Connect

    %% Mutation sync flow
    Pending --> WebSocketMgr[🌐 WS Manager]
    WebSocketMgr -->|mutation localVersion| WS
    WS --> ServerDB[🗄️ Server DB]

    %% Nếu mutation bị lỗi hoặc mất mạng
    WebSocketMgr --> Retry[🔁 Retry + Backoff]
    Retry --> SyncService[🔄 REST Fallback]
    SyncService --> REST[🔧 REST API]
    REST --> ServerDB
    REST --> SyncService
    SyncService --> Pending

    %% Nhận realtime từ server
    WS -->|serverUpdate serverVersion| WebSocketMgr
    WebSocketMgr --> ConflictResolver[⚖️ Version Resolver]
    ConflictResolver --> Isar

    %% Đồng bộ dữ liệu ban đầu hoặc refresh
    UI -->|pull dữ liệu| SyncService
    SyncService --> REST
    REST -->|server data + version| ConflictResolver
    ConflictResolver --> Isar

    %% Conflict Resolver
    ConflictResolver -.->|if serverVersion > localVersion| Overwrite[⬅️ Ghi đè local]
    ConflictResolver -.->|if localVersion > serverVersion| RetryMutation[🔁 Gửi lại mutation]
    Overwrite --> Isar
    RetryMutation --> Pending

:::