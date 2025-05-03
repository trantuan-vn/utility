::: mermaid
graph TD
    UI[User Interface]
    MA[Manager Agent]

    ST[Strategist Agent]
    CT[Content Agent]
    DS[Design Agent]
    SEO[SEO Agent]
    SO[Social Agent]
    EM[Email Agent]
    DA[Data Agent]

    KB[Marketing Knowledge Base]
    MQ[Agent Mesh Router]

    UI --> MA
    MA --> MQ
    ST --> MQ
    CT --> MQ
    DS --> MQ
    SEO --> MQ
    SO --> MQ
    EM --> MQ
    DA --> MQ
    MQ --> ST
    MQ --> CT
    MQ --> DS
    MQ --> SEO
    MQ --> SO
    MQ --> EM
    MQ --> DA
    MQ --> KB
:::