::: mermaid
sequenceDiagram
  participant Sequencer
  participant Batcher
  participant DALogger
  participant DAStore
  participant Verifier / Rebuilder

  %% Step 1: Nhận batch txs
  Batcher->>DALogger: logBatch(batchTxs)

  %% Step 2: Serialize batch
  DALogger->>DALogger: encode(batchTxs, metadata)

  %% Step 3: Ghi xuống nơi lưu trữ
  DALogger->>DAStore: write(batchHash → encodedData)

  %% Step 4: Trả lại reference
  DAStore-->>DALogger: dataCID / txHash / blobID
  DALogger-->>Batcher: reference (hash / link)

  %% Optional: Verifier hoặc Rebuilder dùng lại
  Verifier->>DAStore: fetch(batchHash)
  DAStore-->>Verifier: full batch data
:::