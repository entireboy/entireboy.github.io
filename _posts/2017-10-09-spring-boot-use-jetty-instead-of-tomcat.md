---
layout: post
title:  "[Spring Boot] Tomcat 대신 Jetty 사용하기"
date:   2017-10-09 21:38:00 +0900
published: true
categories: [ spring ]
tags: [ spring, spring boot, framework, was, jetty, tomcat, embedded, web, server ]
---

`spring-boot-starter-web`을 사용하면 dependency로 embedded Tomcat이 따라온다. 프로젝트를 실행하면 이 Tomcat 위에서 돌게 된다.
하지만, 나는 WAS로 Tomcat 말고 Jetty를 쓰고 싶다면??

```groovy
configurations {
    compile.exclude module: "spring-boot-starter-tomcat"
}

dependencies {
    compile "org.springframework.boot:spring-boot-starter-web:1.5.7.RELEASE"
    compile "org.springframework.boot:spring-boot-starter-jetty:1.5.7.RELEASE"
}
```

`spring-boot-starter-jetty`를 추가해 주고, `spring-boot-starter-tomcat`을 exclude로 빼주면 된다.
Maven은 이렇게..

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jetty</artifactId>
</dependency>
```

spring-boot-starter에서 지원하는 서버로 Tomcat, Jetty 말고 Undertow도 있다고 하는데, 주변에서 쓰는건 자주 못 봐서 요놈은 [링크](https://examples.javacodegeeks.com/enterprise-java/spring/tomcat-vs-jetty-vs-undertow-comparison-of-spring-boot-embedded-servlet-containers/)로 대체..


# 참고

- [Use Jetty instead of Tomcat - Spring Boot Reference Guide](https://docs.spring.io/spring-boot/docs/current/reference/html/howto-embedded-servlet-containers.html#howto-use-jetty-instead-of-tomcat)

- [https://examples.javacodegeeks.com/enterprise-java/spring/tomcat-vs-jetty-vs-undertow-comparison-of-spring-boot-embedded-servlet-containers/](Tomcat vs. Jetty vs. Undertow: Comparison of Spring Boot Embedded Servlet Containers)
