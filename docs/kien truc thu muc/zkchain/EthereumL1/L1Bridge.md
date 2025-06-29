::: mermaid
sequenceDiagram
  participant User
  participant L1Bridge
  participant ERC20
  participant Sequencer
  participant Rollup

  %% Deposit flow
  User->>L1Bridge: deposit(amount, token)
  L1Bridge->>ERC20: transferFrom(User, L1Bridge, amount)
  L1Bridge->>L1Bridge: emit DepositEvent(user, amount, token)
  Sequencer-->>L1Bridge: watch DepositEvent
  Sequencer->>L2: mint on L2 + update Merkle Tree

  %% Withdrawal flow (later)
  Note over Rollup, L1Bridge: After zkProof & Merkle root verified
  User->>L1Bridge: finalizeWithdrawal(proof, merklePath)
  L1Bridge->>Rollup: verifyExitProof(...)
  Rollup-->>L1Bridge: true/false
  alt proof valid
    L1Bridge->>ERC20: transfer(User, amount)
  else invalid
    L1Bridge-->>User: revert
  end
:::