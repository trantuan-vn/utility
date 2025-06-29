::: mermaid
sequenceDiagram
  participant Batcher
  participant ProverEngine
  participant Circom
  participant SnarkJS
  participant VerifierGen
  participant ZKVerifier.sol

  %% Step 1: Nhận batch
  Batcher->>ProverEngine: generateProof(batchTxs)

  %% Step 2: Tạo input cho circuit
  ProverEngine->>ProverEngine: buildCircuitInput(batchTxs)

  %% Step 3: Gọi Circom để sinh witness
  ProverEngine->>Circom: compile + calculateWitness(input)
  Circom-->>ProverEngine: witness.wtns

  %% Step 4: Gọi snarkjs để sinh proof
  ProverEngine->>SnarkJS: prove(witness, provingKey)
  SnarkJS-->>ProverEngine: proof.json + publicSignals

  %% Step 5: Gọi verifier generator (1 lần)
  VerifierGen->>ZKVerifier.sol: generate Solidity verifier

  %% Step 6: Trả kết quả về
  ProverEngine-->>Batcher: zkProof + publicInputs + newMerkleRoot
:::