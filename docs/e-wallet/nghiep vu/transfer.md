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

    FSM->>PostgreSQL: is_lock(fromUserId) and is_lock(toUserId)

    alt is_lock(fromUserId)=false and is_lock(toUserId)=false Success
        FSM->>PostgreSQL: lock(fromUserId) and lock(toUserId)
        %% Step 3: Atomic transfer
        FSM->>PostgreSQL: BEGIN<br>UPDATE wallet SET balance = balance - $amount WHERE user_id = $fromUserId AND balance >= $amount<br>GET DIAGNOSTICS v1 = ROW_COUNT<br>IF v1 = 0 THEN ROLLBACK<br> RETURN<br> END IF<br>UPDATE wallet SET balance = balance + $amount WHERE user_id = $toUserId<br>GET DIAGNOSTICS v2 = ROW_COUNT<br>IF v2 = 0 THEN ROLLBACK<br> RETURN<br> END IF<br>INSERT INTO tx_record(tx_id, status) VALUES ($txId, 'PENDING')<br>COMMIT;

        alt No row returned
            PostgreSQL-->>FSM: TRANSFER_FAILED
            FSM->>PostgreSQL: INSERT TxRecord(txId, status=FAILED, reason)
        else Success
            FSM->>PostgreSQL: INSERT TxRecord(txId, status=PENDING)
            FSM->>zkSync: SubmitTx(txId, fromUserId, from_amount, userId, amount)
        end

        %% Step 4: zkSync Callback or Polling
        alt zkSync success
            zkSync-->>FSM: onSuccess(txId)
            FSM->>PostgreSQL: UPDATE TxRecord SET status=COMPLETED
        else zkSync failed or timeout
            zkSync-->>FSM: onFail(txId)
            FSM->>CompensateTopic: CompensateEvent(txId, fromUserId, userId, amount)
            %% Step 5: Compensation
            CompensateTopic-->>Compensator: CompensateEvent
            Compensator->>PostgreSQL: BEGIN<br>UPDATE wallet SET balance = balance + $amount WHERE user_id = $fromUserId<br>UPDATE wallet SET balance = balance - $amount WHERE user_id = $toUserId<br>UPDATE TxRecord SET status=FAILED<br>COMMIT;
        end
        FSM->>PostgreSQL: unlock(fromUserId) and unlock(toUserId)        
    else Failed
        PostgreSQL-->>FSM: TRANSFER_FAILED
    end


:::