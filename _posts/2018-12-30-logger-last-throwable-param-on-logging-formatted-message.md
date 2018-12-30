---
layout: post
title:  "[Logger] 형식화된 메시지(formatted message)를 로깅할 때 마지막 throwable(exception) 파라미터에 관해"
date:   2018-12-30 22:18:00 +0900
published: true
categories: [ java ]
tags: [ java, logging, logger, log, formatted message, message, format, parameter, param, argument, arg, throwable, exception, stacktrace ]
---

# 형식화된 메시지(formatted message) 로깅

아래 두 로깅 메시지는 똑같은 로그 메시지를 남긴다. 읽기도 쉽고 사용하기도 쉽기 때문에 아래 방법을 많이 사용한다.

```java
try {
    throw new RuntimeException("HUH!!");
} catch (Exception e) {
    log.error("Failed to fetch Account - ID: " + accountId + ", channel: " + channelId, e);
    log.error("Failed to fetch Account - ID: {}, channel: {}", accountId, channelId, e);
}
```

`{}`는 파라미터에서 차례로 하나씩 꺼내서 로깅 문자열 포메팅에 사용하게 된다.

```java
[main] ERROR kr.leocat.test.logging.Test - Failed to fetch Account - ID: 123, channel: 456
java.lang.RuntimeException: HUH!!
	at kr.leocat.test.logging.Test.main(Test.java:31)

[main] ERROR kr.leocat.test.logging.Test - Failed to fetch Account - ID: 123, channel: 456
java.lang.RuntimeException: HUH!!
	at kr.leocat.test.logging.Test.main(Test.java:32)
```


# 더 많은/적은 개수의 파라미터

그런데, 궁금해졌다. `{}`는 2개인데, 파라미터를 3개, 4개 등 더 많이 주면 어떻게 될까?? 반대로 적게 1개만 주면 어떻게 될까??

```java
try {
    throw new RuntimeException("HUH!!");
} catch (Exception e) {
    log.error("Failed to fetch Account - ID: {}, channel: {}", accountId, channelId, name, age, e);
    log.error("Failed to fetch Account - ID: {}, channel: {}", accountId, e);
}

```

우선, 위처럼 `{}`는 2개지만 파라미터로 4개(`accountId`, `channelId`, `name`, `age`)와 예외(`e`)를 주면, 남는 파라미터(`name`, `age`)는 무시된다. 그리고 파라미터가 모자라는 경우는 메시지가 포메팅되지 않고 `{}` 그대로 로깅된다.

```java
[main] ERROR kr.leocat.test.logging.Test - Failed to fetch Account - ID: 123, channel: 456
java.lang.RuntimeException: HUH!!
	at kr.leocat.test.logging.Test.main(Test.java:31)

[main] ERROR kr.leocat.test.logging.Test - Failed to fetch Account - ID: 123, channel: {}
java.lang.RuntimeException: HUH!!
	at kr.leocat.test.logging.Test.main(Test.java:32)
```


# 마지막 파라미터의 비밀 (특히, throwable)

그런데, 파라미터 개수가 부족해도 마지막에 넘겨준 예외(`e`)는 사용되지 않았다. 왜일까??

{% include image.html file='/assets/img/2018-12-30-logger-last-throwable-param-on-logging-formatted-message.png' alt='overloaded methods' %}

logger 마다 다르겠지만 오버로딩 된 메소드들을 보면, `error(String msg, Throwable t)` 같이 throwable을 던질 수 있는 메소드가 하나 있다. 이 메소드를 쓰면 예외의 stacktrace를 함께 남겨줘서 디버깅할 때 유용하다. 이런 습관이 다른 오버라이딩된 메소드를 쓸 때도 이어지다 보니 `error(String format, Object... arguments)` 같은 메소드를 쓴 때도 무의식적으로 마지막에 throwable을 넘겨주게 된다.

몇 가지 logger 소스를 받아서 따라가 보면, 메시지를 포메팅할 때 마지막 파라미터에 throwable이 있으면 뽑아내 버리는 로직이 있다.

```java
// logback(v1.2.3) - ch.qos.logback.classic.spi.LoggingEvent
public LoggingEvent(String fqcn, Logger logger, Level level, String message, Throwable throwable, Object[] argArray) {
    // .. 생략

    if (throwable == null) {
        throwable = extractThrowableAnRearrangeArguments(argArray);
    }

    if (throwable != null) {
        this.throwableProxy = new ThrowableProxy(throwable);
        LoggerContext lc = logger.getLoggerContext();
        if (lc.isPackagingDataEnabled()) {
            this.throwableProxy.calculatePackagingData();
        }
    }

    timeStamp = System.currentTimeMillis();
}

// slf4j(v1.7.20) - org.slf4j.helpers.MessageFormatter#arrayFormat(java.lang.String, java.lang.Object[])
final public static FormattingTuple arrayFormat(final String messagePattern, final Object[] argArray) {
    Throwable throwableCandidate = getThrowableCandidate(argArray);
    Object[] args = argArray;
    if (throwableCandidate != null) {
        args = trimmedCopy(argArray);
    }
    return arrayFormat(messagePattern, args, throwableCandidate);
}
```

`logback`에서는 `LoggingEvent`를 만들 때 `extractThrowableAnRearrangeArguments()` 메소드에서, `slf4j`는 로깅할 메시지인 `FormattingTuple`를 만들 때 `getThrowableCandidate()` 메소드에서 throwable을 꺼낸다. 두 구현체의 같은 점은 **마지막 파라미터만** `throwable`인지 체크해서 뽑는다는 것이다.


# 결론

파라미터에서 마지막 throwable을 뽑아내서 stacktrace를 찍어줄지는 구현체 마다 다르겠지만, 많은 구현체에서 구현하고 있으니 편하게 쓸 수 있겠다. (그래도 진짜 되는 구현체인지 한번 확인은;;)

```java
log.error("Failed to fetch Account - ID: {}", accountId, e);
```
