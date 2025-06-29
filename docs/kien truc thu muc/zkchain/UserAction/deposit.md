::: mermaid
sequenceDiagram
  participant User
  participant L1Bridge
  participant Ethereum
  participant Sequencer
  participant SMT
  participant Prover
  participant Rollup

  User->>L1Bridge: deposit(l2Receiver, amount)
  L1Bridge->>Ethereum: emit DepositRequested
  Sequencer-->>Ethereum: listen DepositRequested
  Sequencer->>Sequencer: create deposit tx
  Sequencer->>SMT: update balance(l2Receiver)
  Sequencer->>Prover: generate zkProof (with deposit)
  Prover->>Sequencer: proof + newRoot
  Sequencer->>Rollup: submitProof(proof, [oldRoot, newRoot])
  Rollup->>Rollup: verify + update storedRoot
:::