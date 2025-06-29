::: mermaid
sequenceDiagram
  participant Submitter
  participant Rollup
  participant DataCommitment
  participant EthereumStorage
  participant LightClient

  %% Step 1: Gửi proof & dữ liệu
  Submitter->>Rollup: submitProof(proof, publicInputs, dataCID)

  %% Step 2: Xác minh proof
  Rollup->>DataCommitment: commitBatch(newRoot, batchHash, dataCID)
  DataCommitment->>EthereumStorage: store(newRoot, hash, CID, time)

  %% Step 3: Xác minh lại (off-chain)
  LightClient->>EthereumStorage: getBatchCommitment(batchIndex)
  EthereumStorage-->>LightClient: batchHash, root, CID
:::