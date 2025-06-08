::: mermaid
sequenceDiagram
    participant FE as Frontend
    participant BE as Backend
    participant KC as Keycloak Server

    FE->>FE: Hiển thị Login Screen
    FE->>KC: Chuyển hướng người dùng đến trang login của Keycloak
    KC->>FE: Người dùng đăng nhập và Keycloak redirect về callback URL
    FE->>BE: Gửi mã code tới BE (POST /auth/token)

    BE->>KC: Trao đổi mã code lấy access_token, refresh_token, id_token
    KC-->>BE: Trả về access_token, refresh_token, id_token

    BE->>KC: Kiểm tra access_token
    KC-->>BE: Trả về thông tin user hoặc lỗi

    alt Token hợp lệ
        BE-->>FE: Trả về AuthTokens + userInfo
        FE->>FE: Lưu token và chuyển tới Dashboard
    else Token không hợp lệ
        BE-->>FE: 401 Unauthorized
        FE->>FE: Quay lại Login Screen
    end
:::