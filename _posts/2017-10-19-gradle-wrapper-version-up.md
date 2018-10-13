---
layout: post
title:  "[Gradle] gradle wrapper 버전 업"
date:   2017-10-19 21:38:00 +0900
published: true
categories: [ gradle ]
tags: [ gradle, wrapper, gradle-wrapper, build, version, version up, upgrade ]
---

[gradle-wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html)를 쓰는 경우 버전업이 필요할 때가 있다. 예를 들면, Spring 5는 gradle 4.0 이상 버전이 필요하다. gradle 3.x 버전으로 Spring 4 버전을 쓰다가 5로 버전업을 하려면 gradle 버전업까지 해야 한다.

버전업 명령은 간단하다.

```bash
$ ./gradlew wrapper --gradle-version=4.2.1 --distribution-type=bin
```

이제 새 버전으로 변경 됐고, IDE에서 gradle 프로젝트를 refresh하거나 다음 명령을 실행해 주면 새 버전의 wrapper를 다운받는다.

```bash
$ ./gradlew tasks
Downloading https://services.gradle.org/distributions/gradle-4.2.1-bin.zip
```

`gradle/wrapper/gradle-wrapper.properties` 파일의 `distributionUrl`이 새로 버전업 된 파일로 변경된다.


# 참고

- [Upgrade with the Gradle Wrapper - Gradle Installation](https://gradle.org/install/)
