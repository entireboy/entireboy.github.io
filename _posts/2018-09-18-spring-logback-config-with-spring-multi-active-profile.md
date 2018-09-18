---
layout: post
title:  "[Spring] 여러 active profile을 사용할 때 logback 설정"
date:   2018-09-18 23:18:00 +0900
published: true
categories: [ spring ]
tags: [ spring, logback, profile, active profile, logger, logging, java, log ]
---

이런 logback 설정이 있었다.

```xml
<if condition='"${spring.profiles.active}".equals("alpha")'>
     <then>
        <logger name="kr.leocat.test" level="INFO">
            <appender-ref ref="myLogger" />
        </logger>
    </then>
</if>
```

일반적인 (active profile을 1개만 쓰는) 경우에는 문제가 되지 않았다. 그런데, 이렇게 `String.equals()`로 비교하는 것은 여러개 profile을 active 시킬 수 있다는 점이 문제가 된다.

```java
-Dspring.profiles.active="alpha,dev"
-Dspring.profiles.active="alpha,swagger"
```

위와 같이 `alpha`와 함께 `dev`, `swagger` 같은 profile을 함께 사용한다면, `String.equals()`로 비교하는 logback 설정은 원하는 형태로 동작하지 않는다. 그렇다고 `String.contains()`로 비교하는 것은 부정확할 수도 있고, 정확한게 만들자니 `condition`이 길어질 수 있어서 설정파일을 읽기 어려워 지는 문제가 생길 수 있다.

이럴 때 사용할 만한 것으로 spring [logback extension](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-logging.html#boot-features-logback-extensions)이 있다.

```xml
<springProfile name="alpha">
    <logger name="kr.leocat.test" level="INFO">
        <appender-ref ref="myLogger" />
    </logger>
</springProfile>
```

위와 같이 설정하면 `-Dspring.profiles.active="alpha,dev"`처럼 여러 profile이 active 되어 있어도 `alpha` profile이 포함되어 있으면 해당 설정이 추가된다. 아래처럼 활용할 수 있다.

```xml
<springProfile name="dev">
    <!-- 'dev' profile이 active일 때 설정 -->
</springProfile>
<springProfile name="dev, alpha, beta">
    <!-- 'dev'나 'alpha', 'beta' profile이 active일 때 설정 -->
</springProfile>
<springProfile name="!production">
    <!-- 'production' profile이 active 아닐 때 설정 -->
</springProfile>
```

**주의점** `logback.xml` 파일은 일찍 로딩되기 때문에, spring이 이 설정을 제대로 로딩하려면 `logback-spring.xml` 파일로 설정하거나 `logging.config` property로 설정하기를 권장한다고 문서에 적혀 있다.


# 참고

- [Logback Extensions - Spring doc](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-logging.html#boot-features-logback-extensions)
- [여러 profile을 사용할 때 config override](https://stackoverflow.com/questions/23617831/what-is-the-order-of-precedence-when-there-are-multiple-springs-environment-pro)
