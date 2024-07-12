# AI-assistant(UniGPT) 后端仓库
## 组员信息

1. 陈启炜   522031910299
2. 叶懿芯   520030910136
3. 韦东良   522031910516
4. 黄峻涛   522031910456

## 描述
与[UniGPT前端](https://github.com/kiwi142857/ISE-AI-assistant-Frontend)配合使用
## 配置环境变量
```
export DB_URL="jdbc:mysql://124.70.222.207:3306/unigpt"
export DB_USERNAME="xxx"
export DB_PASSWORD="xxx"
export OPENAI_API_BASE_URL="https://api.openai.com"
export OPENAI_API_KEY="sk-j101-s9eW29r08H38nPRyMia7T3BlbkFJom4ndaHHCjhWkRRp3lsG"
export CLAUDE_API_BASE_URL="https://api.claude-Plus.top"
export CLAUDE_API_KEY="sk-JAuS27IqZB15lJST6a76A0111e2d4eEb9d7aD0Bd34F20271"
export LLAMA_API_BASE_URL="https://xqtd520qidong.com"
export LLAMA_API_KEY="sk-hMdKUabqMiRM247Y2b23B02e8f484a9198D27cA2D66eAe4d"
export KIMI_API_BASE_URL="https://api.moonshot.cn"
export KIMI_API_KEY="sk-xn7ruJ2a0MYLAcheud6qVC87I6mx7b0wpFODccHCDW8oUWMg"

export POSTGRES_HOST="124.70.222.207" #（可选，默认值为"124.70.222.207"）
export POSTGRES_PORT="5432"  # （可选，默认值为"5432"）
export POSTGRES_DB="mydatabase" # (可选，默认值为"mydatabase")
export POSTGRES_USER="bleaves" # (可选，默认值为"bleaves")
export POSTGRES_PASSWORD="bleaves" # (可选，默认值为"bleaves")

export HTTP_PROXY_HOST="127.0.0.1" # (可选，默认值为"127.0.0.1")
export HTTP_PROXY_PORT="7890" # (可选，默认值为"7890")
export FRONTEND_SERVER_URL="http://localhost:3000" # (可选，默认值为"http://localhost:3000")
```
## 运行
Linux 和 MacOS:
```
./mvnw spring-boot:run
``` 
Windows:
```
./mvnw.cmd spring-boot:run
```
后端URL为http://localhost:8080
## 构建
Linux 和 MacOS:
```
./mvnw package
```
Windows:
```
./mvnw.cmd package
```
