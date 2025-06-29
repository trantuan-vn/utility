::: mermaid
sequenceDiagram
  participant Sequencer
  participant Mempool
  participant Batcher
  participant Prover
  participant Rollup

  %% Step 1: Nhận tx từ Sequencer
  Sequencer->>Mempool: addTx(tx)

  %% Step 2: Batcher gom giao dịch
  loop every interval or size limit
    Batcher->>Mempool: getBatch(n)
    Mempool-->>Batcher: batchTxs[]
  end

  %% Step 3: Batcher gửi sang Prover
  Batcher->>Prover: generateProof(batchTxs)

  %% Step 4: Prover xử lý
  Prover-->>Batcher: zkProof, newMerkleRoot

  %% Step 5: Submit lên Ethereum
  Batcher->>Rollup: submitProof(proof, oldRoot, newRoot)

  %% Step 6: Rollup xác minh
  Note over Rollup: Rollup gọi verifier, cập nhật state
:::