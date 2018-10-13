---
layout: post
title:  "[Java] 로깅할 때 marker로 필터링하기"
date:   2018-06-29 23:18:00 +0900
published: true
categories: [ java ]
tags: [ java, log, logging, log4j, slf4j, logback, marker, filter ]
---

개발 시에만 로깅을 하고 실서비스에서는 안 보여주고 싶은 로그가 있을 때, marker를 쓰면 은근 필터링하기 좋다. 또는, 개인정보가 담긴 로깅을 할 때 실서비스에서는 로깅하지 않도록 필터링할 수 있다. marker도 함께 로깅해서 로깅파일을 분석에 사용할 수도 있다. 일부 appender를 분리하고 특정 marker가 달린/달리지않은 경우만 로깅을 할 수도 있다.


# Marker

예를 들어, 개발 시에만 보고 싶은 메시지 전송 관련 로깅을 한다고 하면, 아래처럼 `MESSAGE`라는 marker를 만들고 로깅할 때 마다 marker를 지정해 준다. (전체 샘플은 [요기](https://github.com/entireboy/blog-sample/tree/master/logger)에)

```java

public class Publisher {
  private static final Marker MESSAGE_MARKER = MarkerFactory.getMarker("MESSAGE");
  private static final Logger log = LoggerFactory.getLogger(MarkerFilterSample.class);

  public static void publish(String message) {
    log.debug("Attempting to publish message");
    messageTemplate.publish("message");
    log.debug(MESSAGE_MARKER, "Message published: " + message);
  }
}
```

자.. 아래의 `logback.xml` 설정으로 실행을 해 보자.

```xml
<?xml version="1.0" encoding="UTF-8"?>

<configuration>

  <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
      <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - [%marker] %msg%n</pattern>
    </encoder>
  </appender>

  <root level="DEBUG">
    <appender-ref ref="STDOUT" />
  </root>

</configuration>
```

로깅할 때 marker도 보여주려면 설정에서 `%marker`라고 적어주면 된다. 음.. marker를 볼 수 있는거 말고 별로 다른게 없는데??

```bash
17:46:14.812 [main] DEBUG k.l.t.s.logger.MarkerFilterSample - [] Attempting to publish message
17:46:14.815 [main] DEBUG k.l.t.s.logger.MarkerFilterSample - [MESSAGE] Message published: THIS IS MY PERSONAL MESSAGE
```

# Marker Filter

그럼 이 marker로 필터링을 할 수 있게 필터를 추가해 보자. 위의 xml 설정에서 `turboFilter`만 추가했다.

```xml
<?xml version="1.0" encoding="UTF-8"?>

<configuration>

  <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
      <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - [%marker] %msg%n</pattern>
    </encoder>
  </appender>

  <turboFilter class="ch.qos.logback.classic.turbo.MarkerFilter">
    <Name>MESSAGE_FILTER</Name>
    <Marker>MESSAGE</Marker>
    <OnMatch>DENY</OnMatch>
  </turboFilter>

  <root level="DEBUG">
    <appender-ref ref="STDOUT" />
  </root>

</configuration>
```

marker `MESSAGE`를 발견하면 `DENY` 처리해서 로깅이 되지 않도록 한다. 실행해 보면, `MESSAGE` marker가 달린 로그가 없어졌다. (전체 샘플은 [요기](https://github.com/entireboy/blog-sample/tree/master/logger)에)

```bash
17:48:21.321 [main] DEBUG k.l.t.s.logger.MarkerFilterSample - [] Attempting to publish message
```

filter 설정에 `OnMatch` 이외에 `OnMismatch`도 설정이 가능하다. 사용 가능한 값은 `ACCEPT`, `DENY`, `NEUTRAL`이고, default값은 `OnMatch`는 `NEUTRAL`이고 `OnMismatch`는 `DENY`이다.


# 참고

- [SLF4J markers example](https://examples.javacodegeeks.com/enterprise-java/slf4j/slf4j-markers-example/)
- [What are markers in Java Logging frameworks and what is a reason to use them?](https://stackoverflow.com/questions/16813032/what-are-markers-in-java-logging-frameworks-and-what-is-a-reason-to-use-them)
- [Best practices for using Markers in SLF4J/Logback](https://stackoverflow.com/questions/4165558/best-practices-for-using-markers-in-slf4j-logback)
- [Turbo Filter - logback doc](https://logback.qos.ch/manual/filters.html#TurboFilter)
