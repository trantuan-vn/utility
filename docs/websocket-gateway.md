::: mermaid
flowchart TD
    subgraph Client Side
        X1(Client WebSocket)
    end

    subgraph Interface Layer
        A1(WebSocketRoute)
        A2(WebSocketHandler)
        A3(WebSocketManager)
        A4(WebSocketConsumer)
    end

    subgraph Application Layer
        B3(HandleIncomingMessageUseCase)
        B4(HandleOutgoingMessageUseCase)
        B1(IncomingMessageInputPort)
        B2(OutgoingMessageOutputPort)
    end

    subgraph Domain Layer
        C1(WebSocketMessage)
        C2(WebSocketRepository)
        C3(WebSocketBusinessRules)
    end

    subgraph Infrastructure Layer
        D2(PulsarProducer)
        D3(WebSocketRepositoryImpl)
    end

    subgraph Config Layer
        E1(WebSocketConfig)
        E2(PulsarConfig)
    end

    %% Flow connections (updated with domain repository, service, ack, and repo impl)
    X1 -->|connect| A1
    A1 -->|setup| A2
    A2 -->|register session| A3

    X1 -->|send message| A2
    A2 -->|call UseCase| B3
    B3 -->|validate/update| C1
    B3 -->|access repository| C2
    C2 -->|implemented by| D3
    B3 -->|call service| C3
    B3 -->|via InputPort| B1
    B3 -->|call Producer| D2
    B3 -->|send ack via OutputPort| B2
    B2 -->|delegate to manager| A3
    A3 -->|push ack to Client| X1

    A4 -->|receive from broker| B4
    B4 -->|process message| C1
    B4 -->|via OutputPort| B2
    B2 -->|delegate to manager| A3
    A3 -->|push to Client| X1
:::