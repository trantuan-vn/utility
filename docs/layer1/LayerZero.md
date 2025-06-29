::: mermaid
flowchart TD
    %% Chain A
    subgraph Chain_A["Chain A (Ethereum)"]
        UAppA["User App<br>(UA Sender)<br>Smart Contract"]
        EndpointA["LayerZero Endpoint<br>Smart Contract"]
        BlockA["Block A<br>Ghi event"]
        UAppA -->|sendMessage payload| EndpointA
        EndpointA -->|Emit Event| BlockA
    end

    %% Off-chain Network
    subgraph OffChain["🌐 Off-chain Components"]
        Oracle["Oracle Node<br>Quan sát Block Header"]
        Relayer["Relayer Node<br>Quan sát Log Event + gửi Merkle Proof"]
    end

    BlockA -->|Gửi Header| Oracle
    BlockA -->|Gửi Log & Proof| Relayer

    %% Chain B
    subgraph Chain_B["Chain B (Avalanche)"]
        EndpointB["LayerZero Endpoint<br>Smart Contract"]
        UAppB["User App<br>(UA Receiver)<br>Smart Contract"]
        EndpointB -->|deliverMessage payload | UAppB
    end

    Oracle -->|Gửi Header| EndpointB
    Relayer -->|Gửi Merkle Proof| EndpointB
:::