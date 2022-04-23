---
layout: post
title:  "[Resilience4j] Resilience4j.CircuitBreaker 서킷 열렸을 때 던져지는 CallNotPermittedException 예외"
date:   2022-04-23 22:18:00 +0900
published: true
categories: [ resilience4j ]
tags: [ resilience4j, circuit breaker, circuit, open, exception, fault tolerance ]
---

> 모든 설정은 꼭 눈으로 손으로 확인하자. 설정해 뒀으니 되겠지 하다가 다른 설정 때문에 먹지 않는 경우도 많다. 실제로 이렇게 확인하지 않고 넘어갔다가 장애로 번지는 경우도 많이 봤다.


Resilience4j 의 Circuit Breaker 는 서킷이 열리면 `CallNotPermittedException` 예외를 던진다. 서킷이 열렸는지, 열리기 전 또는 반만 열렸는지(half open)를 구분하는데 유용하다.

Spring Boot starer를 사용하면, 간단하게 `application.yml` 파일로 Circuit Breaker 와 Bulk Head 등을 설정할 수 있다.

```kotlin
dependencies {
    implementation("io.github.resilience4j:resilience4j-spring-boot2")
}
```

`CallNotPermittedException` 예외를 던지는지 테스트를 위해 간단히 설정해서 확인해 본다.


# COUNT_BASED 테스트

```yaml
resilience4j.circuitbreaker:
  configs:
    default:
      slidingWindowType: COUNT_BASED
      slidingWindowSize: 3
      permittedNumberOfCallsInHalfOpenState: 2
      waitDurationInOpenState: 10s # second
      failureRateThreshold: 50
      registerHealthIndicator: false
    another-my-circuit:
      baseConfig: default # 설정을 override 할 수 있다.
      slidingWindowSize: 100
```

역시 테스트를 위해 간단히 annotation을 사용한 샘플을 만든다. 1초 마다 호출을 하지만, 항상 예외를 던지는 샘플이다. 그리고 fallback은 2개가 있고, 하나는 `CallNotPermittedException` 을 다루고, 다른 하나는 모든 예외를 처리한다.

```kotlin
@Component
class MyTest(
    private val mySub: MySub,
) {
    fun call() {
        repeat(20) {
            mySub.throwException()
            TimeUnit.SECONDS.sleep(1) // sleep 1s
        }
    }
}

@Component
class MySub {
    @CircuitBreaker(name = "my-circuit", fallbackMethod = "throwExceptionFallback")
    fun throwException(): String {
        log.info("호출한다")
        throw RuntimeException("으앜!!")
    }

    fun throwExceptionFallback(e: Exception): String {
        log.error("에러닷")
        return "휴"
    }

    fun throwExceptionFallback(e: CallNotPermittedException): String {
        log.error("서킷 열림")
        return "휴"
    }
}
```

실행시키면 아래와 같은 로그를 확인할 수 있다.

1초 마다 로그를 찍으며, 처음 로그(`에러닷`) 3개는 서킷이 열리기 전 `slidingWindowSize` 설정 개수 만큼 `Exception` 을 파라미터로 가지는 fallback이 처리한 것을 볼 수 있다.

다음 로그(`서킷 열림`) 9개는 서킷이 열린 상태로, 메소드를 호출하지 않고 바로 리턴(shot-circuit)해서 `호출한다` 라는 로그가 찍히지 않았다. 서킷이 열린 상태에서 몇 초를 기다릴 것인지를 설정하는 `waitDurationInOpenState` 시간(`10초`) 만큼 유지된다.

half open 상태에서 정상인지 확인하기 위해 일부를 흘려보내는 설정인 `permittedNumberOfCallsInHalfOpenState` 로 `2` 가 설정되어 있다. 10초가 지나서 open 상태에서 half open 상태로 바뀌었고, 두 번의 호출을 흘려 보내서 `에러닷` 로그를 두 번 찍게 된다.

