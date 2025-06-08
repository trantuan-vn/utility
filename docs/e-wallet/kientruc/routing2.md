::: mermaid
flowchart TD
    subgraph HAProxy

        direction TB

        A1[Client HTTPS <br> port 443] --> FE_TLS[fe_tls_multiplexer<br>mode: tcp]
        FE_TLS -->|SNI: auth.smartconsultor.com| BE_HTTPS[be_https_router<br>mode: tcp]
        FE_TLS -->|SNI: ws*| BE_WS[be_gateway_cluster<br>mode: tcp]

        BE_HTTPS --> FE_HTTPS[fe_https_router<br>127.0.0.1:8443<br>mode: http]

        FE_HTTPS -->|Host & Path: /api| BE_API[be_api_service]
        FE_HTTPS -->|Host only| BE_KC[be_keycloak]
        FE_HTTPS -->|Others| BE_DENY[be_deny_all\n403]

        subgraph be_gateway_cluster WebSocket TCP Routing
            direction LR
            BE_WS --> GW0[gateway-0]
            BE_WS --> GW1[gateway-1]
            BE_WS --> GW2[gateway-2]
            BE_WS --> GW3[gateway-3]
            BE_WS --> GW4[gateway-4]
        end

        subgraph be_gateway_http Port 80 HTTP
            FE_HTTP[fe_http_gateway<br>port 80<br>mode: http] --> BE_HTTP
            BE_HTTP --> GW0
            BE_HTTP --> GW1
            BE_HTTP --> GW2
            BE_HTTP --> GW3
            BE_HTTP --> GW4
        end

        subgraph Backends
            BE_API
            BE_KC
            BE_DENY
        end

    end
:::