::: mermaid
graph TD
    subgraph Sequencer
        S1[🧱 Batch Committer]
    end

    subgraph Prover Node
        A1[📥 Batch Receiver]
        A2[🔄 Pre-Processor / Circuit Input Builder]
        A3[🧮 zkCircuit Runner Halo2 / Plonk]
        A4[🔐 Proof Generator ZK-SNARK]
        A5[📦 Proof Packager]
        A6[📤 Proof Submitter]
    end

    subgraph Ethereum Layer 1
        B1[✅ Verifier Smart Contract]
    end

    S1 --> A1
    A1 --> A2 --> A3 --> A4 --> A5 --> A6 --> B1
:::