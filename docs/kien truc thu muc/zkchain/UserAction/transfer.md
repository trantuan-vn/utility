::: mermaid
sequenceDiagram
  participant User
  participant Client
  participant Sequencer
  participant SMT
  participant Batcher
  participant Prover
  participant Rollup
  participant Verifier

  %% Step 1: User gửi lệnh
  User->>Client: sendTransfer(to: Bob, amount: 100)
  Client->>Sequencer: POST /tx {from, to, amount, signature}

  %% Step 2: Sequencer xử lý
  Sequencer->>SMT: load balance(sender)
  alt balance đủ
    Sequencer->>SMT: subtract sender, add receiver
    Sequencer->>Batcher: add to current batch
  else không đủ
    Sequencer->>Client: Reject tx
  end

  %% Step 3: Tạo batch, sinh proof
  Batcher->>Prover: generateProof(batch)
  Prover->>Batcher: zkProof + newRoot

  %% Step 4: Gửi proof lên Ethereum
  Batcher->>Rollup: submitProof(proof, oldRoot, newRoot)

  %% Step 5: On-chain xác minh
  Rollup->>Verifier: verifyProof(proof, publicInputs)
  Verifier-->>Rollup: valid = true
  Rollup->>Rollup: update stored Merkle Root
:::