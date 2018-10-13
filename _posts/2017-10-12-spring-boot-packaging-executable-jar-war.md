---
layout: post
title:  "[spring-boot] 실행가능한 jar/war 파일 만들기"
date:   2017-10-12 21:38:00 +0900
published: true
categories: [ spring ]
tags: [ spring, spring-boot, framework, package, packaging, executable, war, jar, tomcat, jetty ]
---

`spring-boot`로 개발을 마치고 실제 서비스를 해야 한다면, jar나 war 파일을 만들어서 배포를 해야 한다. 서비스할 때도 소스 코드를 들고 가서 `./gradlew bootRun` 같은 명령을 실행할 수는 없으니..

방법은 간단하다. `build` task로 빌드하면, `build/libs` 폴더에 실행 가능한 jar나 war 파일이 생생된다. spring-boot 프로젝트는 모든 라이브러리가 jar/war 파일 안에 포함된다.

```bash
$ gradle build
$ java -jar build/libs/myapp-0.0.1-SNAPSHOT.jar
```

war 파일이 필요하다면?? war 플러그인을 넣어주면 된다.

```groovy
apply plugin: 'war'

war {
    baseName = 'myapp'
    version = '0.0.1-SNAPSHOT'
}
```

파일명만 war로 바뀌었을뿐 실행도 jar와 동일하다.

```bash
$ gradle build
$ java -jar build/libs/myapp-0.0.1-SNAPSHOT.war
```

이렇게 실행하면, `spring-boot-starter-web`에 포함되어 있는 embedded Tomcat 위에서 실행된다. (Tomcat을 Jetty로 바꾸고 싶다면 [이전 포스트]({{ site.baseurl }}{% post_url 2017-10-09-spring-boot-use-jetty-instead-of-tomcat %}) 참조)

웹서버가 별도로 있어서, embedded Tomcat이나 embedded Jetty가 필요 없는 경우는 아래처럼 dependency에 `compile` 대신 `providedRuntime`으로 걸어주면 war 파일이 생성될 때 해당 웹서버 모듈은 제외하고 생성된다.

```groovy
apply plugin: 'war'

war {
    baseName = 'myapp'
    version = '0.0.1-SNAPSHOT'
}

repositories {
    jcenter()
    maven { url "http://repo.spring.io/libs-snapshot" }
}

dependencies {
    compile "org.springframework.boot:spring-boot-starter-web"
    providedRuntime "org.springframework.boot:spring-boot-starter-tomcat"
}
```


# 참고

- [Packaging executable jar and war files - Spring Boot Reference Guide](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#build-tool-plugins-gradle-packaging)
- [[spring-boot] Tomcat 대신 Jetty 사용하기]({{ site.baseurl }}{% post_url 2017-10-09-spring-boot-use-jetty-instead-of-tomcat %})
