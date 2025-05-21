::: mermaid
flowchart LR
    %% User đến Cloudflare Anycast Network
    User[User Ở bất kỳ đâu]
    DNS[DNS Cloudflare Anycast]
    POP_Near_User[Cloudflare POP gần nhất]
    CDN[CDN Cache]
    WAF[WAF kiểm tra]
    WS_Routing[WebSocket Routing & Proxy]
    Backbone[Cloudflare Backbone Network]

    %% Tới Gateway Cluster vùng tương ứng
    Gateway_Cluster[Gateway Cluster vùng tương ứng]
    Region1[Region 1 VD: Châu Á]
    Region2[Region 2 VD: Châu Âu]
    Region3[Region 3 VD: Bắc Mỹ]
    Region4[Region 4 VD: Nam Mỹ]
    Region5[Region 5 VD: Châu Úc]

    %% Trong từng Region Gateway Cluster
    LoadBalancer1[Load Balancer LB]
    Pod1[Pod Gateway 1]
    Pod2[Pod Gateway 2]
    PodN[Pod Gateway N]

    %% User -> Cloudflare Anycast
    User --> DNS
    DNS --> POP_Near_User
    POP_Near_User -->|Tài nguyên tĩnh| CDN
    POP_Near_User -->|Kiểm tra & lọc| WAF
    POP_Near_User -->|Kết nối WebSocket| WS_Routing
    WS_Routing --> Backbone

    %% Backbone chuyển tiếp tới vùng Gateway cụ thể theo địa lý
    Backbone --> Gateway_Cluster

    %% Chọn vùng (region) dựa trên geo routing
    Gateway_Cluster --> Region1
    Gateway_Cluster --> Region2
    Gateway_Cluster --> Region3
    Gateway_Cluster --> Region4
    Gateway_Cluster --> Region5

    %% Trong mỗi region có Load Balancer phân phối request tới các pod gateway
    Region1 --> LoadBalancer1
    LoadBalancer1 --> Pod1
    LoadBalancer1 --> Pod2
    LoadBalancer1 --> PodN
:::