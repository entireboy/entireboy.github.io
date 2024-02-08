---
layout: post
title:  "[MongoDB] WiredTiger에서 read/write tickets"
date:   2023-01-04 22:18:00 +0900
published: true
categories: [ mongodb ]
tags: [ mongodb, WiredTiger, ticket, concurrency, operation, monitoring, indicator ]
---

# 문제 발생

MongoDB 사용 중 갑자기 응답이 오지 않고, 멍 때리기 시작하는 현상이 발생했다.

Cache eviction, Lock wait, 비정상적인 Connection 수 증가 등 모니터링 지표 중에 몇 가지 의심가는 것들이 발견되었고, 그 중 가장 눈에 띄는 것은 Ticket available write 였다. 이 값은 평소에 128 언저리였지만, 0으로 뚝 떨어진 것.


# Ticket Available

Ticket available은 동시에 실행할 수 있는 operation의 수라고 보면 된다. write와 read 각각 128로 고정되어 있으며, 동시에 128개의 read/write oepration을 실행하고 있으면 하나가 끝나야 다음 operation을 시작할 수 있다는 의미이다.

이 128개는 일반 장비에서 충분한 개수이고, 이 수치를 변경할 수는 있지만 변경한다고 성능이 더 좋아지는 것은 아니다. 동시에 실행할 수 있는 operation 개수가 늘어나서 지연시킬 수는 있지만 그만큼 CPU나 IO를 많이 잡아먹기 때문에 근본적인 해결이 필요하다.


# 시도해 볼 것

## Query 개선

우리의 문제는 write ticket이 0개로 모두 소진되었고, 응답을 주지 못 하고 멍 때리고 있었던 것이다. 원인은 과거데이터를 무리하게 조회하는 배치의 쿼리가 상당히 오래 걸렸던 것이었다. 배치라 응답시간 확인이 어려워서 발견이 늦었다.

data traverse하는 쿼리가 더 좋은 성능을 낼 수 있도록 more 방식으로 조회를 하도록 변경하였다.


## Read preference 설정

그리고 모든 커맨드가 primary로 보내지는 것이 발견되었고, 비정상이던 지표들은 모두 primary의 것이었다.

놀고 있는 secondary를 활용하기 위해, 데이터를 조회할 때는 secondary에서 조회할 수 있도록 [Read preference](https://www.mongodb.com/docs/v6.0/core/read-preference/)를 설정하였다.

```kotlin
// kotlin
MongoTemplate(mongoFactory, converter).apply {
    setReadPreference(ReadPreference.secondaryPreferred())
}
```

```java
// java
MongoTemplate mongoTemplate = new MongoTemplate(mongoFactory, converter)
mongoTemplate.setReadPreference(ReadPreference.secondaryPreferred())
```

`secondaryPreferred`로 설정하면 [읽기는 secondary에서 실행](https://www.mongodb.com/docs/v6.0/core/read-preference/#mongodb-readmode-secondaryPreferred)한다. 단, secondary를 찾을 수 없는 경우 primary에서 실행한다.

모니터링 지표를 보면 mongos가 operation은 모두 primary로 보내는 것으로 보이는데, `docuemnts returned`는 secondary에서 오는 것을 확인할 수 있다.

{% include image.html file='/assets/img/2023/2023-01-04-mongodb-read-write-tickets.png' alt='Documents returned' %}


# 참고
- [What are read and write tickets in WiredTiger?](https://muralidba.blogspot.com/2018/04/what-are-read-and-write-tickets-in.html)
- [Read Preference - MongoDB Documentation](https://www.mongodb.com/docs/v6.0/core/read-preference/)