::: mermaid
graph TD
    UI[User Interface]
    MA[Manager Agent]
    AG1[CodeAgent]
    AG2[TestAgent]
    AG3[UXAgent]
    AG4[DevOpsAgent]
    KB[Knowledge Hub]
    MQ[Agent Gateway / Mesh Router]

    UI --> MA
    MA --> MQ
    AG1 --> MQ
    AG2 --> MQ
    AG3 --> MQ
    AG4 --> MQ
    MQ --> AG1
    MQ --> AG2
    MQ --> AG3
    MQ --> AG4
    MQ --> KB
:::