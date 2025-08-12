::: mermaid
sequenceDiagram
    participant User
    participant Browser
    participant Keycloak
    participant SmartAccount
    participant Backend

    User->>Browser: Truy cập dApp
    Browser->>Keycloak: Login (OIDC)
    Keycloak-->>Browser: JWT (access token)
    Browser->>Backend: Gửi token
    Backend->>Keycloak: Verify token
    alt Lần đầu
        Backend->>SmartAccount: Deploy SA + gán signer (webauthn hoặc ephemeral)
    else Đã có SA
        Backend->>SmartAccount: Add signer mới / call recovery
    end
    SmartAccount-->>User: Xác nhận signer mới
    
    User->>NewDevice: Mất máy cũ, dùng máy mới
    NewDevice->>Keycloak: Đăng nhập + 2FA
    Keycloak-->>NewDevice: Access token (JWT)
    NewDevice->>Backend: Gửi yêu cầu khôi phục signer
    Backend->>Keycloak: Xác minh JWT
    Backend->>SmartAccount: MetaTx `replaceSigner(newWebAuthn)`
    SmartAccount-->>User: Signer mới đã được gán
:::