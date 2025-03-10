FROM maven:latest
LABEL authors="gary"

WORKDIR /app

COPY pom.xml /app/

COPY . /app/

RUN mvn package -DskipTests

CMD ["java", "-jar", "target/gradebook.jar"]
