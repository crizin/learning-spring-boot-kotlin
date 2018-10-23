[![Build Status](https://travis-ci.org/crizin/learning-spring-boot-kotlin.svg?branch=master)](https://travis-ci.org/crizin/learning-spring-boot-kotlin)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/5c46d04105c845d08e0d891baee31d11)](https://www.codacy.com/app/crizin/learning-spring-boot-kotlin?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=crizin/learning-spring-boot-kotlin&amp;utm_campaign=Badge_Grade)

# What's this?

An example project for learning Spring Boot with Kotlin

# Running this project

```sh
$ docker build --tag learning-spring-boot-kotlin:latest .
$ docker run --name learning-mysql -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=spring -d mysql:8.0
$ docker run -it --rm --name learning-spring-boot-kotlin --link learning-mysql:mysql -p 8080:8080 learning-spring-boot-kotlin:latest
```

And then open [http://localhost:8080/](http://localhost:8080/) in your browser.

# Using libraries or spec

- Kotlin
- Spring Boot
- Spring Security
- Thymeleaf
- Hibernate
- Gradle