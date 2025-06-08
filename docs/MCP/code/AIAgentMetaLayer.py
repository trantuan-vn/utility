### AI Agent Meta Layer - Python Prototype
# Mô phỏng cấu trúc Clean Architecture với meta-agent hỗ trợ code automation

# domain/models/project_meta.py
from dataclasses import dataclass
from typing import List

@dataclass
class ProjectMeta:
    name: str
    language: str
    framework: str
    features: List[str]

# domain/models/task.py
@dataclass
class Task:
    id: str
    title: str
    description: str
    category: str  # feature | bug | refactor | test

# domain/models/agent_action.py
@dataclass
class AgentAction:
    file_path: str
    action_type: str  # create | modify | delete
    content: str

# usecases/generate_feature_usecase.py
class GenerateFeatureUseCase:
    def __init__(self, code_agent, prompt_repo):
        self.code_agent = code_agent
        self.prompt_repo = prompt_repo

    def execute(self, project_meta: ProjectMeta, task: Task):
        prompt = self.prompt_repo.get_prompt("generate_feature")
        response = self.code_agent.generate_code(prompt, project_meta, task)
        return response

# agents/code_agent.py
class CodeAgent:
    def __init__(self, llm_client):
        self.llm_client = llm_client

    def generate_code(self, prompt_template, project_meta, task):
        prompt = prompt_template.format(
            project_name=project_meta.name,
            framework=project_meta.framework,
            task_description=task.description
        )
        return self.llm_client.call(prompt)

# infrastructure/llm/openai_client.py
class OpenAIClient:
    def __init__(self, model="gpt-4o"):
        self.model = model

    def call(self, prompt: str) -> str:
        print("[LLM] Prompt:", prompt)
        return f"# Code generated for: {prompt[:30]}..."

# prompts/task_templates/generate_feature.prompt.txt
"""
You are an expert {framework} developer.
Create the full implementation for the following task in project {project_name}:

{task_description}

Return complete code.
"""

# entrypoints/cli.py
if __name__ == "__main__":
    # mock setup
    project_meta = ProjectMeta("SmartWallet", "python", "FastAPI", ["deposit", "withdraw"])
    task = Task("001", "Thêm API nạp tiền", "Viết API POST /deposit nhận số tiền và userId", "feature")

    prompt_repo = type("PromptRepo", (), {
        "get_prompt": lambda self, name: open("prompts/task_templates/generate_feature.prompt.txt").read()
    })()

    agent = CodeAgent(OpenAIClient())
    usecase = GenerateFeatureUseCase(agent, prompt_repo)
    result = usecase.execute(project_meta, task)
    print("\nGenerated Code:\n", result)
