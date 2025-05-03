::: mermaid
graph TD
    subgraph Client Side
        A[WebSocket Client: clientId user123]
    end

    subgraph Gateway Pod
        B[Gateway: sessionMap clientId -> WS session]
        B1[Produce to Pulsar: gateway-requests key clientId]
        B2[Consume from Pulsar: gateway-responses Key_Shared]
        B3[Lookup sessionMap by key and send to WS]
    end

    subgraph Pulsar
        C1[Topic: gateway-requests]
        C2[Topic: gateway-responses]
    end

    subgraph Microservice
        D1[Consume from: gateway-requests]
        D2[Process and Produce to: gateway-responses key clientId]
    end

    A -->|Send request| B
    B --> B1
    B1 --> C1
    C1 --> D1
    D1 --> D2
    D2 --> C2
    C2 --> B2
    B2 --> B3
    B3 -->|Send response| A
:::