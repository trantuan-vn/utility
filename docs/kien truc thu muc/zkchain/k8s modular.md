::: mermaid
flowchart TB
    subgraph UserSide["🧑‍💻 User"]
        Browser --> Wallet[User Wallet]
        Wallet --> L2API[Public API Gateway]
    end

    subgraph k8sCluster["🧩 L2 Modular Network on Kubernetes"]
        subgraph Sequencing["🧭 Sequencer Layer"]
            Ingress --> SequencerPod[Sequencer Pod]
        end

        subgraph Execution["⚙️ Execution Layer"]
            SequencerPod --> ExecEngine[EVM Executor Pod]
            ExecEngine --> StateKeeper[State Manager Pod]
        end

        subgraph ZK["🧠 ZK Prover Cluster"]
            StateKeeper --> ProofQueue[Proof Task Queue]
            ProofQueue --> Prover1[Prover Pod 1]
            ProofQueue --> Prover2[Prover Pod 2]
        end

        subgraph DA["📦 Data Availability"]
            StateKeeper --> DAConnector[DA Connector Pod]
            DAConnector --> EthereumDA[(Ethereum / Celestia / Avail)]
        end

        subgraph Settlement["✅ L1 Verifier"]
            Prover1 --> ProofSubmitter[Proof Submitter]
            ProofSubmitter --> VerifierContract[(Verifier Contract<br>on Ethereum)]
        end
    end
:::