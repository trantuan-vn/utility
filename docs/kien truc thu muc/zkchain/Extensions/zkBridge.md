::: mermaid
sequenceDiagram
  participant SourceChain
  participant Prover
  participant zkBridgeSender
  participant DA Layer
  participant DestinationChain
  participant zkBridgeReceiver
  participant VerifierContract

  %% Step 1: State/Message phát sinh
  SourceChain->>zkBridgeSender: emit Message(msg, dstChain, proofKey)
  zkBridgeSender->>DA Layer: write Message + MerkleProof
  zkBridgeSender->>Prover: request proof(stateRoot, message)

  %% Step 2: Sinh ZK Proof
  Prover->>Prover: generate zkProof that msg ∈ Merkle tree in stateRoot

  %% Step 3: Gửi sang DestinationChain
  Prover->>zkBridgeReceiver: submitProof( zkProof, publicInputs (stateRoot, msgHash), sourceBlock, calldata)

  %% Step 4: Xác minh proof
  zkBridgeReceiver->>VerifierContract: verify(zkProof, publicInputs)
  VerifierContract-->>zkBridgeReceiver: true/false

  %% Step 5: Thực thi message
  alt valid
    zkBridgeReceiver->>DestinationChain: execute(msg)
  else invalid
    zkBridgeReceiver-->>Prover: revert
  end

:::