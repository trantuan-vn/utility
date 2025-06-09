::: mermaid
flowchart TD
    subgraph Flink
        A[GatewayMessage Input Stream]
        A --> B[TransactionProcessFunction]
    end

    subgraph Business Layer
        B --> C[BusinessDispatcher]

        C -->|EventType.DEPOSIT_REQUESTED| D1[DepositUseCase]
        C -->|EventType.WITHDRAW_REQUESTED| D2[WithdrawUseCase]
        C -->|...| Dn[OtherUseCase]
    end

    D1 --> E1[TransactionResult]
    D2 --> E2[TransactionResult]
    Dn --> En[TransactionResult]

    E1 --> F[Collector<TransactionResult>]
    E2 --> F
    En --> F

:::