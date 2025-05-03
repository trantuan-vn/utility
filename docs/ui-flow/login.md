::: mermaid
flowchart TD
    Start([Start])
    LoginScreen[Login Screen]
    RedirectToKeycloak[Chuyển hướng tới Keycloak Login]
    KeycloakLogin[Đăng nhập tại Keycloak]
    Callback[Callback xử lý sau đăng nhập]
    ValidateTokenAPI[/POST /auth/token/]
    ValidateTokenResult{Token hợp lệ?}
    Success[Đăng nhập thành công]
    Error401[401: Token không hợp lệ]
    Error500[500: Lỗi máy chủ]
    Dashboard[Chuyển tới Dashboard]

    Start --> LoginScreen
    LoginScreen --> RedirectToKeycloak
    RedirectToKeycloak --> KeycloakLogin
    KeycloakLogin --> Callback
    Callback --> ValidateTokenAPI
    ValidateTokenAPI --> ValidateTokenResult

    ValidateTokenResult -- Yes --> Success
    Success --> Dashboard

    ValidateTokenResult -- No --> Error401
    Error401 --> LoginScreen

    ValidateTokenResult -- Error --> Error500
    Error500 --> LoginScreen
:::