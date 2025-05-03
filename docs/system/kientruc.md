::: mermaid
architecture-beta
    group client(mobile)[Client Services]
        service client_web(web)[Web Client] in client
        service client_mobile(mobile)[Mobile Client] in client
        service client_desktop(desktop)[Desktop Client] in client

    group auth_server(auth)[Authentication]
        service keycloak_server(shield)[Keycloak Server] in auth_server

    group gateway(internet)[Gateway]
        service vertx_gateway(cloud)[Vertx Gateway] in gateway

    group services(k8s)[Kubernetes Services]
        service postgres(database)[Postgres] in services
        service account(service)[Account Service] in services
        service issuer(service)[Issuer Service] in services
        service investor(service)[Investor Service] in services
        service report(service)[Report Service] in services

    group bigdata(bigdata)[Big Data Cluster]
        service nodemanager(nodeManager)[NodeManager] in bigdata
        service namenode(nameNode)[NameNode] in bigdata
        service historyserver(historyServer)[HistoryServer] in bigdata

    client_web:L --> R:keycloak_server
    client_mobile:L --> R:keycloak_server
    client_desktop:L --> R:keycloak_server
    keycloak_server:R --> L:vertx_gateway
    vertx_gateway:R --> L:postgres
    vertx_gateway:R --> L:account
    vertx_gateway:R --> L:issuer
    vertx_gateway:R --> L:investor
    vertx_gateway:R --> L:report
    vertx_gateway:B --> T:nodemanager
    vertx_gateway:B --> T:namenode
    vertx_gateway:B --> T:historyserver
:::