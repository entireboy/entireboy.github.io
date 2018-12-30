---
layout: post
title:  "[Java] GC 로그 남기기"
date:   2018-06-26 23:18:00 +0900
published: true
categories: [ java ]
tags: [ java, logging, gc, log, logger ]
---

Java 프로세스에서 GC가 발생할 때 마다 로그를 남기고 싶을 때, `java` 명령에 이런 옵션을 주면 된다.

```bash
-verbose:gc -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:/my/tomcat/log/gc.log -XX:+UseGCLogFileRotation -XX:GCLogFileSize=1m -XX:NumberOfGCLogFiles=100
```

`-Xloggc`옵션으로 로그파일을 지정할 수 있다. `-XX:+PrintGCCause`는 Java 1.7.0_45 버전 이상에서만 가능하다고 한다.

모아진 GC 파일은 GC 분석툴로 보면 이쁘게 잘 보여준다. <http://gceasy.io/> 등의 서비스를 이용하면 툴 설치 없이 분석할 수 있다.


# 참고

- [Understanding the Java Garbage Collection Log](https://dzone.com/articles/understanding-garbage-collection-log)
- [How to Enable Garbage Collection (GC) Logging](https://confluence.atlassian.com/confkb/how-to-enable-garbage-collection-gc-logging-300813751.html)
- [GCeasy](http://gceasy.io/)
