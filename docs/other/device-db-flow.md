::: mermaid
sequenceDiagram
    participant U as User Device
    participant ISP as ISP / Carrier
    participant IX as Internet Exchange / Backbone
    participant DC as Cloud / Hosting Provider
    participant LB as Load Balancer
    participant S as Server App
    participant DB as Database or Cache

    rect rgba(255, 0, 0, 0.1)
    U->>ISP: 1. TCP handshake<br>Target: ≤ 2ms
    Note right of U: WiFi or 4G jitter → dùng Ethernet
    end

    rect rgba(255, 165, 0, 0.1)
    ISP->>IX: 2. NAT + routing<br>Target: ≤ 2ms
    Note right of ISP: Giảm NAT chồng, dùng ISP có peering tốt
    end

    rect rgba(255, 165, 0, 0.1)
    IX->>DC: 3. Internet transit<br>Target: ≤ 3ms
    Note right of IX: Datacenter cùng khu vực địa lý
    end

    rect rgba(255, 255, 0, 0.1)
    DC->>LB: 4. TLS handshake<br>Target: ≤ 1ms nếu resumed
    Note right of DC: Dùng TLS 1.3 + session resumption
    end

    rect rgba(255, 255, 0, 0.1)
    LB->>S: 5. Proxy forwarding<br>Target: ≤ 1ms
    Note right of LB: L4 load balancer hoặc direct connect
    end

    rect rgba(144, 238, 144, 0.1)
    S->>S: 6. App logic<br>Target: ≤ 1ms
    Note right of S: Non-blocking I/O, no GC pause
    end

    S->>DB: 7. Query cache or db
    DB-->>S: 8. DB response

    rect rgba(144, 238, 144, 0.1)
    S-->>U: 9. Response to client<br>Target: ≤ 1ms
    Note right of S: Avoid writeQueueFull, zero-copy buffer
    end

    %% Bảng tổng kết
    Note over U,S: === Target Latency Summary ===<br>- Device → ISP: ≤ 2ms<br>- ISP → IX: ≤ 2ms<br>- IX → DC: ≤ 3ms<br>- TLS handshake: ≤ 1ms<br>- LB → App: ≤ 1ms<br>- App → Response: ≤ 1ms<br>**Tổng cộng: ≤ 10ms best effort**
:::