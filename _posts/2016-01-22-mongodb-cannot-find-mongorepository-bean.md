---
layout: post
title:  "[Spring] MongoRepository bean을 찾을 수 없을 때"
date:   2016-01-22 03:39:36 +0900
categories: spring spring-data mongodb
tags: spring spring-boot 스프링 스프링부트 mongodb spring-data spring-data-mongodb MongoRepository injection missing bean
---

간단히 아래와 같은 package 구조의 웹서비스를 만들었을 때, MongoRepository bean을 찾지 못 하게 된다.

```java
package my.project.web;
@Controller
public class MyController {
    @Autowired
    private MyService service;
}
```

```java
package my.project.service;
@Service
public class MyService {
    @Autowired
    private MyMongo mongo;
}
```

```java
package my.project.repository;
@Repository
public interface MyMongo extends MongoRepository<String, String> {
}
package my.project;
@EnableAutoConfiguration
@EnableMongoRepositories
@ComponentScan(basePackages = "my.project")
public class MyApplication {
    	public static void main(String [] args) {
        SpringApplication.run(EyedropperApplication.class, args);
    }
}
```

에러에러에러

```bash
2016-01-22 03:28:50.403 ERROR 19424 --- [           main] o.s.boot.SpringApplication               : Application startup failed
.........
org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'service': Injection of autowired dependencies failed; nested exception is .........
.........
Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException: No qualifying bean of type [my.project.repository.MyMongo] found for dependency: expected at least 1 bean which qualifies as autowire candidate for this dependency. Dependency annotations: {@org.springframework.beans.factory.annotation.Autowired(required=true)}
```

원인은 project 구성 때문에 MyMongo bean이 생성되지 않는 것이다. MyMongo를 찾을 수 있도록 도와주자. 아래에서 맘에 드는걸로 하나 골라주면 간단히 끝. :D Solutions:

```java
@EnableMongoRepositories(basePackages = "my.project")
@EnableMongoRepositories(basePackages = "my.project.repository")
@EnableMongoRepositories(basePackageClasses = my.project.repository.MyMongo.class)
@EnableMongoRepositories(basePackageClasses = my.project.repository.RepositoryScanBase.class)
```
