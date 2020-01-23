---
layout: post
title:  "[Gradle] spring-boot 플러그인으로 실행가능한 executable jar 만들기 (feat. 그런데 실행이 안 돼요)"
date:   2020-01-23 22:38:00 +0900
published: true
categories: [ gradle ]
tags: [ gradle, build, package, executable jar, executable, jar, spring, spring-boot, bootJar ]
---

[Spring Boot Gradle Plugin](https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/html/)을 사용하면 손쉽게 실행가능한 jar(executable jar)를 만들 수 있다. jar 뿐만 아니라 war 등도 만들 수 있다. 설정도 아주 쉽다.

```kotlin
plugins {
  	java
  	id("org.springframework.boot") version "2.2.4.RELEASE"
}

apply(plugin = "io.spring.dependency-management")

dependencies {
    // 이 프로젝트는 web 샘플인 API 프로젝트이다.
  	implementation("org.springframework.boot:spring-boot-starter-web")
}

tasks.getByName<BootJar>("bootJar") {
    enabled = true
    mainClassName = "kr.leocat.test.api.ApiApplication"
}
```

`bootJar` task 설정에서 실행해 줄 application 클래스를 지정해 주기만 하면 된다. 그런데 생성된 jar 파일이 실행이 되지 않는 것이다!!

```bash
$ ./gradlew clean :promotion-api:build
$ cd build/libs
$ java -jar promotion-api.jar
no main manifest attribute, in promotion-api.jar
$
$ # 오잉?? 왜지?? jar 파일 까볼까??
$ jar xvf promotion-api.jar
 .. (생략) ..
$ cat META-INF/MANIFEST.MF
Manifest-Version: 1.0
$ # 아.. 진짜 아무 내용도 없다!!
```

jar 파일을 열어보니 `MANIFEST.MF` 파일에 정말 아무 것도 안 들어 있다. `--info` 옵션을 주고 gradle 빌드를 다시 돌려보니, `bootJar` task가 실행된 후에 `jar` task가 실행되고 같은 경로에 jar 파일을 생성하는 것을 볼 수 있었다.

```bash
$ ./gradlew clean :promotion-api:build --info

  .. (생략) ..

> Task :promotion-api:bootJar
Excluding []
Caching disabled for task ':promotion-api:bootJar' because:
  Build cache is disabled
Task ':promotion-api:bootJar' is not up-to-date because:
  Output property 'archiveFile' file /{PATH_TO_PROJECT}/promotion-api/build/libs/promotion-api.jar has been removed.
:promotion-api:bootJar (Thread[Execution worker for ':' Thread 5,5,main]) completed. Took 0.773 secs.
:promotion-api:inspectClassesForKotlinIC (Thread[Execution worker for ':' Thread 5,5,main]) started.

> Task :promotion-api:inspectClassesForKotlinIC
Caching disabled for task ':promotion-api:inspectClassesForKotlinIC' because:
  Build cache is disabled
Task ':promotion-api:inspectClassesForKotlinIC' is not up-to-date because:
  Output property 'classesListFile$kotlin_gradle_plugin' file /{PATH_TO_PROJECT}/promotion-api/build/kotlin/promotionapijar-classes.txt has been removed.
file or directory '/{PATH_TO_PROJECT}/promotion-api/build/classes/java/main', not found
:promotion-api:inspectClassesForKotlinIC (Thread[Execution worker for ':' Thread 5,5,main]) completed. Took 0.002 secs.
:promotion-api:jar (Thread[Execution worker for ':' Thread 5,5,main]) started.

> Task :promotion-api:jar
Caching disabled for task ':promotion-api:jar' because:
  Build cache is disabled
Task ':promotion-api:jar' is not up-to-date because:
  Output property 'archiveFile' file /{PATH_TO_PROJECT}/promotion-api/build/libs/promotion-api.jar has changed.
:promotion-api:jar (Thread[Execution worker for ':' Thread 5,5,main]) completed. Took 0.008 secs.
:promotion-api:assemble (Thread[Execution worker for ':' Thread 5,5,main]) started.

  .. (생략) ..

```

원인은 멀티 모듈 프로젝트에서 parent 모듈에 `jar` task가 enable 되어 있어서 둘 다 실행되는 것이었고, `jar`가 나중에 실행되어 `bootJar`에서 생성한 executable jar 파일을 덮어버린 것이다.

```kotlin
tasks.jar {
    enabled = true
}
```


# 해결 방법 #1 - jar task를 disable 시킨다

이 모듈은 API 서버 모듈이라 일반 jar 파일이 필요 없었다. 그래서 `bootJar` task만 실행하고 `jar` task는 실행되지 않도록 하면 된다.

```kotlin
tasks.jar {
    enabled = false
}

tasks.bootJar {
    enabled = true
    mainClassName = "woowa.promotion.api.PromotionApiApplication"
}
```


# 해결 방법 #2 - bootJar에 classifier 설정을 해준다

만일 executable jar 이외에 일반 jar 파일도 필요해서 `jar` task도 필요하다면, classifier를 지정해 주면 된다.

```kotlin
tasks.jar {
    enabled = true
}

tasks.bootJar {
    enabled = true
//  classifier는 deprecated 되었으니 archiveClassifier를 설정
//  classifier = "boot"
    archiveClassifier.set("boot")
    mainClassName = "woowa.promotion.api.PromotionApiApplication"
}
```

생성된 jar 파일을 확인해 보면 `archiveClassifier`로 지정해 준 `boot`가 붙은 jar가 별도로 생성된 것을 확인할 수 있다. executable jar 이기 때문에 라이브러리들이 함께 포함되어 있어서 파일 사이즈가 크다.

```bash
$ ls -al
total 120192
drwxr-xr-x  4 leocat  staff       128  1 24 03:05 .
drwxr-xr-x  8 leocat  staff       256  1 24 03:05 ..
-rw-r--r--  1 leocat  staff  60672907  1 24 03:05 promotion-api-boot.jar
-rw-r--r--  1 leocat  staff      9556  1 24 03:05 promotion-api.jar
```


# 참고

- [Spring Boot Gradle Plugin](https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/html/)
