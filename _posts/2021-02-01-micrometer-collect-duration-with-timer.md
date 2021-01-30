---
layout: post
title:  "[Micrometer] 처리/응답시간 메트릭 수집하기"
date:   2021-02-01 22:18:00 +0900
published: true
categories: [ metric collect ]
tags: [ micrometer, metric, collect, duration, timer, counter, gauge, influxdb, spring ]
---

[Micrometer](https://micrometer.io/)를 이용하면 쉽게 InfluxDB와 같은 시계열 디비로 메트릭을 수집할 수 있다. 특히 [Spring Boot의 Metrics를 사용]({{ site.baseurl }}{% post_url 2021-01-29-spring-collect-prometheus-metirc-into-influxdb %})하면 간단한 설정으로 Prometheus로 수집되는 정보들을 수집할 수 있다.

여기서는 Prometheus 등으로 수집되지 않지만 메트릭을 수동으로 수집하는 방법을 간단히 (까먹지 않게) 적어둔다.


# Micrometer

[Micrometer Concepts](https://micrometer.io/docs/concepts) 페이지를 보면 여러 타입의 정보를 수집할 수 있는 것을 알 수 있다.

- Counters: 횟수를 수집
- Gauges: 특정 수치를 수집 (CPU, 메모리, 디스크 사용량이나 트래픽 등 특정 수치로 떨어지는 값을 수집한다.)
- Timers: 처리시간 같은 시간은 수집. 횟수(frequency), 평균(mean) 등도 함께 수집할 수 있다

여기서는 Timers 샘플을 살펴본다. API나 특정 메시지를 처리하는 시간 관련된 메트릭을 수집할 수 있다. 장애 징후를 감지할 때나 배포 후 특정 API의 latency가 안 좋아지는 경우를 파악하는데 유용하게 사용될 수 있다. 보통 API endpoint 마다 평균적으로 처리된 시간 정도는 모니터링하기도 하는데, p95, p99 같은 백분위수를 모니터링할 수 있다면 더 쉽게 잘못된 점을 체크하기 좋다.

예를 들어, 상품을 조회하는 API를 배포했다고 하자. 이번 배포로 인해 품절이 됐거나 하는 등의 특정 조건에 해당하는 상품을 조회할 때 성능이 안 좋아졌다면, 품절이 되지 않은 대부분의 상품은 응답시간이 기존과 크게 다르지 않지만 품절된 상품은 응답시간이 많이 늘어져 있을 것이다. 대부분의 상품이 품절이 아니라면 평균적으로 봤을 때는 이 API의 응답시간은 배포 전후가 큰 차이가 없지만, 응답시간이 좋지 않은 백분위 p95나 p99는 품절된 상품의 응답시간으로 latency가 안 좋아진 것을 한눈에 알 수 있다.


# 샘플

이 내용은 API 응답시간이나 Queue message 처리시간 같은 시간을 수집할 때 유용하다.

```java
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import static java.util.concurrent.TimeUnit.MILLISECONDS;


@Component
public class QueueMessageProcessMetricCollector {
    private static final String SQS_PROCESS = "sqs_process";
    private static final String QUEUE_NAME = "queue_name";

    private final Map<String, Timer> timersByQueueName = new ConcurrentHashMap<>();
    private final MeterRegistry meterRegistry;

    public QueueMessageProcessMetricCollector(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void record(String queueName, long elapsedMillis) {
        getTimer(queueName).record(elapsedMillis, MILLISECONDS);
    }

    public <T> void record(String queueName, Supplier<T> messageHandler) {
        getTimer(queueName).record(messageHandler);
    }

    public <T> void record(String queueName, Callable<T> messageHandler) {
        try {
            getTimer(queueName).recordCallable(messageHandler);
        } catch (Exception e) {
            // 예외처리 또는 default return
        }
    }

    private Timer getTimer(String queueName) {
        return timersByQueueName.computeIfAbsent(queueName, this::createTimer);
    }

    private Timer createTimer(String queueName) {
        return Timer
                .builder(SQS_PROCESS)
                .description("Metrics for processing a SQS message")
                .tag(QUEUE_NAME, queueName)
                .publishPercentiles(0, 0.5, 0.9, 0.95, 0.99) // 수집할 백분위 값
                .publishPercentileHistogram()
                .register(meterRegistry);
    }

}
```

사실 `timersByQueueName` 는 필요가 없다. `MeterRegistry` 내부적으로 map을 가지고 있어서, `meterRegistry.timer(SQS_PROCESS)` 만으로도 Timer 객체를 빠르게 가져올 수 있다.

InfluxDB 등에서 확인해 보면, 수집된 메트릭은 지정한 Timer 이름인 `sqs_process` measurement로 메트릭으로 저장이 된다. 그리고 `publishPercentiles` 로 지정한 백분위 값은 `_percentile` postfix가 붙은 `sqs_process_percentile` measurement로 저장된다.

```bash
> -- InfluxDB에 저장된 measurement
> show measurements;
name: measurements
name
----
sqs_process
sqs_process_percentile
>
```


# 편의 기능

재밌는 점은 `Supplier` 와 `Callable` 을 전달하면 실행된 시간을 알아서 확인하고 MeterRegistry에 저장해 준다는 점이다.

```java
timer.record(1L, MILLISECONDS);
timer.reocrd(() -> helloWithoutReturn());
timer.reocrdCallable(() -> helloWithReturn());
```

더 재밌는건 `@Timed` annotation만 붙여도 메소드의 실행시간을 수집할 수 있다는 것이다. (사실 위에 있는 샘플은 구조상 저런 형태로 사용해야 해서, 만든 김에 기록용으로 적어둔 것이다. 아마 앞으로 대부분은 아래처럼 사용할 것 같다. =_=)

```java
@Configuration
public class TimedConfiguration {
   @Bean
   public TimedAspect timedAspect(MeterRegistry registry) {
      return new TimedAspect(registry);
   }
}

@Component
public class MyClass {
    @Timed
    public void test() {
      ..
    }
}
```


# 참고

- [Micrometer Concepts](https://micrometer.io/docs/concepts)
- [Timers - Micrometer Concepts](https://micrometer.io/docs/concepts#_timers)
- [Histograms and percentiles - Micrometer Concepts](https://micrometer.io/docs/concepts#_histograms_and_percentiles)
