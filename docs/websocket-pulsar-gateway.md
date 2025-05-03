::: mermaid
flowchart TD
    subgraph Client Side
        X1(Client WebSocket)
    end

    subgraph Adapter Layer
        A1(WebSocketHandler)
        A2(WebSocketManager)
        A3(PulsarService)
    end

    X1 -->|connect/send message| A1
    A1 -->|register session / delegate| A2
    A1 -->|send to broker| A3
    A3 -->|message received from broker| A1
    A1 -->|delegate push to client| A2
    A2 -->|push message| X1
:::
