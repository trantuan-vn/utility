::: mermaid
flowchart TD

%% CLIENT
Client[Client / CLI / Frontend]
Client -->|sendTx - transfer.ts| SequencerIndex
Client -->|checkBalance - wallet.ts| SequencerIndex
Client -->|deposit| L1Bridge
Client -->|withdraw| SequencerIndex

%% SEQUENCER
subgraph sequencer/ [Sequencer Engine]
  SequencerIndex[index.ts]
  Batcher[batcher.ts]
  SMT[smt.ts - SMT KV Store]
  Submitter[submitter.ts]
end
SequencerIndex -->|validate tx| SMT
SequencerIndex -->|append to batch| Batcher
Batcher -->|create circuit input| ProverIndex
SequencerIndex -->|generate withdraw proof| ProverIndex
SMT -->|update state.db| StateDB[state.db]
Submitter -->|send proof+root| Rollup

subgraph PROVER/
  %% PROVER
  subgraph prover/ [Proof Generator]
    ProverIndex[index.ts]
    MerkleGen[merkle.ts]
    PoseidonHash[poseidon.ts]
    Encode[utils.ts]
  end
  ProverIndex -->|Merkle root| MerkleGen
  ProverIndex -->|Poseidon hash| PoseidonHash
  ProverIndex -->|serialize input| Encode
  ProverIndex -->|generate proof| ZKCircuit

  %% CIRCUITS
  subgraph circuits/
    CircuitDef[transfer.circom]
    Input[input.json]
    Witness[witness.wtns]
    Proof[proof.json]
    VK[verification_key.json]
    ZKCircuit[ZKVerifier.sol - generated]
  end
end


%% CONTRACTS
subgraph contracts/
  subgraph rollup/
    Rollup[Rollup.sol]
    DataCommit[DataCommitment.sol]
  end

  subgraph verifier/
    Verifier[ZKVerifier.sol]
  end

  subgraph tokens/
    L2Token[L2Token.sol]
    L1Bridge[L1Bridge.sol]
  end

  subgraph utils/
    PoseidonLib[Poseidon.sol]
    MerkleLib[MerkleLib.sol]
  end

  interfaces[interfaces/ *.sol]
end

%% ONCHAIN FLOW
Submitter -->|call submitProof  | Rollup
Rollup -->|call verifier.verify| Verifier
Verifier -->|return true/false| Rollup
Rollup -->|update root| DataCommit
Rollup -->|mint/burn| L2Token
L1Bridge -->|lock/unlock| ERC20[ETH/ERC20 token]

%% L1-L2 Bridge
L1Bridge -->|mint on L2| L2Token
Rollup -->|read/write Poseidon, Merkle| PoseidonLib & MerkleLib

%% TEST / DEPLOY / CONFIG
scripts[deploy.ts] --> Rollup
scripts --> L1Bridge
test[Rollup.t.sol, circuits.test.ts] --> Rollup
deployConfig[l1-config.json] --> scripts
config[forge.toml / hardhat.config.js] --> test

%% DOCUMENTATION
readme[README.md]

:::