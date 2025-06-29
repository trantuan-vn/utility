::: mermaid
sequenceDiagram
  participant User
  participant Client
  participant Sequencer
  participant SMT
  participant Batcher
  participant Prover
  participant Rollup

  %% Step 1: User gửi yêu cầu
  User->>Client: register(publicKey)
  Client->>Sequencer: POST /register {publicKey, signature}

  %% Step 2: Sequencer xác thực
  Sequencer->>SMT: check if publicKey exists
  alt chưa tồn tại
    Sequencer->>SMT: insertLeaf(publicKey → initialBalance = 0)
    Sequencer->>Batcher: add to batch as "RegisterTx"
  else đã tồn tại
    Sequencer-->>Client: reject("Account already registered")
  end

  %% Step 3: Prover sinh proof
  Batcher->>Prover: generateProof(batch)
  Prover->>Batcher: zkProof + newRoot

  %% Step 4: Submit lên L1
  Batcher->>Rollup: submitProof(proof, oldRoot, newRoot)
  Rollup->>Rollup: verify + update storedRoot
:::