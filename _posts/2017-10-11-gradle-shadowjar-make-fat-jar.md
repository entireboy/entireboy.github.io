---
layout: post
title:  "[Gradle] shadowJar로 dependency가 포함된 뚱뚱한 fat jar 만들기"
date:   2017-10-11 22:38:00 +0900
published: true
categories: [ gradle ]
tags: [ gradle, build, package, fat, jar, dependency, library ]
---

기본적으로 gradle로 빌드를 하면 내가 만들 코드만 컴파일 돼서 `build/libs` 경로에 jar 파일로 패키징된다. 개발이 끝나고 IDE를 벗어나 커맨드로 동작시키려면 dependency로 걸어서 사용하던 라이브러리 파일들은 내가 손수 찾아서 클래스패스에 넣어줘야 한다. (모든 라이브러리를 다운로드 받으려면 [이전 포스트]({{ site.baseurl }}{% post_url 2017-10-10-gradle-copy-dependencies %}) 참고)

클래스패스 잡고 하는거 다 귀찮으면, 내 jar 파일에 모든 라이브러리를 넣어주는 [shadowJar](http://imperceptiblethoughts.com/shadow/) 플러그인이 있다.

```groovy
apply plugin: 'java'
apply plugin: 'com.github.johnrengelman.shadow'

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.github.jengelman.gradle.plugins:shadow:2.0.1'
    }
}
```

jar task가 실행될 때 마다 shadowJar가 실행되게 하려면, 아래처럼 jar에 finalizedBy를 달아주면 된다.

```groovy
jar {
    finalizedBy shadowJar
    manifest {
        attributes 'Main-Class': 'my.package.MyMain'
    }
}
```

빌드를 하면 `build/libs` 경로에 2개 파일이 생성된다.
- xxx-version.jar: 원래 내 프로젝트
- xxx-version-all.jar: 라이브러리가 포함된 프로젝트

```bash
$ ./gradlew shadowJar
:compileJava
:processResources
:classes
:shadowJar

BUILD SUCCESSFUL

Total time: 8.315 secs
$ ls build/libs
xxx-yyy-1.0-SNAPSHOT-all.jar

$ # jar task에 'finalizedBy shadowJar'를 달아놓은 경우
$ ./gradlew build
:compileJava
:processResources
:classes
:jar
:shadowJar
:assemble
:compileTestJava NO-SOURCE
:processTestResources NO-SOURCE
:testClasses UP-TO-DATE
:test NO-SOURCE
:check UP-TO-DATE
:build

BUILD SUCCESSFUL

Total time: 7.901 secs
$ ls build/libs
xxx-yyy-1.0-SNAPSHOT-all.jar xxx-yyy-1.0-SNAPSHOT.jar
```

생성된 xxx-all.jar 파일을 열어보면 모든 라이브러리가 함께 들어 있는걸 볼 수 있다.




# 참고

- [shadowJar](http://imperceptiblethoughts.com/shadow/)
- [[Gradle] 프로젝트 dependency 다운로드]({{ site.baseurl }}{% post_url 2017-10-10-gradle-copy-dependencies %})
