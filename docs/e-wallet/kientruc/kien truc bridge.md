::: mermaid
sequenceDiagram
    autonumber
    participant UserA as Người dùng Blockchain A
    participant SC_A as Smart Contract Blockchain A
    participant WalletA as Ví Multi-sig Blockchain A
    participant Relayer as Relayer/Validator
    participant SC_B as Smart Contract Blockchain B
    participant WalletB as Ví Multi-sig Blockchain B
    participant UserB as Người dùng Blockchain B

    Note over UserA,UserB: Case 1: Nguồn có SC, Đích không có SC
    UserA->>SC_A: Gửi token vào SC (lock)
    SC_A->>Relayer: Phát event khóa token
    Relayer->>WalletB: Lệnh unlock token
    WalletB->>UserB: Gửi token cho người dùng

    Note over UserA,UserB: Case 2: Nguồn không có SC, Đích có SC
    UserA->>WalletA: Gửi token vào ví multi-sig (lock)
    WalletA->>Relayer: Thông báo khóa token
    Relayer->>SC_B: Gửi lệnh mint/unlock token
    SC_B->>UserB: Gửi token cho người dùng

    Note over UserA,UserB: Case 3: Nguồn có SC, Đích có SC
    UserA->>SC_A: Gửi token vào SC (lock)
    SC_A->>Relayer: Phát event khóa token
    Relayer->>SC_B: Gửi lệnh mint/unlock token
    SC_B->>UserB: Gửi token cho người dùng

    Note over UserA,UserB: Case 4: Nguồn không có SC, Đích không có SC
    UserA->>WalletA: Gửi token vào ví multi-sig (lock)
    WalletA->>Relayer: Thông báo khóa token
    Relayer->>WalletB: Gửi lệnh unlock token
    WalletB->>UserB: Gửi token cho người dùng
:::