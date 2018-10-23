FROM java:openjdk-8

MAINTAINER Crizin <crizin@gmail.com>

COPY . /application

WORKDIR /application

RUN ./gradlew build -x test

EXPOSE 8080

CMD ["java", "-jar", "/application/build/libs/learning-spring-boot-kotlin-0.0.1-SNAPSHOT.jar"]