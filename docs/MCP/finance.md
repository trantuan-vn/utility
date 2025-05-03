::: mermaid
graph TD
    UI[Client / Analyst App]
    MA[Manager Agent]

    MC[Macro Agent]
    NS[News Agent]
    EQ[Equity Agent]
    CR[Crypto Agent]
    PF[Portfolio Agent]
    RS[Risk Agent]
    CP[Compliance Agent]
    AD[Advisor Agent]

    DS[Data Sources]
    MQ[Mesh Router: LangGraph / EventBus / Pulsar]
    KB[Financial Knowledge Base]

    UI --> MA
    MA --> MQ
    MC --> MQ
    NS --> MQ
    EQ --> MQ
    CR --> MQ
    PF --> MQ
    RS --> MQ
    CP --> MQ
    AD --> MQ
    MQ --> KB
    MQ --> DS
:::