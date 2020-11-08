---
layout: post
title:  "[Querydsl] Spring과 함께 사용할 때 UnsupportedOperationException 문제"
date:   2020-11-08 22:18:00 +0900
published: true
categories: [ querydsl ]
tags: [ querydsl, spring, spring boot, UnsupportedOperationException, dependency management, dependency, version, mismatch ]
---

# 문제

Spring Boot와 함께 Querydsl 을 사용하는 프로젝트를 설정하는데, 정말 불친절한 `UnsupportedOperationException` 발생 =_= 어찌나 불친절한지 예외 클래스 이름 빼면 아무 내용도 없음;;;

```bash
java.lang.UnsupportedOperationException
	at java.base/java.util.Collections$UnmodifiableMap.put(Collections.java:1457)
	at com.querydsl.jpa.JPQLSerializer.visitConstant(JPQLSerializer.java:327)
	at com.querydsl.core.support.SerializerBase.visit(SerializerBase.java:221)
	at com.querydsl.core.support.SerializerBase.visit(SerializerBase.java:36)
	at com.querydsl.core.types.ConstantImpl.accept(ConstantImpl.java:140)
	at com.querydsl.core.support.SerializerBase.handle(SerializerBase.java:122)
	at com.querydsl.core.support.SerializerBase.visitOperation(SerializerBase.java:301)
	at com.querydsl.jpa.JPQLSerializer.visitOperation(JPQLSerializer.java:426)
	at com.querydsl.core.support.SerializerBase.visit(SerializerBase.java:262)
	at com.querydsl.core.support.SerializerBase.visit(SerializerBase.java:36)
	at com.querydsl.core.types.OperationImpl.accept(OperationImpl.java:83)
	at com.querydsl.core.support.SerializerBase.handle(SerializerBase.java:122)
	at com.querydsl.jpa.JPQLSerializer.serialize(JPQLSerializer.java:220)
	at com.querydsl.jpa.JPAQueryBase.serialize(JPAQueryBase.java:60)
	at com.querydsl.jpa.JPAQueryBase.serialize(JPAQueryBase.java:50)
	at com.querydsl.jpa.impl.AbstractJPAQuery.createQuery(AbstractJPAQuery.java:98)
	at com.querydsl.jpa.impl.AbstractJPAQuery.createQuery(AbstractJPAQuery.java:94)
	at com.querydsl.jpa.impl.AbstractJPAQuery.fetch(AbstractJPAQuery.java:201)
	at kr.leocat.test.service.test.CustomizedUserRepositoryImpl.findAllOverTenYearsOld(CustomizedUserRepositoryImpl.kt:13)
  .. (생략) ..
	at kr.leocat.test.service.test.UserIntegrationTest.queryDslTest(UserIntegrationTest.groovy:33)
```

구세주 같은 나와 [동일한 문제]([https://stackoverflow.com/questions/62112312/spring-boot-querydsl-returns-caused-by-java-lang-unsupportedoperationexception](https://stackoverflow.com/questions/62112312/spring-boot-querydsl-returns-caused-by-java-lang-unsupportedoperationexception))를 겪는 분들 덕분에 해결


# 원인

프로젝트의 Querydsl dependency로 `4.2.2` 를 사용하고 있었는데, Spring Boot 버전을 최신 버전인 `2.3.5.RELEASE` 로 변경했다. Spring Boot dependency manager에서 참조하는 Querydsl 은 `4.3.1` 이었고, 이 버전이 서로 맞지 않아서 이런 에러가 발생했다.

다음은 사용 중인 `build.gradle.kts`(Kotlin dsl) 일부분이다.

```kotlin
dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:2.3.5")
    }
}

dependencies {
    api("com.querydsl:querydsl-jpa:4.2.2")
    kapt("com.querydsl:querydsl-apt:4.2.2:jpa")
}
```

다음은 Groovy dsl로 된 `build.gradle` 일부분이다.

```groovy
plugins {
    id 'io.spring.dependency-management' version '1.0.10.RELEASE'
}
dependencies {
    api 'com.querydsl:querydsl-jpa:4.2.2'
    annotationProcessor 'com.querydsl:querydsl-apt:4,2,2:jpa'
}
```


# 해결방법

간단히 Querydsl dependency의 버전을 제거하면 된다.

```kotlin
dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:2.3.5")
    }
}

dependencies {
    api("com.querydsl:querydsl-jpa")
    kapt("com.querydsl:querydsl-apt::jpa")
}
```

Groovy dsl 에서도 마찬가지로 버전을 모두 제거한다.

```groovy

plugins {
    id 'io.spring.dependency-management' version '1.0.10.RELEASE'
}
dependencies {
    api 'com.querydsl:querydsl-jpa'
    annotationProcessor group: 'com.querydsl', name: 'querydsl-apt', classifier: 'jpa'
}
```

혹은, Dependency management에 있는 `4.3.1` 보다 높은 버전(최신 버전인 `4.4.0`)을 사용하면 이런 오류가 발생하지는 않으니 꾸준히 버전업을 해주면 된다.

이런 라이브러리는 무조건 최신 버전으로 버전업을 하면 큰 문제가 발생할 수 있으니, 사용하는 Querydsl 버전을 명시해 두고 integration test를 잘 만들어 두고 버전업 할 때 마다 잘 확인하는 것이 좋을 수도 있다. 아주 간단해 보이고 따분한 `save()`, `fetch()` 등의 테스트도 만들어 둬야 한다.


# 참고

- [Spring Boot QueryDsl returns Caused by: java.lang.UnsupportedOperationException: null](https://stackoverflow.com/questions/62112312/spring-boot-querydsl-returns-caused-by-java-lang-unsupportedoperationexception)
- [Dependency versions - Spring Boot doc](https://docs.spring.io/spring-boot/docs/2.3.5.RELEASE/reference/html/appendix-dependency-versions.html)
