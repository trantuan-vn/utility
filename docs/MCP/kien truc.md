::: mermaid
flowchart TD
    subgraph DevInterface["🎯 Dev Interface (kích hoạt AI Agent)"]
        CLI["🖥️ CLI"]
        VSCode["🧩 VSCode Extension"]
        GHAction["🔁 GitHub Action"]
        APICall["🌐 API Trigger"]
    end

    subgraph EntryPoints
        CLI --> CLIEntrypoint["entrypoints/cli"]
        VSCode --> VSEntrypoint["entrypoints/vscode_extension"]
        GHAction --> GHEntrypoint["entrypoints/github_action"]
        APICall --> APIEntrypoint["entrypoints/api (tùy chọn)"]
    end

    subgraph AIAgentCore["🤖 AI Agent Core (ai_agent/)"]
        Planner["📋 TaskPlanner"]
        Orchestrator["🧠 AgentOrchestrator"]
        Registry["🗂 AgentRegistry"]
        Context["🪢 AgentContext"]
        Graph["🕸 UpdateKnowledgeGraphUseCase"]
        FeatureGen["⚙️ GenerateFeatureUseCase"]
        Refactor["🪚 RefactorCodeUseCase"]
        SyncModel["🔄 SyncBackendModelsUseCase"]
        CodeAgent["👨‍💻 CodeAgent"]
        TestAgent["🧪 TestAgent"]
        GitAgent["🌿 GitAgent"]
        KnowledgeAgent["🧠 KnowledgeAgent"]
    end

    subgraph InfraLLM
        LLM["🤖 OpenAIClient / Claude / LLM API"]
    end

    subgraph InfraFS
        FS["📁 FileEditor (Đọc/Ghi file code)"]
    end

    subgraph InfraGit
        GitHub["🔗 GitHubClient"]
    end

    subgraph ProjectSide["🧱 Dự Án Thật (Frontend + Backend)"]
        Frontend["🖼 Frontend (Flutter/Web/...)"]
        Backend["🧩 Backend (Dart/Node/Python/...)"]
        Codebase["📦 Codebase"]
        Proto["📜 .proto / openapi.json"]
    end

    %% Flow từ Dev → EntryPoint
    CLIEntrypoint --> Planner
    VSEntrypoint --> Planner
    GHEntrypoint --> Planner
    APIEntrypoint --> Planner

    %% Orchestration pipeline
    Planner --> Orchestrator
    Orchestrator --> Registry
    Orchestrator --> Context
    Registry --> CodeAgent
    Registry --> TestAgent
    Registry --> GitAgent
    Registry --> KnowledgeAgent

    %% UseCases gọi Agent tương ứng
    Orchestrator --> FeatureGen
    Orchestrator --> Refactor
    Orchestrator --> SyncModel
    FeatureGen --> CodeAgent
    Refactor --> CodeAgent
    SyncModel --> CodeAgent
    SyncModel --> FS
    SyncModel --> Proto

    %% CodeAgent và các Agent khác dùng Infra
    CodeAgent --> FS
    TestAgent --> FS
    GitAgent --> GitHub
    KnowledgeAgent --> Graph
    Graph --> Codebase

    %% LLM dùng trong Agent
    CodeAgent --> LLM
    Planner --> LLM
    TestAgent --> LLM

    %% Tác động đến Codebase thật
    FS --> Codebase
    GitHub --> Codebase

    %% Codebase ảnh hưởng Frontend/Backend
    Codebase --> Frontend
    Codebase --> Backend
:::