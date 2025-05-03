::: mermaid
graph TD
    UI[HR App / Admin Dashboard]
    MA[Manager Agent]

    JM[JobMatcher Agent]
    RP[ResumeParser Agent]
    IV[Interviewer Agent]
    TR[Trainer Agent]
    PF[Performance Agent]
    SE[Sentiment Agent]
    EN[Engagement Agent]
    RE[Retention Agent]
    PO[Policy Agent]

    MQ[Mesh Router: EventBus / LangGraph / CrewAI]
    DB[HR Database / Vector Store / Feedback Form]

    UI --> MA
    MA --> MQ
    JM --> MQ
    RP --> MQ
    IV --> MQ
    TR --> MQ
    PF --> MQ
    SE --> MQ
    EN --> MQ
    RE --> MQ
    PO --> MQ
    MQ --> DB
:::