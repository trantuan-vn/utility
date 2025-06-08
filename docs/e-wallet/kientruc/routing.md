::: mermaid
sequenceDiagram
Client->>GeoRouter: GET /geo
GeoRouter->>Client: sg-gateway.example.com
Client->>SG-Gateway: WebSocket connect
SG-Gateway->>PodManager: select least loaded pod
PodManager-->>SG-Gateway: pod-3
SG-Gateway->>Pod-3: connect user u123
Pod-3->>SlotManager: assign topic for u123
SlotManager-->>Pod-3: topic-7
Pod-3->>SessionTracker: check if u123 has pending session
alt has pending session
    Pod-3->>Old Pod (old dns): fetch state/messages
    Old Pod (old dns)->>Pod-3: deliver remaining
    Pod-3->>Client: ok, resume done
end
:::