FROM adoptopenjdk/openjdk16:alpine as build
WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN chmod +x mvnw
RUN ./mvnw install -DskipTests

FROM adoptopenjdk/openjdk16:alpine
VOLUME /tmp
RUN addgroup -S springdocker && adduser -S springdocker -G springdocker
USER springdocker:springdocker
ARG JAR_FILE=/workspace/app/target/*.jar
COPY --from=build ${JAR_FILE} /app/app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]