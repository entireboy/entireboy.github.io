---
layout: post
title:  "[Querydsl] Kotlin Gradle프로젝트에 Querydsl 설정 (feat. kapt)"
date:   2020-01-30 22:18:00 +0900
published: true
categories: [ querydsl ]
tags: [ querydsl, kotlin, gradle, project, config, kapt, jpa, IntelliJ ]
---

Kotlin과 함께 Querydsl을 쓰려고 설정하는데, 정말 후우 T_T (그래서 기록용으로 메모)


# Gradle 설정

> Gradle 설정 파일은 kotlin DSL로 작성되었다. spring-data-jpa가 함께 설정된 프로젝트에 Querydsl만 추가했고, 기타 spring 관련 코드는 샘플에서 제외했다.

```kotlin
// /build.gradle.kts

buildscript {
  repositories {
    maven("https://plugins.gradle.org/m2/")
    mavenCentral()
  }

  dependencies {
    classpath("gradle.plugin.com.ewerk.gradle.plugins:querydsl-plugin:1.0.10")
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.61")
  }
}

plugins {
  kotlin("jvm") version "1.3.61
  kotlin("kapt") version "1.3.61" // annotation processing을 위한 kapt

  idea
}

dependencies {
  api("com.querydsl:querydsl-jpa:4.2.2")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

  // kapt로 dependency를 지정해 준다.
  // kotlin 코드가 아니라면 kapt 대신 annotationProcessor를 사용한다.
  kapt("com.querydsl:querydsl-apt:4.2.2:jpa") // ":jpa 꼭 붙여줘야 한다!!"
  kapt("org.hibernate.javax.persistence:hibernate-jpa-2.1-api:1.0.2.Final")
}

// 생성된 QClass들을 intelliJ IDEA가 사용할 수 있도록 소스코드 경로에 추가해 준다.
idea {
  module {
    val kaptMain = file("build/generated/source/kapt/main")
    sourceDirs.add(kaptMain)
    generatedSourceDirs.add(kaptMain)
  }
}
```

여기서 **중요한 점** 은 annotation processing을 할 수 있도록 `kapt`를 사용하는 것과, `querydsl-apt` dependency 마지막에 `:jpa`를 붙여주는 것이다.

```kotlin
kapt("com.querydsl:querydsl-apt:4.2.2:jpa")
```

마지막에 `:jpa`를 붙여주는 것은 `JPAAnnotationProcessor`를 설정해 주는 것이다. ([Integrating Spring Boot + QueryDSL into Gradle Build](https://discuss.gradle.org/t/integrating-spring-boot-querydsl-into-gradle-build/15421/2) 참고)


# Grenerate QClasses

다음 명령을 실행해서 Querydsl의 도메인 클래스인 QClass를 생성한다.

```bash
$ ./gradlew clean compileKotlin
```

`build/generated/source/kapt/main`에 클래스 파일들이 생성되는 것을 볼 수 있다.


# Querydsl 사용하기

이제 생성된 QClass들을 사용한다. 조심할 점은 [Spring Data JPA 문서](https://docs.spring.io/spring-data/jpa/docs/2.2.2.RELEASE/reference/html/#repositories.custom-implementations)에 설명된 것 처럼 custom 인터페이스와 구현한 클래스의 이름을 잘 맞춰줘야 한다.

> The most important part of the class name that corresponds to the fragment interface is the `Impl` postfix.

`CustomizedOrderRepository`라고 인터페이스를 만들었다면, 구현체 이름은 인터페이스에 `Impl`을 붙인 형태인 `CustomizedOrderRepositoryImpl`이라고 해야 한다. (아.. 구현체만 rename 했다가 클래스 못 찾아서 것나 삽질;; 이 내용은 글 가장 아래에 다시 다룬다.)

```kotlin
// /src/main/kotlin/kr/leocat/test/order/OrderRepository.kt
package kr.leocat.test.order

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository : JpaRepository<Order, Long>, CustomizedOrderRepository
```

```kotlin
// /src/main/kotlin/kr/leocat/test/order/CustomizedOrderRepository.kt
package kr.leocat.test.order

interface CustomizedOrderRepository {
  fun findMyOrder(userId: Long): List<Order>
}
```

```kotlin
// /src/main/kotlin/kr/leocat/test/order/CustomizedOrderRepositoryImpl.kt
package kr.leocat.test.order

import kr.leocat.test.order.QOrder.order
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport

class CustomizedOrderRepositoryImpl() : QuerydslRepositorySupport(Order::class.java), CustomizedOrderRepository {
  fun findMyOrder(userId: Long): List<Order> =
    from(order)
      .where(order.userId.eq(userId))
      .fetch()
}
```


# Spring Data JPA 네이밍 문제

구현체의 이름을 변경하는 과정에서 인터페이스 이름과 구현체 `Impl` 클래스 이름이 맞지 않았다. `CustomizedOrderRepository` 인터페이스와 `CustomizedOrderRepositoryImpl` 구현 클래스의 이름이 맞지 않으면 아래와 같은 오류를 만날 수 있다. 인터페이스와 클래스 이름을 꼭 다시 확인하자. 인터페이스 이름 마지막에 `Impl`이 붙어 있는지..

```bash
java.lang.IllegalStateException: Failed to load ApplicationContext

      .. (생략) ..

Caused by: java.lang.IllegalArgumentException: Failed to create query for method public abstract java.util.List kr.leocat.test.order.CustomizedOrderRepository.findMyOrder(long)! No property findMyOrder found for type Order!
	at org.springframework.data.jpa.repository.query.PartTreeJpaQuery.<init>(PartTreeJpaQuery.java:102)
	at org.springframework.data.jpa.repository.query.JpaQueryLookupStrategy$CreateQueryLookupStrategy.resolveQuery(JpaQueryLookupStrategy.java:106)

      .. (생략) ..

Caused by: org.springframework.data.mapping.PropertyReferenceException: No property findMyOrder found for type Order!
	at org.springframework.data.mapping.PropertyPath.<init>(PropertyPath.java:94)
	at org.springframework.data.mapping.PropertyPath.create(PropertyPath.java:382)
```

[Spring Data JPA 문서](https://docs.spring.io/spring-data/jpa/docs/2.2.2.RELEASE/reference/html/#repositories.custom-implementations)에는 인터페이스 이름에 `Impl`을 붙인 이름으로 구현 클래스를 만들라고 하지만, 예전부터 아래처럼 인터페이스는 `Custom`으로 끝나고 구현 클래스는 `Impl`로 끝나도록 사용했었고 아직 까지도 문제는 없다. 그래도 언제 바뀔지 모르니 이제는 뒤에 `Impl` 붙여 보기로..

- OrderRepositoryCustom
- OrderRepositoryImpl

> The most important part of the class name that corresponds to the fragment interface is the `Impl` postfix.


# 참고

- [kotlin-querydsl - kotlin-examples](https://github.com/Kotlin/kotlin-examples/tree/master/gradle/kotlin-querydsl)
- [org.jetbrains.kotlin.kapt - Kotlin plugins for Gradle](https://plugins.gradle.org/plugin/org.jetbrains.kotlin.kapt)
- [Annotation Processing with Kotlin](https://kotlinlang.org/docs/reference/kapt.html)
- [4.6. Custom Implementations for Spring Data Repositories - Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/2.1.3.RELEASE/reference/html/#repositories.custom-implementations)
- [Integrating Spring Boot + QueryDSL into Gradle Build - Gradle Forums](https://discuss.gradle.org/t/integrating-spring-boot-querydsl-into-gradle-build/15421/2)
