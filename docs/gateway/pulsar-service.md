::: mermaid
sequenceDiagram
    autonumber
    participant App as Application
    participant Service as PulsarServiceImpl
    participant Vertx
    participant PulsarClient
    participant Producer
    participant Consumer
    participant SlotManager

    %% Constructor gọi initializeWithRetry
    App->>Service: constructor()
    Service->>Service: initializeWithRetry()
    Service->>Service: retryWithBackoff(task: init)

    loop retry until success
        Service->>Service: init()
        alt isShuttingDown?
            Service-->>App: return
        else
            Service->>Service: shutdownInternalResources()
            Service->>Producer: closeAsync()
            loop each Consumer
                Service->>Consumer: unsubscribe()
            end
            Service->>PulsarClient: close()
            Service->>Service: createPulsarClient()
            Service->>Vertx: create PulsarClient
            Vertx-->>Service: PulsarClient
            Service->>Service: createProducer()
            Service->>Vertx: create Producer
            Vertx-->>Service: Producer
            Service->>SlotManager: getManagedTopics()
            SlotManager-->>Service: topics
            loop each topic
                Service->>Service: createConsumer(topic)
                Service->>Vertx: subscribeAsync(topic)
                Vertx-->>Service: Consumer
            end
        end
    end

    %% sendToTopic()
    App->>Service: sendToTopic(topic, message)
    Service->>Producer: sendAsync(message)
    Producer-->>Service: messageId
    Service-->>App: send result

    %% addConsumerForUser()
    App->>Service: addConsumerForUser(userId)
    Service->>SlotManager: findTopicForUser(userId)
    SlotManager-->>Service: topic
    Service->>Service: createConsumer(topic)
    Service->>Vertx: subscribeAsync(topic)
    Vertx-->>Service: Consumer
    Service-->>App: consumer added

    %% removeConsumerForUser()
    App->>Service: removeConsumerForUser(userId)
    Service->>SlotManager: findTopicForUser(userId)
    SlotManager-->>Service: topic
    Service->>Consumer: unsubscribe()
    Service-->>App: consumer removed

    %% startBacklogMonitor()
    App->>Service: startBacklogMonitor()
    Service->>Vertx: setTimer() to monitor backlog periodically

    %% startMetricsCollector()
    App->>Service: startMetricsCollector()
    Service->>Vertx: setPeriodic() to collect metrics periodically

    %% handleMessage()
    Consumer->>Service: handleMessage(topic, consumer, message)
    Service->>Service: processMessage(topic, consumer, message)
    Service->>Vertx: executeBlocking(process logic)
    Vertx-->>Service: done processing

    %% handleError()
    Service->>Service: handleError(error, context)
    alt error recoverable?
        Service->>Service: retryWithBackoff(task)
    else
        Service->>App: log error and alert
    end

    %% close()
    App->>Service: close()
    Service->>Service: shutdownInternalResources()
    Service->>Producer: closeAsync()
    loop each Consumer
        Service->>Consumer: unsubscribe()
    end
    Service->>PulsarClient: close()

    %% retryWithBackoff()
    Service->>Service: retryWithBackoff(task)
    alt success
        Service-->>Service: task completed
    else failure
        Service->>Service: wait and retry
    end

    %% shutdownInternalResources()
    Service->>Producer: closeAsync()
    loop each Consumer
        Service->>Consumer: unsubscribe()
    end
    Service->>PulsarClient: close()

    %% createPulsarClient()
    Service->>Vertx: build PulsarClient
    Vertx-->>Service: PulsarClient

    %% createProducer()
    Service->>Vertx: build Producer
    Vertx-->>Service: Producer

    %% createConsumer()
    Service->>Vertx: subscribeAsync(topic)
    Vertx-->>Service: Consumer
:::