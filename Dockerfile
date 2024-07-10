FROM maven:3.9.5 AS build
WORKDIR /app
COPY pom.xml .
COPY keystore.p12 . 
COPY src ./src
# COPY ./.mvn/wrapper/settings.xml /root/.m2/settings.xml
RUN mvn clean package -DskipTests

FROM ubuntu:22.04
WORKDIR /app

RUN apt-get update && \
    apt-get install -y openjdk-17-jdk

COPY --from=build /app/target/*.jar app.jar
COPY --from=build /app/keystore.p12 keystore.p12

ENV OPENAI_API_KEY=sk-OhenOiFqJC5rjzCI5793C1B0B7804bF3967eB776847809E2
ENV OPENAI_API_BASE_URL=https://api.chatgptid.net
ENV CLAUDE_API_BASE_URL=https://api.claude-Plus.top
ENV CLAUDE_API_KEY=sk-JAuS27IqZB15lJST6a76A0111e2d4eEb9d7aD0Bd34F20271
ENV LLAMA_API_BASE_URL=https://xqtd520qidong.com
ENV LLAMA_API_KEY=sk-hMdKUabqMiRM247Y2b23B02e8f484a9198D27cA2D66eAe4d
ENV KIMI_API_BASE_URL=https://api.moonshot.cn
ENV KIMI_API_KEY=sk-xn7ruJ2a0MYLAcheud6qVC87I6mx7b0wpFODccHCDW8oUWMg


EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
