---
layout: post
title:  "[JaCoCo] Kotlin 1.5 버전업 시 JaCoCo 프러그인 예외 발생: Unexpected SMAP line: *S KotlinDebug"
date:   2021-06-13 22:18:00 +0900
published: true
categories: [ jacoco ]
tags: [ jacoco, gradle, plugin, exception, bug ]
---

# 문제

Kotlin 버전 1.5로 버전업 시 JaCoCo 에서 이런 오류를 뱉는다.: `Unexpected SMAP line: *S KotlinDebug`

(`--stacktrace` 옵션과 함께 실행하면 보면 보인다.)

```bash
$ ./gradlew jacocoTestReport --stacktrace

    .. (생략) ..

Caused by: : Error while creating report
        at org.jacoco.ant.ReportTask.execute(ReportTask.java:502)
        at org.apache.tools.ant.UnknownElement.execute(UnknownElement.java:292)

    .. (생략) ..

Caused by: java.lang.IllegalStateException: Unexpected SMAP line: *S KotlinDebug
        at org.jacoco.core.internal.analysis.filter.KotlinInlineFilter.getFirstGeneratedLineNumber(KotlinInlineFilter.java:98)
        at org.jacoco.core.internal.analysis.filter.KotlinInlineFilter.filter(KotlinInlineFilter.java:44)
        at org.jacoco.core.internal.analysis.filter.Filters.filter(Filters.java:58)

    .. (생략) ..
```


# 해결 방법

문제는 JaCoCo Gradle 플러그인이 1.5 버전에서 만들어내는 SMAP 을 지원하지 못 해서 그런 것 같다.
JaCoCo `0.8.7` 이상 버전을 사용하면 된다.

```kotlin
jacoco {
    toolVersion = "0.8.7"
}
```


# Jenkins 플러그인 버전업

CI 등을 위해 Jenkins 에서 빌드를 돌리는 경우, [JaCoCo 플러그인](https://plugins.jenkins.io/jacoco/)을 `3.2.0` 이상 버전으로 버전업 해야 한다.
단점은, Jenkins `2.277.1` 이상 버전이 필요하기 때문에 Jenkins 까지 버전업을 해야 할 수 있다.

{% include image.html file='/assets/img/2021/2021-06-13-jacoco-Unexpected-SMAP-line-S-KotlinDebug-kotlin-1.5.png' alt='Jenkins JaCoCo Plugin' %}


# 참고

- [Compiling with language version 1.5 breaks JaCoCo: "Unexpected SMAP line: *S KotlinDebug"](https://youtrack.jetbrains.com/issue/KT-44757)
- [Support SMAP generated by Kotlin 1.5 compiler (Support JSR-45 spec) #1155](https://github.com/jacoco/jacoco/issues/1155)
- [Jenkins JaCoCo 플러그인](https://plugins.jenkins.io/jacoco/)
