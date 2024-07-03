# 使用Python基础镜像
FROM python:3.9-slim

# 创建工作目录
WORKDIR /app

# 安装所需依赖（如果有requirements.txt）
# COPY requirements.txt /app/
# RUN pip install -r requirements.txt

# 入口命令
CMD ["python", "run.py"]