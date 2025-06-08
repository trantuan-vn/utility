::: mermaid
sequenceDiagram
    participant Alice as Người chơi A (Alice)
    participant Game as Game App
    participant Contract as NFT Smart Contract
    participant Bob as Người chơi B (Bob)

    Note over Alice, Game: Alice thắng trận PvP
    Game->>Contract: Gọi mint NFT ("Thanh kiếm rồng") cho Alice<br/>mint(AliceAddress, tokenId)

    Note over Contract: Ghi nhận tokenId = "Thanh kiếm rồng"<br/>chủ sở hữu: AliceAddress

    Alice->>Game: Xem inventory => thấy NFT "Thanh kiếm rồng"

    Note over Alice, Game: Alice bán thanh kiếm cho Bob
    Game->>Alice: Hiển thị UI bán NFT
    Alice->>Contract: Gửi giao dịch transferFrom(Alice, Bob, tokenId)

    Contract->>Bob: Ghi nhận Bob là chủ sở hữu mới
    Bob->>Game: Truy cập inventory => thấy "Thanh kiếm rồng"
:::