::: mermaid
sequenceDiagram
  participant SourceChain
  participant OracleFeeder
  participant OracleContract
  participant DestinationRollup
  participant LightClient

  %% Step 1: Oracle theo dõi state ở chain nguồn
  SourceChain->>OracleFeeder: emit StateUpdateEvent(stateHash, blockNum, extraData)

  %% Step 2: Feeder build commitment
  OracleFeeder->>OracleFeeder: sign(stateHash, blockNum, metadata)

  %% Step 3: Gửi lên chain đích
  OracleFeeder->>OracleContract: submitState( stateHash, blockNum, metadata,sig)

  %% Step 4: OracleContract xác minh chữ ký
  OracleContract->>OracleContract: verify(sig)

  %% Step 5: Lưu state đã đồng bộ
  OracleContract->>OracleContract: store(blockNum → stateHash)

  %% Step 6: DestinationRollup sử dụng
  DestinationRollup->>OracleContract: getStateAt(blockNum)
  OracleContract-->>DestinationRollup: stateHash

  %% Optional: LightClient cũng có thể dùng
  LightClient->>OracleContract: verifyCrossChainProof(...)
:::