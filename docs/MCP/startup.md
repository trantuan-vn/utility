::: mermaid
graph TD
  subgraph MCP_CORE["🧠 MCP Core (Message Bus / Memory / Orchestration)"]
    MCP
  end

  %% Product
  subgraph Product["📦 Product Dept"]
    FeaturePlanner
    FeedbackSynth
  end

  %% Finance
  subgraph Finance["💸 Finance Dept"]
    Cashflow
    Forecast
    InvoiceBot
  end

  %% HR
  subgraph HR["👥 Human Resource Dept"]
    TalentScout
    Engagement
  end

  %% Marketing
  subgraph Marketing["📣 Marketing Dept"]
    SEOGenerator
    AdOptimizer
  end

  %% Engineering
  subgraph Eng["🧑‍💻 Engineering Dept"]
    DevMentor
    CodeReviewer
  end

  %% Operations
  subgraph Ops["🔧 Operations Dept"]
    KPITracker
    TaskOrchestrator
  end

  %% Legal
  subgraph Legal["⚖ Legal & Compliance"]
    ContractChecker
    PolicyUpdater
  end

  %% Security
  subgraph Security["🛡 Security Dept"]
    AccessControl
    RiskPredictor
  end

  %% Customer Support
  subgraph CS["📞 Customer Support"]
    AutoResponder
    ChurnAlert
  end

  %% Business Dev
  subgraph BizDev["🤝 Business Development"]
    PartnerScan
    DealMaker
  end

  %% R&D
  subgraph RnD["🔬 R&D"]
    TechRadar
    PrototypeAdvisor
  end

  %% Connections to MCP Core
  MCP --- FeaturePlanner
  MCP --- FeedbackSynth
  MCP --- Cashflow
  MCP --- Forecast
  MCP --- InvoiceBot
  MCP --- TalentScout
  MCP --- Engagement
  MCP --- SEOGenerator
  MCP --- AdOptimizer
  MCP --- DevMentor
  MCP --- CodeReviewer
  MCP --- KPITracker
  MCP --- TaskOrchestrator
  MCP --- ContractChecker
  MCP --- PolicyUpdater
  MCP --- AccessControl
  MCP --- RiskPredictor
  MCP --- AutoResponder
  MCP --- ChurnAlert
  MCP --- PartnerScan
  MCP --- DealMaker
  MCP --- TechRadar
  MCP --- PrototypeAdvisor

  %% Optional Mesh connections (between departments)
  FeaturePlanner --- Forecast
  FeedbackSynth --- SEOGenerator
  CodeReviewer --- DevMentor
  ChurnAlert --- KPITracker
  InvoiceBot --- ContractChecker
  PartnerScan --- DealMaker
  TechRadar --- FeaturePlanner
:::