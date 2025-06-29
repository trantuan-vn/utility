::: mermaid
sequenceDiagram
  participant Prover
  participant Submitter
  participant Ethereum
  participant Rollup
  participant Verifier

  %% Step 1: Nhận proof từ Prover
  Prover->>Submitter: zkProof + publicInputs (newRoot, oldRoot, etc)

  %% Step 2: Submit lên Ethereum
  Submitter->>Rollup: submitProof(proof, oldRoot, newRoot, batchData)

  %% Step 3: Rollup gọi Verifier
  Rollup->>Verifier: verify(proof, publicInputs)
  Verifier-->>Rollup: true/false

  %% Step 4: Cập nhật state on-chain
  alt Proof valid
    Rollup->>Rollup: update storedRoot
    Rollup->>Rollup: process withdrawals (if any)
  else Invalid
    Rollup-->>Submitter: revert("Invalid proof")
  end
:::