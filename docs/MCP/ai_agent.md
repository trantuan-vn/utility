::: mermaid
flowchart TD
  subgraph Agent["🤖 AI Agent"]
    Obs["👁️ Observer<br>(Input Handler)"]
    Mem["🧠 Memory<br>(Context / Vector DB)"]
    Planner["🧩 Planner<br>(Task Decomposer / Chain Builder)"]
    Executor["⚙️ Executor<br>(Tool / API / LLM Caller)"]
    Feedback["🔁 Feedback Loop<br>(Eval / Adjust / Retry)"]
    Goal["🎯 Goal<br>(Instruction / Task Spec)"]
    Tools["🔌 Tools & APIs<br>(Plugins / External Systems)"]
    LLM["🧬 LLM / Reasoning Engine"]
  end

  Obs --> Mem
  Obs --> Planner
  Goal --> Planner
  Mem --> Planner
  Planner --> Executor
  Executor --> Tools
  Executor --> LLM
  Executor --> Mem
  Executor --> Feedback
  Feedback --> Planner
  Feedback --> Mem
:::