::: mermaid
graph TD
  A[Envelope]
  A --> B1[type: MessageType]
  A --> B2[semantic: SemanticType]
  A --> B3[payload]

  B3 --> C1(SendMessage)
  B3 --> C2(AckMessage)
  B3 --> C3(SyncRequest)
  B3 --> C4(EventMessage)
  B3 --> C5(HeartbeatMessage)
  B3 --> C6(ErrorMessage)
  B3 --> C7(AuthMessage)
  B3 --> C8(ControlMessage)
  B3 --> C9(DataMessage)

  B2 --> D1[COMMAND]
  B2 --> D2[QUERY]
  B2 --> D3[EVENT]
  B2 --> D4[RESPONSE]
  B2 --> D5[ERROR_MSG]
  B2 --> D6[SYSTEM]

  D1 --> E11[Messaging]
  D1 --> E12[Authentication]
  D1 --> E13[Session Control]
  D1 --> E14[Notification]

  D2 --> E21[Messaging]
  D2 --> E22[Profile]
  D2 --> E23[Settings]

  D3 --> E31[System Health]
  D3 --> E32[Messaging]
  D3 --> E33[Security]
  D3 --> E34[UI/UX]

  D4 --> E41[Messaging]
  D4 --> E42[Data Access]

  D5 --> E51[Authentication]
  D5 --> E52[Authorization]
  D5 --> E53[Business Rule]

  D6 --> E61[Connectivity]
  D6 --> E62[Session]
  D6 --> E63[Identity]
:::