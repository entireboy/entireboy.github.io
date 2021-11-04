---
layout: post
title:  "[Kotlin] Kotlin 코드에서 로깅 (kotlin-logging)"
date:   2021-11-04 22:18:00 +0900
published: true
categories: [ kotlin ]
tags: [ kotlin, logging, slf4j, lombok, kotlin-logging, kommons-logging ]
---

Lombok을 쓰지 못 하는 Kotlin 코드에서 가장 불편했던건 `@Slf4j` annotation을 쓰지 못 하는 것이었다. 다른 것들은 대체제가 많은데, 이 annotation은 너무 편해서 중독성이 상당했고 그만큼 쓰지 못 하는게 너무나도 불편했다. 그리고 대체제 자체도 찾기가 너무 어려웠다. [이제는 Lombok을 쓸 수 있게 되었](https://kotlinlang.org/docs/lombok.html)지만, 일부 annotation만을 지원하고 대체제가 있는 상황에서 굳이 Lombok을 쓰고 싶지는 않다. 나 이외의 다른 팀원들도 비슷한 생각이었다.

`@Slf4j`의 여러 대체제를 테스트해 본 내용과 현재 적용을 고려하는 방법을 까먹을까봐 적어둔다. (이 방법도 언젠가 테스트해 본 방법이 될지도..)


# 이런저런 시도들

## kommons-logging

처음엔 디밥님이 만들어 두신 [kommons-logging](https://github.com/debop/kotlin-design-patterns/tree/master/kommons-logging)을 사용했다. companion object를 추가로 상속받을 수 없다는 제약이 있었지만, companion object를 상속받을 일이 얼마나 있을까??

```kotlin
class SomeClass {
    companion object: KLogging()

    fun someMethod(param1:String) {
        try {
            log.debug { "Some logging... param1=$param1" }
        } catch(e:Exception) {
            log.error(e) { "Fail to execute method. param1=$param1" }
        }
    }
}
```

또는 `loggerOf` 메소드를 통해서 logger를 만들어 낼 수 있다.

```kotlin
private val log = loggerOf {}

fun method1(arg1:String, arg2:String) {
    log.debug { "method1 arg1=$arg1, arg2=$arg2" }
    // Somthing to do
}
```

게다가 로깅 메시지를 lambda로 전달한다. 귀찮게 `log.isDebugEnabled` 같은 확인을 하지 않아도 로깅하는 순간이 아니면 문자열을 만들지 않기 때문에 성능 저하가 없다.


## IntelliJ LiveTemplates

다음 시도로는, IntelliJ의 LiveTemplates로 `slf4j` 라는 짧은 키워드를 만들어 두고, 에디터에서 `slf4j` 키워드를 타이핑하면 자동으로 아래와 같은 logger 설정 코드를 만들도록 하는 것이었다.

```kotlin
private val log = org.slf4j.LoggerFactory.getLogger($CLASS_NAME$::class.java)
```

kommons-logging은 팀에서 프로젝트 몇 개에서 쓰다가 상속이라는 제약도 있어서 코드리뷰를 하면서 InteliJ의 LiveTemplates을 써보자고 해서 쓰게 되었다. 과거로 회귀하는 느낌이 들었지만, IntelliJ가 편하게 만들어 주니까 그래도 참고 쓸만 했다.


# kotlin-logging

이번에 적용해 보는 방식은 [kotlin-logging](https://github.com/MicroUtils/kotlin-logging)이다. 사실 사용법은 디밥님이 만들어둔 `loggerOf` 방식과 완전히 동일하다.

```kotlin
dependencies {
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.10")
    implementation("org.slf4j:slf4j-api:1.7.30")
}
```

위와 같이 dependency를 추가한다. SLF4J를 사용하기 때문에 `slfj4j-api`도 함께 필요하다.

```kotlin
val logger = KotlinLogging.logger {}
val ex = RuntimeException("으앜")

logger.trace { "hello ${heavyWorld(1)}" } // heavyWorld() 호출되지 않음
logger.debug { "hello ${heavyWorld(2)}" }
logger.info { "hello ${heavyWorld(3)}" }
logger.error(ex) { "hello ${heavyWorld(4)}" } // stacktrace 남기기

fun heavyWorld(num: Int): String {
    println("heavy method called: $num")
    return "world"
}

// 출력 - logging level debug
heavy method called: 2
02:23:04.691 [main] DEBUG org.jetbrains.kotlin.idea.scratch.generated.ScratchFileRunnerGenerated - hello world
heavy method called: 3
02:23:04.691 [main] INFO org.jetbrains.kotlin.idea.scratch.generated.ScratchFileRunnerGenerated - hello world
heavy method called: 4
02:23:04.694 [main] ERROR org.jetbrains.kotlin.idea.scratch.generated.ScratchFileRunnerGenerated - hello world
java.lang.RuntimeException: 으앜
	at org.jetbrains.kotlin.idea.scratch.generated.ScratchFileRunnerGenerated$ScratchFileRunnerGenerated.<init>(tmp.kt:10)
	at org.jetbrains.kotlin.idea.scratch.generated.ScratchFileRunnerGenerated.main(tmp.kt:39)
```

디밥님의 kommon-logging과 동일하게 lambda로 전달하기 때문에, 실제로 로깅하지 않으면 호출되지 않아 성능 저하가 없다. logging level이 `debug`로 설정되어 있기 때문에, `trace`로그는 남지 않고 `heavyWorld(1)`도 실행되지 않는다.

lambda를 전달하는 방법도 있지만, 아래와 같이 SLF4J를 그대로 사용해서 format과 arg를 전달해도 된다.

```kotlin
logger.trace("hello ${heavyWorld(1)}") // 로깅하지 않지만 heavyWorld() 호출됨
logger.debug("hello ${heavyWorld(2)}")
logger.info("hello ${heavyWorld(3)}")

logger.trace("hello {}", heavyWorld(11))) // 로깅하지 않지만 heavyWorld() 호출됨
logger.debug("hello {}", heavyWorld(12))
logger.info("hello {}", heavyWorld(13))

if (logger.isTraceEnabled) logger.trace("hello {}", heavyWorld(21))) // heavyWorld() 호출되지 않음
if (logger.isDebugEnabled) logger.debug("hello {}", heavyWorld(22))
if (logger.isInfoEnabled) logger.info("hello {}", heavyWorld(23))

// 출력 - logging level debug
heavy method called: 1
heavy method called: 2
02:23:04.687 [main] DEBUG org.jetbrains.kotlin.idea.scratch.generated.ScratchFileRunnerGenerated - hello world
heavy method called: 3
02:23:04.689 [main] INFO org.jetbrains.kotlin.idea.scratch.generated.ScratchFileRunnerGenerated - hello world

heavy method called: 11
heavy method called: 12
02:23:04.689 [main] DEBUG org.jetbrains.kotlin.idea.scratch.generated.ScratchFileRunnerGenerated - hello world
heavy method called: 13
02:23:04.690 [main] INFO org.jetbrains.kotlin.idea.scratch.generated.ScratchFileRunnerGenerated - hello world

heavy method called: 22
02:23:04.690 [main] DEBUG org.jetbrains.kotlin.idea.scratch.generated.ScratchFileRunnerGenerated - hello world
heavy method called: 23
02:23:04.691 [main] INFO org.jetbrains.kotlin.idea.scratch.generated.ScratchFileRunnerGenerated - hello world
```

차이점이라면, 현재 logging level은 `debug` 라서 `trace` 로그를 남기지 않아도, `heavyWorld()` 메소드가 호출된다는 점이다. 무거운 메소드가 호출될 수 있기 때문에, 이런 경우라면 `isTraceEnabled`로 확인하는 것이 좋다.

물론, SLF4J를 사용하기 때문에 `logger.isTraceEnabled` 와 같은 비교를 하지 않아도 로깅할 문자열을 만들어 내지는 않을 것이다. (참고: [What is the fastest way of (not) logging? - SLF4J FAQ](http://www.slf4j.org/faq.html#logging_performance)) 로깅할 문자열을 만들지는 않지만 arg는 준비하는지 메소드가 호출된다.


# 옆길로 샌 결론

로깅할 문자열은 lambda로 만드는게 가장 좋아 보인다. 실제로 로깅하는 순간이 오지 않으면 lambda는 아무 행동도 하지 않을테니 `isTraceEnabled`와 같은 체크도 필요 없고 로깅을 위해 무거운 메소드가 호출되는지 문자열이 만들어 지는지 고민할 필요가 없다.


# 참고

- [kotlin-logging](https://github.com/MicroUtils/kotlin-logging)
- [kommons-logging](https://github.com/debop/kotlin-design-patterns/tree/master/kommons-logging)
- [SLF4J](http://www.slf4j.org/)
- [What is the fastest way of (not) logging? - SLF4J](http://www.slf4j.org/faq.html#logging_performance)
- [Lombok compiler plugin - Kotlin Docs](https://kotlinlang.org/docs/lombok.html)
