::: mermaid
graph TD
  subgraph Clients
    WSClient[WebSocket Client]
  end

  subgraph Gateway Layer
    WSClient -->|Protobuf Order| WebSocketGateway[Vert.x WebSocket Gateway]
    WebSocketGateway --> AuthService[Keycloak / API Auth]
    WebSocketGateway --> OrderPublisher
  end

  subgraph Messaging Backbone Apache Pulsar
    OrderPublisher -->|Topic: order.input.groupX| Pulsar[Apache Pulsar Cluster]
    FlinkProcessor -->|Topic: order.output.groupX| Pulsar
    Matcher -->|Topic: order.matched.symbolY| Pulsar
    EventPublisher -->|Topic: event.userId| Pulsar
    Pulsar --> FlinkProcessor
  end

  subgraph Matching Engine In-Memory
    Pulsar -->|Consume order.input.groupX| Matcher[Disruptor Matching Engine]
    Matcher --> ResultHandler
    ResultHandler --> EventPublisher
  end

  subgraph E-Wallet
    EventPublisher -->|Debit/Credit Event| WalletService
    WalletService --> RedisCache
    WalletService --> PostgresDB[(PostgreSQL)]
  end

  subgraph Observability
    WebSocketGateway --> Prometheus
    Pulsar --> Prometheus
    FlinkProcessor --> Prometheus
    Matcher --> Prometheus
  end
:::