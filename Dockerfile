FROM       openjdk:21-jdk-alpine
LABEL      author="ARDI"
RUN        mkdir -p /app
WORKDIR    /app

ARG        JAR
ARG        JAR_FILE=server/build/libs/${JAR}.jar

COPY       ${JAR_FILE} ./app.jar

COPY       entrypoint.sh entrypoint.sh

HEALTHCHECK --interval=30s --timeout=10s --start-period=15s --retries=5 CMD curl -k http://127.0.0.1:8081/actuator/health | grep UP && curl http://127.0.0.1:3000 || exit 1

RUN        chmod +x entrypoint.sh
ENTRYPOINT ["./entrypoint.sh"]