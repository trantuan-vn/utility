::: mermaid
sequenceDiagram
  participant Submitter
  participant Rollup
  participant ZKVerifier
  participant Token
  participant Users

  %% Step 1: Submit proof
  Submitter->>Rollup: submitProof(proof, publicInputs, batchData)

  %% Step 2: Verify ZK proof
  Rollup->>ZKVerifier: verify(proof, publicInputs)
  ZKVerifier-->>Rollup: true/false

  %% Step 3: Update state
  alt proof valid
    Rollup->>Rollup: update storedRoot, batchNonce
    Rollup->>Rollup: processWithdrawals(batchData.withdrawals)
    Rollup->>Token: unlock/release tokens to Users
  else invalid proof
    Rollup-->>Submitter: revert("Invalid proof")
  end

  %% Step 4: Optional - Forced Exit
  Users->>Rollup: forceExit(proof, merklePath)
  Rollup->>Rollup: verifyExitProof
  Rollup->>Token: send funds to user

  %% Step 5: Optional - Emergency mode
  alt fraudDetected or noProofTimeout
    Rollup->>Rollup: activateEmergencyMode()
    Users->>Rollup: withdrawWithMerkleProof(...)
  end
:::