::: mermaid
sequenceDiagram
    participant Client
    participant API
    participant Gateway
    participant PulsarTopic as TxEventTopic
    participant Worker
    participant FSM
    participant PostgreSQL
    participant zkSync
    participant Compensator
    participant CompensateTopic

    %% Step 1: Request Init
    Client->>API: POST /withdraw
    API->>Gateway: Validate + route
    Gateway->>PulsarTopic: TxEvent(userId, amount, txId) (partition key = userId)

    %% Step 2: Handle Event
    PulsarTopic-->>Worker: TxEvent (subscription = shared / key_shared)
    Worker->>FSM: FSM.handle(TxEvent)

    %% Step 3: Atomic balance check/update
    FSM->>PostgreSQL: UPDATE wallet<br>SET balance = balance - $amount<br>WHERE user_id = $userId AND balance >= $amount<br>RETURNING balance

    alt No row returned
        PostgreSQL-->>FSM: INSUFFICIENT_BALANCE
        FSM->>PostgreSQL: INSERT TxRecord(txId, status=FAILED, reason)
    else Success
        PostgreSQL-->>FSM: balance_after
        FSM->>PostgreSQL: INSERT TxRecord(txId, status=PENDING)
        FSM->>zkSync: SubmitTx(txId, userId, amount)
    end

    %% Step 4: zkSync Callback or Polling
    alt zkSync success
        zkSync-->>FSM: onSuccess(txId)
        FSM->>PostgreSQL: UPDATE TxRecord SET status=COMPLETED
    else zkSync failed or timeout
        zkSync-->>FSM: onFail(txId)
        FSM->>CompensateTopic: CompensateEvent(txId, userId, amount)
        %% Step 5: Compensation
        CompensateTopic-->>Compensator: CompensateEvent
        Compensator->>PostgreSQL: BEGIN<br>UPDATE wallet SET balance = balance + $amount WHERE user_id = $userId<br>UPDATE TxRecord SET status=FAILED<br>COMMIT;
    end
:::