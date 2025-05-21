::: mermaid
sequenceDiagram
    participant Client
    participant WSHandler as infrastructure.ws.WebSocketHandler
    participant APIGateway as interface.api.HttpController
    participant Router as interface.common.Router
    participant Handler as interface.handler.SendMessageHandler
    participant InPort as application.port.inbound.SendMessageInputPort
    participant UseCase as application.usecase.SendMessageUseCase
    participant DomainMsg as domain.model.EnvelopeMessage
    participant DomainService as domain.service.MessageDomainService
    participant ExternalAPI as infrastructure.api.ExternalApiClient
    participant OutPort as application.port.outbound.SessionSender
    participant Sender as infrastructure.ws.SessionSenderImpl
    participant SessionStore as infrastructure.redis.SessionStore
    participant PulsarProducer as infrastructure.pulsar.MessagePublisher
    participant PulsarConsumer as infrastructure.pulsar.MessageConsumer

    %% WebSocket flow
    Client->>WSHandler: ⇅ WebSocket Frame (binary Protobuf)
    WSHandler->>SessionStore: get(userId)  %% lookup session in Redis
    SessionStore-->>WSHandler: session metadata
    WSHandler->>WSHandler: parse Envelope (Protobuf → EnvelopeMessage)
    WSHandler->>Router: route(EnvelopeMessage)
    Router->>Handler: dispatch to SendMessageHandler
    Handler->>InPort: handle(EnvelopeMessage)
    InPort->>UseCase: execute(DomainMsg)
    UseCase->>DomainService: validateAndEnrich(DomainMsg)
    DomainService-->>UseCase: DomainMsg (validated/enriched)
    UseCase->>ExternalAPI: callExternalService(DomainMsg)
    ExternalAPI-->>UseCase: response(data)
    UseCase->>PulsarProducer: publishToTopic(topic, Envelope)
    UseCase->>OutPort: sendToClient(userId, Envelope)
    OutPort->>SessionStore: getSession(userId)
    SessionStore-->>OutPort: session info
    OutPort->>Sender: send binary(Protobuf)
    Sender-->>Client: WebSocket Frame (binary Protobuf)

    %% HTTP flow
    Client->>APIGateway: POST /sendMessage (JSON or Protobuf)
    APIGateway->>Router: route(requestEnvelope)
    Router->>Handler: dispatch to SendMessageHandler
    Handler->>InPort: handle(EnvelopeMessage)
    InPort->>UseCase: execute(DomainMsg)
    UseCase->>DomainService: validateAndEnrich(DomainMsg)
    DomainService-->>UseCase: DomainMsg (validated/enriched)
    UseCase->>PulsarProducer: publishToTopic(topic, Envelope)
    UseCase->>OutPort: sendToClient(userId, Envelope)
    OutPort->>SessionStore: getSession(userId)
    SessionStore-->>OutPort: session info
    OutPort->>Sender: send binary(Protobuf)
    Sender-->>Client: WebSocket Frame (binary Protobuf)

    %% Background: Pulsar Consumer receives message and dispatches to WS
    PulsarConsumer->>Router: onMessage(topic, Envelope)
    Router->>Handler: dispatch to handler based on type
    Handler->>InPort: handle(EnvelopeMessage)
    InPort->>UseCase: execute(DomainMsg)
    UseCase->>OutPort: sendToClient(userId, Envelope)
    OutPort->>SessionStore: getSession(userId)
    SessionStore-->>OutPort: session info
    OutPort->>Sender: send binary(Protobuf)
    Sender-->>Client: WebSocket Frame (binary Protobuf)

    %% Reconnect WebSocket flow
    Client->>WSHandler: ⇅ WebSocket reconnect (binary Protobuf)
    WSHandler->>SessionStore: get(userId)  %% lookup session in Redis
    SessionStore-->>WSHandler: session metadata
    WSHandler->>WSHandler: restore session state (seqId)
    WSHandler->>Router: route(EnvelopeMessage)
    Router->>Handler: dispatch to SendMessageHandler
    Handler->>InPort: handle(EnvelopeMessage)
    InPort->>UseCase: execute(DomainMsg)
    UseCase->>OutPort: sendToClient(userId, Envelope)
    OutPort->>SessionStore: getSession(userId)
    SessionStore-->>OutPort: session info
    OutPort->>Sender: send binary(Protobuf)
    Sender-->>Client: WebSocket Frame (binary Protobuf)

    %% Reconnect Redis flow
    WSHandler->>SessionStore: reconnect(userId)  %% reconnect to Redis
    SessionStore-->>WSHandler: session metadata
    SessionStore->>SessionStore: refresh session state
    WSHandler-->>Client: reconnect success

    %% Reconnect Pulsar flow
    PulsarConsumer->>PulsarProducer: reconnect (subscribe to topic)
    PulsarProducer-->>PulsarConsumer: successful reconnect
    PulsarConsumer->>Router: onMessage(topic, Envelope)
    Router->>Handler: dispatch to handler based on type
    Handler->>InPort: handle(EnvelopeMessage)
    InPort->>UseCase: execute(DomainMsg)
    UseCase->>OutPort: sendToClient(userId, Envelope)
    OutPort->>SessionStore: getSession(userId)
    SessionStore-->>OutPort: session info
    OutPort->>Sender: send binary(Protobuf)
    Sender-->>Client: WebSocket Frame (binary Protobuf)
:::