역시나 예외가 던져졌기 때문에 서킷은 계속 open 상태를 유지한다.

```bash
INFO  03:34:01.091 [main] kr.leocat.test.circuit.MySub - 호출한다 []
ERROR 03:34:01.094 [main] kr.leocat.test.circuit.MySub - 에러닷 []
INFO  03:34:02.095 [main] kr.leocat.test.circuit.MySub - 호출한다 []
ERROR 03:34:02.095 [main] kr.leocat.test.circuit.MySub - 에러닷 []
INFO  03:34:03.100 [main] kr.leocat.test.circuit.MySub - 호출한다 []
ERROR 03:34:03.103 [main] kr.leocat.test.circuit.MySub - 에러닷 []
ERROR 03:34:04.108 [main] kr.leocat.test.circuit.MySub - 서킷 열림 []
ERROR 03:34:05.111 [main] kr.leocat.test.circuit.MySub - 서킷 열림 []
ERROR 03:34:06.115 [main] kr.leocat.test.circuit.MySub - 서킷 열림 []
ERROR 03:34:07.119 [main] kr.leocat.test.circuit.MySub - 서킷 열림 []
ERROR 03:34:08.124 [main] kr.leocat.test.circuit.MySub - 서킷 열림 []
ERROR 03:34:09.129 [main] kr.leocat.test.circuit.MySub - 서킷 열림 []
ERROR 03:34:10.134 [main] kr.leocat.test.circuit.MySub - 서킷 열림 []
ERROR 03:34:11.137 [main] kr.leocat.test.circuit.MySub - 서킷 열림 []
ERROR 03:34:12.138 [main] kr.leocat.test.circuit.MySub - 서킷 열림 []
INFO  03:34:13.142 [main] kr.leocat.test.circuit.MySub - 호출한다 []
ERROR 03:34:13.143 [main] kr.leocat.test.circuit.MySub - 에러닷 []
INFO  03:34:14.145 [main] kr.leocat.test.circuit.MySub - 호출한다 []
ERROR 03:34:14.146 [main] kr.leocat.test.circuit.MySub - 에러닷 []
ERROR 03:34:15.150 [main] kr.leocat.test.circuit.MySub - 서킷 열림 []
ERROR 03:34:16.155 [main] kr.leocat.test.circuit.MySub - 서킷 열림 []
ERROR 03:34:17.159 [main] kr.leocat.test.circuit.MySub - 서킷 열림 []
ERROR 03:34:18.162 [main] kr.leocat.test.circuit.MySub - 서킷 열림 []
ERROR 03:34:19.167 [main] kr.leocat.test.circuit.MySub - 서킷 열림 []
ERROR 03:34:20.170 [main] kr.leocat.test.circuit.MySub - 서킷 열림 []
```


# TIME_BASED 테스트

```yaml
resilience4j.circuitbreaker:
  configs:
    default:
      slidingWindowType: TIME_BASED
      slidingWindowSize: 3
      minimumNumberOfCalls: 10 # 요놈 설정 필수!! default는 100
      permittedNumberOfCallsInHalfOpenState: 2
      waitDurationInOpenState: 10s # second
      failureRateThreshold: 50
      registerHealthIndicator: false
    another-my-circuit:
      baseConfig: default # 설정을 override 할 수 있다.
      slidingWindowSize: 100
```


## 결론

서킷이 완전히 열리게 되면 `CallNotPermittedException` 예외가 던져진다.
서킷이 완전히 열렸는지 반반 열렸는지 체크하려면 fallback을 둘로 나눠 처리하면 확인이 가능하다.
(개인적으로) 서킷이 완전히 열리면 모든 요청의 로그가 찍히기 때문에, `CallNotPermittedException`에는 stacktrace를 찍지 않는다. 이미 반만 열렸을 때(half open)의 stacktrace로 파악이 가능하다.
