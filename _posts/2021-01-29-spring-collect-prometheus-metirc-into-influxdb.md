---
layout: post
title:  "[Spring Metrics] Prometheus를 통해 수집한 정보를 InfluxDB에 저장하기"
date:   2021-01-29 22:18:00 +0900
published: true
categories: [ spring ]
tags: [ spring, spring metrics, metric, collect, prometheus, influxdb, micrometer ]
---

[Prometheus](https://prometheus.io/)로 이런저런 정보를 수집할 수 있다. [Spring Metrics](https://docs.spring.io/spring-metrics/docs/current/public/prometheus)를 통하면 간단한 설정만으로도 수집이 가능하다. 회사에서는 이렇게 수집된 정보들을 여러 모니터링 지표로 사용하고 있다.

이렇게 수집된 정보를 InfluxDB 로 보내본다.

왜냐?? InfluxDB에 넣으면 Grafana로 보기 편하고 쿼리 속도도 좋다. ELK로 보내서 대시보드로 볼 수 있긴 하지만, Kibana는 너무 느리고 구리다. (정말.. 휴..)


# InfluxDB 설치

로컬에서는 간단하게 docker로 실행할 수도 있다.

```bash
$ docker run -p 8086:8086 \
  -e INFLUXDB_DB=db0 \
  -e INFLUXDB_ADMIN_USER=admin \
  -e INFLUXDB_ADMIN_PASSWORD=admin \
  --name influxdb influxdb
```


# 프로젝트 설정

Spring Metrics 설정을 해준다.

```yaml
management:
  metrics:
    export:
      influx:
        uri: http://localhost:8086
        db: leocat-api-${spring.profiles.active}
        enabled: true  # export to InfluxDB enable
        step: 10s
      prometheus:
        enabled: true  # prometheus enable
```

- uri: 설치한 InfluxDB 경로를 적어준다.
- db: prometheus로 수집한 메트릭을 저장할 DB명을 지정한다.
- step: 수집한 정보를 얼마나 자주 InfluxDB에 저장할지를 설정한다.

참, 그 전에 dependency 추가가 필요하다.

```groovy
compile('io.micrometer:micrometer-registry-prometheus')
compile('io.micrometer:micrometer-registry-influx')
```


# 데이터 확인

InfluxDB도 실행하고 spring application도 실행하면, InfluxDB 에 (위에서 설정한) 10초 마다 데이터가 쌓인다.

Docker 로 InfluxDB를 실행했다면 아래 커맨드로 접속할 수 있다.

```bash
$ docker exec -it influxdb influx
Connected to http://localhost:8086 version 1.8.3
InfluxDB shell version: 1.8.3
> show databases;
name: databases
name
----
db0
_internal
leocat-api-local
```

혹은 [Time Series Admin](https://timeseriesadmin.github.io/) 같은 툴들이 있긴 하지만, 쿼리 자동 완성도 안 되고.. 하아.. InfluxDB는 로컬에서 돌릴만한 이쁜 툴이 없어 T_T Grafana 가 젤 이쁘다..

![Time Series Admin](https://timeseriesadmin.github.io/images-mini/timeseriesadmin.png)

위에서 설정한 `leocat-api-local` DB에 잘 저장된 것을 볼 수 있다. 어떤 데이터들이 있는지 `measurements`를 찾아보면, prometheus가 수집하는 데이터들이 우르르.. hiakariCP 정보도 있고, jvm 모니터링도 있다.

```
> use ad-center-api-local;
> show measurements;
name: measurements
name
----
hikaricp_connections
hikaricp_connections_acquire
hikaricp_connections_active
hikaricp_connections_creation
hikaricp_connections_idle
hikaricp_connections_max
http_server_requests
jvm_classes_unloaded
jvm_gc_live_data_size
jvm_gc_max_data_size
jvm_gc_memory_allocated
jvm_gc_memory_promoted
jvm_gc_pause
jvm_memory_committed
jvm_memory_max
jvm_memory_used
jvm_threads_daemon
system_cpu_count
system_cpu_usage
...
```

그 중에 API 처리시간, 처리한 개수 등이 궁금해 졌다.

`measurements`를 잘 살펴보면 `http_server_requests`라는 항목이 있고, 여기에 API request 관련된 메트릭들이 수집되어 있어요.각 uri 마다 수집되는 것을 볼 수 있다. (이렇게 모아서 보니 actuator 통해서 보는 것 보다 더 잘 보이네..)

```
> select * from http_server_requests;
name: http_server_requests
time                count exception mean      method metric_type outcome      status sum        upper     uri
----                ----- --------- ----      ------ ----------- -------      ------ ---        -----     ---
1611848115985000000 0     None      0         GET    histogram   SUCCESS      200    0          94.306568 /v2/ad-categories
1611848125986000000 3     None      34.697168 GET    histogram   SUCCESS      200    104.091503 94.306568 /v2/ad-categories
1611848135986000000 0     None      0         GET    histogram   SUCCESS      200    0          94.306568 /v2/ad-categories
1611848145987000000 0     None      0         GET    histogram   SUCCESS      200    0          0         /v2/ad-categories
1611848155986000000 0     None      0         GET    histogram   SUCCESS      200    0          0         /v2/ad-categories
1611848165987000000 0     None      0         GET    histogram   SUCCESS      200    0          0         /v2/ad-categories
1611848175986000000 0     None      0         GET    histogram   SUCCESS      200    0          0         /v2/ad-categories
1611848185989000000 0     None      0         GET    histogram   SUCCESS      200    0          0         /v2/ad-categories
1611848195990000000 0     None      0         GET    histogram   SUCCESS      200    0          0         /v2/ad-categories
1611848205989000000 0     None      0         GET    histogram   SUCCESS      200    0          0         /v2/ad-categories
1611848215985000000 0     None      0         GET    histogram   SUCCESS      200    0          0         /v2/ad-categories
1611848675998000000 0     None      0         GET    histogram   CLIENT_ERROR 404    0          10.581296 /**
1611848685997000000 0     None      0         GET    histogram   SUCCESS      200    0          0         /v2/ad-categories
1611848685998000000 2     None      7.78893   GET    histogram   CLIENT_ERROR 404    15.57786   10.581296 /**
1611848849460000000 0     None      0         GET    histogram   SUCCESS      200    0          65.584904 /actuator/prometheus
1611848859455000000 0     None      0         GET    histogram   CLIENT_ERROR 401    0          2.659762  /v2/ad-campaigns/finished/yay
1611848859457000000 1     None      56.981775 GET    histogram   SUCCESS      200    56.981775  56.981775 /v2/ad-categories
1611848859458000000 2     None      35.053714 GET    histogram   SUCCESS      200    70.107429  65.584904 /actuator/prometheus
```

이전 회사에서 있었던 있이지만, 배포 후 API latency가 전반적으로 안 좋아졌다. 로그를 API 마다 나눠서 봤더니 배포 후 특정 API의 latency가 안 좋아진 것을 확인했고 코드를 다시 확인해 잘못된 부분을 확인할 수 있었는데, 이렇게 나눠진 uri 별 latency 는 장애를 감지하거나 하는데 유용하게 사용될 수 있으니 잘 활용하면 좋은 지표이다. 특히 p95, p99 등의 지표가 뽈록하게 올라가는 것을 확인할 수 있으니 평소에 잘 보관해 두자.


# 더 해볼 것

Spring Metrics를 통해서 percentile-histogram 설정이 가능한 것 같은데, 설정이 안 먹히는지 조회가 잘 되지 않는다.. (더 해보고 진전이 있으면 추가 예정)

```yaml
management:
  metrics:
    distribution:
      percentiles:
        p0: 0
        p50: 0.5
        p90: 0.9
        p95: 0.95
        p99: 0.99
      percentiles-histogram:
        http.server.requests: true
```


# 참고

- [Spring Metrics](https://docs.spring.io/spring-metrics/docs/current/public/prometheus)
- [Metrics - Spring Boot Docs](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/production-ready-metrics.html)
- [Histograms and percentiles - Micrometer Concepts](https://micrometer.io/docs/concepts#_histograms_and_percentiles)
- [Micrometer: Spring Boot 2's new application metrics collector](https://spring.io/blog/2018/03/16/micrometer-spring-boot-2-s-new-application-metrics-collector)
