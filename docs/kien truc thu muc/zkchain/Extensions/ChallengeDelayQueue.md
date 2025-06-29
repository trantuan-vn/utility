::: mermaid
sequenceDiagram
  participant Submitter
  participant Rollup
  participant DelayQueue
  participant Watcher
  participant Verifier
  participant JudgeContract

  %% Step 1: Submit batch proof (pending)
  Submitter->>Rollup: submitBatch(proof, publicInputs)
  Rollup->>DelayQueue: enqueue(batchHash, delayUntil)

  %% Step 2: Waiting period (Challenge Window)
  Note over DelayQueue, Watcher: ⏳ Waiting for challenge period (e.g. 1 hour)

  %% Step 3: Watcher detects invalid proof
  Watcher->>JudgeContract: submitChallenge(batchHash, counterProof)
  JudgeContract->>Verifier: verify(counterProof)

  %% Step 4: Judge decides
  alt counterProof valid
    JudgeContract->>DelayQueue: reject(batchHash)
    DelayQueue->>Rollup: rollback(batchHash)
  else invalid
    JudgeContract->>DelayQueue: confirm(batchHash)
    DelayQueue->>Rollup: finalize(batchHash)
  end
:::