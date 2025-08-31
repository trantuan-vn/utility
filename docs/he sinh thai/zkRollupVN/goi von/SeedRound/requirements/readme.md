::: mermaid
flowchart TD
  %% Yêu cầu
  A1[🔍 URD: User Requirements]
  A2[📄 PRD: Product Requirements]
  A3[💼 BRD: Business Requirements]

  %% Đặc tả
  B1[📘 SRS: Software Requirements Spec]
  B2[📑 RTM: Requirement Traceability Matrix]

  %% Thiết kế
  C1[🧠 SDD: Software Design Document]
  C2[📐 HLD: High Level Design]
  C3[🔧 LLD: Low Level Design]
  C4[🗂️ ERD: Entity-Relationship Diagram]
  C5[🔁 UML: Sequence / Activity Diagrams]
  C6[🔄 DFD: Data Flow Diagram]

  %% Kiểm thử
  D1[🧪 Test Plan]
  D2[Test Cases Document]
  D3[✅ QA Checklist]

  %% Bảo mật & quyết định
  E1[🛡️ Threat Model Document]
  E2[🧭 ADR: Architecture Decisions]

  %% Triển khai
  F1[🚀 Deployment Plan]
  F2[⚙️ System Config]
  F3[📦 API Documentation]

  %% Vận hành & hướng dẫn
  G1[📖 User Manual / Guide]
  G2[📝 Release Notes]

  %% Liên kết
  A1 --> B1
  A2 --> B1
  A3 --> B1

  B1 --> B2
  B1 --> C1
  C1 --> C2
  C1 --> C3
  C2 --> C4
  C3 --> C5
  C3 --> C6

  B2 --> D1
  C1 --> D1
  D1 --> D2
  D1 --> D3

  C1 --> E2
  C1 --> E1

  C1 --> F1
  F1 --> F2
  C1 --> F3

  F2 --> G1
  F3 --> G1
  F1 --> G2

  %% Clickable links
  click A1 "./URD.md" "User Requirements"
  click A2 "./PRD.md" "Product Requirements"
  click A3 "./BRD.md" "Business Requirements"
  click B1 "./SRS.md" "Software Requirements Spec"
  click B2 "./RTM.md" "Requirement Traceability Matrix"
  click C1 "./SDD.md" "Software Design Document"
  click C2 "./HLD.md" "High Level Design"
  click C3 "./LLD.md" "Low Level Design"
  click C4 "./ERD.md" "Entity-Relationship Diagram"
  click C5 "./UML.md" "UML Diagrams"
  click C6 "./DFD.md" "Data Flow Diagram"
  click D1 "./TestPlan.md" "Test Plan"
  click D2 "./TestCases.md" "Test Cases"
  click D3 "./QAChecklist.md" "QA Checklist"
  click E1 "./ThreatModel.md" "Threat Model"
  click E2 "./ADR.md" "Architecture Decisions"
  click F1 "./DeploymentPlan.md" "Deployment Plan"
  click F2 "./SystemConfig.md" "System Configuration"
  click F3 "./API.md" "API Docs"
  click G1 "./UserGuide.md" "User Guide"
  click G2 "./ReleaseNotes.md" "Release Notes"
:::
