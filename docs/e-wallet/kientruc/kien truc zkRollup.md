::: mermaid
sequenceDiagram
    participant Alice as Alice
    participant zkSyncNode as zkSync Prover
    participant Ethereum as Ethereum Smart Contract

    %% Deposit
    Alice->>Ethereum: Gửi lệnh deposit 10 USDC vào zkSync contract
    Ethereum->>Ethereum: Khóa 10 USDC trong contract
    Ethereum->>Alice: Xác nhận deposit thành công

    %% Giao dịch nội bộ trên zkSync
    Alice->>zkSyncNode: Gửi giao dịch (Alice → Bob 5 USDC)
    Bob->>zkSyncNode: Gửi giao dịch (Bob → Charlie 2 USDC)
    
    zkSyncNode->>zkSyncNode: Cập nhật Merkle Tree
    zkSyncNode->>zkSyncNode: Tính toán:<br>- new_root_hash = Merkle(state')<br>- zk-proof = prove(state, txs, state')

    zkSyncNode->>Ethereum: Gửi:<br>- zk-proof<br>- old_root_hash<br>- new_root_hash<br>- publicInputs

    Ethereum->>Ethereum: verify(zk-proof, publicInputs)
    alt proof hợp lệ
        Ethereum->>Ethereum: Cập nhật root hash mới
        Ethereum->>Alice: Giao dịch hợp lệ, trạng thái được chấp nhận
    else proof sai
        Ethereum->>Alice: Reject block, không cập nhật
    end

    %% Withdraw
    Alice->>zkSyncNode: Gửi lệnh withdraw (rút 5 USDC)
    zkSyncNode->>zkSyncNode: Tổng hợp trạng thái cuối cùng sau n giao dịch
    zkSyncNode->>zkSyncNode: Tạo zk-proof xác nhận số dư cuối cùng
    zkSyncNode->>Ethereum: Gửi zk-proof rút token
    Ethereum->>Ethereum: verify(zk-proof, publicInputs)
    alt proof hợp lệ
        Ethereum->>Ethereum: Giải phóng 5 USDC về ví Alice
        Ethereum->>Alice: Xác nhận rút tiền thành công
    else proof sai
        Ethereum->>Alice: Từ chối rút tiền, proof sai
    end
:::