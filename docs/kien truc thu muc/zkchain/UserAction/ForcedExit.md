::: mermaid
sequenceDiagram
  participant User
  participant Ethereum
  participant Rollup
  participant MerkleHelper
  participant Token

  %% Step 1
  User->>Rollup: call forceExit(proof, merklePath, leaf, amount)

  %% Step 2
  Rollup->>Rollup: verifyMerkleProof(leaf in MerkleRoot)
  Rollup->>Rollup: check leaf balance ≥ amount

  %% Step 3
  alt Proof valid
    Rollup->>Token: transfer(user, amount)
    Rollup->>Rollup: mark leaf as exited
  else Invalid proof
    Rollup-->>User: revert("Invalid proof or already exited")
  end
:::