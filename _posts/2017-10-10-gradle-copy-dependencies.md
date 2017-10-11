---
layout: post
title:  "[Gradle] 프로젝트 dependency 다운로드"
date:   2017-10-10 23:38:00 +0900
published: true
categories: [ gradle ]
tags: [ gradle, copy, dependency, build, download, library, maven ]
---

Gradle을 사용하면 개발할 때 dependency 관리가 편하다. 하지만, 개발이 끝나면?? 서비스를 위해 서버로 옮긴다거나 하려면 dependency에 걸려있는 라이브러리 파일을 모두 복사해야 하고, 그 라이브러리가 사용하고 있는 라이브러리 까지 복사해서 클래스패스에 넣어줘야 한다.

Maven을 사용하고 있다면 기본적으로 포함되어 있는 `dependency:copy-dependencies` 플러그인([dependency:copy-dependencies](http://maven.apache.org/plugins/maven-dependency-plugin/copy-dependencies-mojo.html))으로 쉽게 되지만, Gradle은 그런게 없다. T_T

그렇다면 만들어서 써야지.. 아래 코드는 설정의 compile과 runtime에 dependency가 걸린 라이브러리(`configurations.runtime`)를 빌드경로(`$buildDir`) 아래의 dependencies 폴더로 복사하게 된다.

```groovy
task copyDependencies(type: Copy) {
    into "$buildDir/dependencies"
    from configurations.runtime
}
```

요런 task를 하나 만들어두고 커맨드로 호출하면, `build/dependencies` 경로에 라이브러리 파일들이 차곡차곡..

```bash
$ ./gradlew copyDependencies
:copyDependencies

BUILD SUCCESSFUL

Total time: 1.87 secs
$ ls build/dependencies/
이런저런.jar
요런저런.jar
아무거나.jar
```


# 참고
- [How can I gather all my project’s dependencies into a folder?](https://discuss.gradle.org/t/how-can-i-gather-all-my-projects-dependencies-into-a-folder/7146)
- [Maven dependency:copy-dependencies](http://maven.apache.org/plugins/maven-dependency-plugin/copy-dependencies-mojo.html)
