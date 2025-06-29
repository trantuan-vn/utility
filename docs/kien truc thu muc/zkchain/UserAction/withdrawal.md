::: mermaid
sequenceDiagram
  participant User
  participant Sequencer
  participant SMT
  participant Prover
  participant Rollup
  participant Bridge
  participant Ethereum

  User->>Sequencer: withdraw(toL1Address, amount)
  Sequencer->>SMT: update SMT (giảm balance)
  Sequencer->>withdrawQueue: add entry
  Sequencer->>Prover: generate zkProof
  Prover->>Sequencer: proof + newRoot
  Sequencer->>Rollup: submitProof(proof, newRoot, withdrawals[])
  Rollup->>Bridge: record pendingWithdrawals
  User->>Bridge: claimWithdraw()
  Bridge->>Ethereum: unlock token & transfer to user
:::