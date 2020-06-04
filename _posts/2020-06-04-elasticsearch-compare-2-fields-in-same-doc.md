---
layout: post
title:  "[Elasticsearch] 같은 문서의 여러 필드값 비교하기"
date:   2020-06-04 21:18:00 +0900
published: true
categories: [ elasticsearch ]
tags: [ elasticsearch, compare, multi, value, field, document ]
---

Kibana 모니터링 알람을 걸기 위해 Elasticsearch 에서 수집한 동일한 문서의 다른 필드의 값을 비교해야 하는 경우가 생겼다.

문서에서 값을 비교할 부분은 아래처럼 생겼고, `hikaricp_connections_active`는 `hikaricp_connections_max`의 70%를 넘으면 알람을 보내고 싶다. 때문에 두 필드를 한번에 비교해야 했다.

```
{
  "prometheus": {
    "metrics": {
      "hikaricp_connections_timeout_total": 0,
      "hikaricp_connections_idle": 50,
      "hikaricp_connections_max": 50,
      "hikaricp_connections_creation_seconds_count": 230377,
      "hikaricp_connections_active": 0,
      "hikaricp_connections_usage_seconds_sum": 727.225,
      "hikaricp_connections": 50,
      "hikaricp_connections_usage_seconds_max": 0.018,
      "hikaricp_connections_acquire_seconds_max": 0.01207656,
      "hikaricp_connections_creation_seconds_sum": 4754.902,
      "hikaricp_connections_pending": 0,
      "hikaricp_connections_min": 50,
      "hikaricp_connections_acquire_seconds_sum": 328.019810385,
      "hikaricp_connections_creation_seconds_max": 0.057,
      "hikaricp_connections_acquire_seconds_count": 234561,
      "hikaricp_connections_usage_seconds_count": 234561
    },
    "labels": {
      "pool": "my-service-cp"
    }
  }, ...
}
```

같은 문서의 두 필드를 비교하는건 `script`로 만들 수 있다. 동일한 문서이기 때문에 `doc['FIELD_NAME'].value` 형식으로 값에 접근할 수 있다.

```
{
  "bool": {
    "must": [
      {
        "script": {
          "script": {
            "source": "doc['prometheus.metrics.hikaricp_connections_max'].value * 0.7 < (doc['prometheus.metrics.hikaricp_connections_active'].value)",
            "lang": "painless"
          }
        }
      }
    ],
    "adjust_pure_negative": true,
    "boost": 1
  }
}
```


# 참고

- [https://discuss.elastic.co/t/compare-fields/114514](Compare fields - elastic discuss)
