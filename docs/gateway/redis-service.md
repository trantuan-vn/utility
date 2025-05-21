::: mermaid
sequenceDiagram
    participant Client
    participant RedisServiceImpl
    participant RedisAPI
    participant RedisConnection
    participant Vertx

    Client->>RedisServiceImpl: initialize()
    RedisServiceImpl->>RedisServiceImpl: createRedisClient()
    RedisServiceImpl->>Redis: createClient()
    Redis-->>RedisConnection: connect()
    RedisConnection-->>RedisServiceImpl: connection success
    RedisServiceImpl->>RedisAPI: api(conn)
    RedisServiceImpl->>RedisAPIRef: set(redisAPI)
    RedisServiceImpl->>RedisServiceImpl: createRedisSubscriberClient()
    RedisServiceImpl->>Redis: createClient()
    Redis-->>RedisConnection: connect()
    RedisConnection-->>RedisServiceImpl: connection success
    RedisServiceImpl->>RedisAPI: api(conn)
    RedisServiceImpl->>RedisConnection: subscribe(expiredChannel)
    RedisServiceImpl-->>Client: initialization complete

    Client->>RedisServiceImpl: registerWebsocket(userId, deviceId, socketId, podId, ttl)
    RedisServiceImpl->>RedisAPI: multi()
    RedisAPI->>Redis: HSET socket:key
    RedisAPI->>Redis: EXPIRE socket:key
    RedisAPI->>Redis: SADD session:userId
    RedisAPI->>Redis: EXEC()
    Redis-->>RedisAPI: response
    RedisAPI-->>RedisServiceImpl: result
    RedisServiceImpl-->>Client: success/failure

    Client->>RedisServiceImpl: refreshWebsocketTTL(socketId, ttl)
    RedisServiceImpl->>RedisAPI: EXPIRE socket:key ttl
    RedisAPI-->>Redis: command
    Redis-->>RedisAPI: response
    RedisAPI-->>RedisServiceImpl: result
    RedisServiceImpl-->>Client: success/failure

    Client->>RedisServiceImpl: removeWebsocket(socketId)
    RedisServiceImpl->>RedisAPI: KEYS socket:key
    RedisAPI-->>Redis: command
    Redis-->>RedisAPI: response
    RedisServiceImpl->>RedisAPI: HGET userId
    RedisAPI-->>Redis: command
    Redis-->>RedisAPI: response
    RedisServiceImpl->>RedisAPI: multi()
    RedisAPI->>Redis: DEL socket:key
    RedisAPI->>Redis: SREM session:userId
    RedisAPI->>Redis: EXEC()
    Redis-->>RedisAPI: response
    RedisAPI-->>RedisServiceImpl: result
    RedisServiceImpl-->>Client: success/failure

    Note over RedisConnection: Expired Key Events
    Redis-->>RedisConnection: key expired event
    RedisConnection->>RedisServiceImpl: handleExpiredMessage()
    RedisServiceImpl->>RedisAPI: SREM session:userId socketId
    RedisAPI-->>Redis: command
    Redis-->>RedisServiceImpl: response

    Client->>RedisServiceImpl: shutdown()
    RedisServiceImpl->>RedisAPI: close()
    RedisServiceImpl->>RedisConnection: close()
    RedisServiceImpl->>Redis: close()
    RedisServiceImpl-->>Client: shutdown complete
:::