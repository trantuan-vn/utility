::: mermaid
sequenceDiagram
  participant Rollup
  participant ZKVerifier
  participant ProofVerifierLib
  participant Ethereum EVM

  %% Step 1: Gọi verify từ Rollup
  Rollup->>ZKVerifier: verifyProof(proof, publicInputs)

  %% Step 2: Chuẩn bị dữ liệu
  ZKVerifier->>ZKVerifier: parse(publicInputs)
  ZKVerifier->>ProofVerifierLib: pre-process proof elements

  %% Step 3: Thực hiện xác minh elliptic curve
  ProofVerifierLib->>Ethereum EVM: pairingCheck(G1, G2)
  Ethereum EVM-->>ProofVerifierLib: true / false

  %% Step 4: Trả kết quả
  ProofVerifierLib-->>ZKVerifier: result
  ZKVerifier-->>Rollup: true / false
:::