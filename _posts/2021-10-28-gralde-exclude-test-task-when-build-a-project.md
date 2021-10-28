---
layout: post
title:  "[Gradle] Java 프로젝트 빌드할 때 테스트 제외하기"
date:   2021-10-28 22:18:00 +0900
published: true
categories: [ gradle ]
tags: [ gradle, exclude, test, verification, task, build ]
---

# Gradle Java plugin

Gradle java 프로젝트에서 `build` task를 실행하면, `check` task도 함께 돌게 된다.

{% include image.html file='/assets/img/2021/2021-10-28-gralde-exclude-test-task-when-build-a-project.png' alt='Java plugin - tasks' %}

[Java plugin](https://docs.gradle.org/current/userguide/java_plugin.html)을 사용하는 java 프로젝트인 경우에는 위와 같은 task 연관관계가 생긴다. [Java Library plugin](https://docs.gradle.org/current/userguide/java_library_plugin.html)도 Java plugin을 확장한 것이기 때문에 동일한 연관관계를 가진다.

```kotlin
// build.gradle.kts

plugins {
    java
    // 또는
    `java-library`
}
```


# check task

`check` task는 `test` task와 같은 verification task를 실행하는 task이다. 우리팀 같은 경우는 유닛테스트는 `test` task로, 통합테스트는 `integrationTest` task, js 테스트는 `npmTest` task를 만들어 두고 별도로 돌린다. `integrationTest` 는 오래 걸려서 유닛테스트만 돌리고 싶은 경우가 있어서 테스트들을 분리해 두었다. 그리고 이 task들은 verification task 이기 때문에 `check` task에 dependsOn으로 연관관계를 추가해 두었다.

```kotlin
// build.gralde.kts

tasks.check {
    dependsOn("integrationTest")
}

// npmTest가 필요한 admin 모듈
tasks.check {
    dependsOn(npmTest)
}
```


# 테스트 제외하기

운영에서 배포를 할 때면 빠른 배포가 필요할 때가 있다. 배포 자체가 오래 걸리면 작게 자주 배포하는데 걸림돌이 되기도 하지만, 더 큰 문제는 배포에 문제가 생겨서 핫픽스를 할 때이다. 배포 도중에 문제 발견으로 버그를 수정하고 빨리 배포를 하고 싶은데, 이 때 `build` task를 dependsOn 하고 있는 `check` task가 실행되면서 테스트하는데 시간을 다 뺏긴다.

Verification task를 실행하는 `check` task를 실행하지 않으려면 아래처럼 `-x` 옵션을 주면 된다.

```kotlin
$ ./gradlew build -x check --parallel
```

그래서 우리팀은 배포할 때 `check` task를 실행하지 않으려고 `-x check` 옵션을 주고 테스트는 실행하지 않는다. 단, 여기에는 CI가 테스트를 항상 돌려주고 있다는 전제가 깔려 있다. 테스트가 항상 돌아가고 있기 때문에 배포 전에 배포할 브랜치도 이미 테스트가 통과된 상태이다. 팀에서 올초 까지는 CI/CD Jenkins를 구성해서 변경된 코드를 테스트했었고, 지금은 [GitLab CI/CD](https://docs.gitlab.com/ee/ci/)를 통해서 push 되는 모든 코드를 테스트하고 있다.


## 참고

- [The Java Plugin Lifecycle Tasks - Gradle docs](https://docs.gradle.org/current/userguide/java_plugin.html#lifecycle_tasks)
- [The Java Library Plugin - Gradle docs](https://docs.gradle.org/current/userguide/java_library_plugin.html)
- [GitLab CI/CD](https://docs.gitlab.com/ee/ci/)
