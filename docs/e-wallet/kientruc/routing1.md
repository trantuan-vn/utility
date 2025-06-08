::: mermaid
graph TD
  Client[Client Request WebSocket / HTTP]
  DNS[DNS: asia.smartconsultor.com]
  HAProxy[HAProxy Pod]
  LoadAgent[LoadAgent Service Vert.x]
  G1[Gateway Pod 1<br/>activeUser=35]
  G2[Gateway Pod 2<br/>activeUser=15]
  G3[Gateway Pod 3<br/>activeUser=80]

  Client --> DNS --> HAProxy --> LoadAgent
  LoadAgent --> G1
  LoadAgent --> G2
  LoadAgent --> G3

  subgraph Asia Cluster
    HAProxy --> LoadAgent
    LoadAgent --> G1
    LoadAgent --> G2
    LoadAgent --> G3
  end
:::