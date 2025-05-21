::: mermaid
sequenceDiagram
    participant Client
    participant WebSocketManagerImpl
    participant ServerWebSocket
    participant WorkerExecutor
    participant Vertx

    Client->>WebSocketManagerImpl: registerSession(webSocket)
    alt During Shutdown
        WebSocketManagerImpl-->>Client: Reject (shutting down)
    else Valid Registration
        WebSocketManagerImpl->>ServerWebSocket: textHandlerID()
        WebSocketManagerImpl->>ConcurrentHashMap: put(socketId, webSocket)
        alt Existing Socket
            WebSocketManagerImpl->>ServerWebSocket: closeQuietly(previous)
        end
        WebSocketManagerImpl-->>Client: Success
    end

    Client->>WebSocketManagerImpl: removeSession(webSocket)
    WebSocketManagerImpl->>ServerWebSocket: textHandlerID()
    WebSocketManagerImpl->>ConcurrentHashMap: remove(socketId)
    alt Socket Found
        WebSocketManagerImpl->>ServerWebSocket: closeQuietly()
    end
    WebSocketManagerImpl-->>Client: Success

    Client->>WebSocketManagerImpl: sendMessage(socketId, message)
    WebSocketManagerImpl->>ConcurrentHashMap: get(socketId)
    alt Socket Valid
        WebSocketManagerImpl->>ServerWebSocket: writeTextMessage()
        ServerWebSocket-->>WebSocketManagerImpl: Result
        alt Failure
            WebSocketManagerImpl->>ConcurrentHashMap: remove(socketId)
        end
    else Socket Invalid
        WebSocketManagerImpl-->>Client: Error
    end

    Client->>WebSocketManagerImpl: broadcast(message)
    alt Validation Checks
        WebSocketManagerImpl-->>Client: Error if invalid
    else Proceed
        WebSocketManagerImpl->>ConcurrentHashMap: values()
        WebSocketManagerImpl->>WebSocketManagerImpl: chunkList()
        loop For Each Chunk
            WebSocketManagerImpl->>WorkerExecutor: executeBlocking()
            WorkerExecutor->>WebSocketManagerImpl: sendWithRetry()
            loop For Each Socket in Chunk
                WebSocketManagerImpl->>ServerWebSocket: writeBinaryMessage()
                alt Failure & Retries Left
                    WebSocketManagerImpl->>Vertx: setTimer(retry)
                else Final Failure
                    WebSocketManagerImpl->>ConcurrentHashMap: remove(socketId)
                    WebSocketManagerImpl->>ServerWebSocket: closeQuietly()
                end
            end
        end
        WebSocketManagerImpl-->>Client: Composite Result
    end

    Client->>WebSocketManagerImpl: shutdown()
    WebSocketManagerImpl->>ConcurrentHashMap: forEach(closeQuietly)
    WebSocketManagerImpl->>ConcurrentHashMap: clear()
    WebSocketManagerImpl->>WorkerExecutor: close()
    WorkerExecutor-->>WebSocketManagerImpl: Result
    WebSocketManagerImpl-->>Client: Completion

    Note right of WebSocketManagerImpl: Key Features Shown:<br>1. Thread-safe session management<br>2. Graceful shutdown handling<br>3. Chunked parallel broadcasting<br>4. Retry mechanism for failed sends<br>5. Resource cleanup
:::