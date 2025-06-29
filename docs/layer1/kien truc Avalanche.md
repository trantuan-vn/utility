::: mermaid
flowchart TD
    %% Validator and Subnet Layer
    subgraph Avalanche_Network ["Avalanche Network"]
        direction TB

        subgraph Subnet_1 ["Subnet A (Default)"]
            PChain[P-Chain<br>Platform Chain<br>Validator Management]
            XChain[X-Chain<br>Exchange Chain<br>Asset Transfers]
            CChain[C-Chain<br>Contract Chain<br>EVM Smart Contracts]
            ValidatorA[Validator A]
            ValidatorB[Validator B]
            ValidatorC[Validator C]
            PChain <--> ValidatorA
            PChain <--> ValidatorB
            PChain <--> ValidatorC
            CChain <--> dApp1[dApp: DEX]
            CChain <--> dApp2[dApp: NFT Marketplace]
        end

        subgraph Subnet_2 ["Subnet B (Custom Appchain)"]
            CustomChain1[Custom Chain<br>Non-EVM Logic]
            CustomValidator1[Validator X]
            CustomValidator2[Validator Y]
            CustomChain1 <--> CustomValidator1
            CustomChain1 <--> CustomValidator2
        end

        subgraph Consensus_Layer ["Consensus Engine"]
            Snowball[Snowball<br>Probabilistic Sampling]
            Snowman[Snowman<br>Linear Blockchains C-Chain]
            AvalancheProto[Avalanche Protocol<br>DAG Chains X-Chain]
        end

        ValidatorA <--> Snowball
        ValidatorB <--> Snowball
        ValidatorC <--> Snowball
        CustomValidator1 <--> Snowball
        CustomValidator2 <--> Snowball
    end
:::
