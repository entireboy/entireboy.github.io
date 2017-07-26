---
layout: post
title:  "[Java] 날짜 더하고 빼기 - Java8"
date:   2017-07-25 21:18:00 +0900
published: true
categories: [ java ]
tags: [ java, date, add, sub ]
---

Java8 이전까지는 Java에서 날짜 계산은 Calendar를 사용했다. 윤달 체크 같은 것도 해줘서 좋은데, 사용하기 너무 번거로웠다. 그리고 mutable이기 때문에 전혀 예상치 못 하게 값이 변경될 수 있다.

```java
import java.util.Calendar

Calendar rightNow = Calendar.getInstance(); // 현재 시간
rightNow.add(Calendar.DATE, 3); // 3일 뒤
rightNow.add(Calendar.MONTH, -3); // (위에서 바꾼 3일 뒤의) 3달 전
```

Java8에서 새로 추가된 날짜 관련 클래스들(`java.time` 패키지)은 조금 나아졌다. 그리고 **immutable**이기 때문에, thread-safe 하다는 장점도 있다. [LocalDateTime](https://docs.oracle.com/javase/8/docs/api/java/time/LocalDateTime.html)

```java
import java.time.LocalDateTime;

LocalDateTime now = LocalDateTime.now(); // 현재시간
LocalDateTime threeYearsAfter = now.plusYears(3); // 3년 뒤 - now는 계속 현재시간
LocalDateTime twoDaysAgo = now.minusDays(2); // 2일 전
LocalDateTime twoDaysAndThreeHoursAgo = now.minusDays(2).minusHours(3); // 2일 3시간 전
```

중요한 차이점은 immutable이기 때문에, 한번 만들어지면 plus/minus를 해도 변하지 않아서 now는 끝까지 처음 시간을 유지한다. 그리고 리턴값으로 변경된 LocalDateTime을 값을 넘겨주기 때문에 계속 메소드 체이닝을 할 수 있어서 코드가 조금은 이뻐진다. Calendar의 경우는 add() 메소드가 void이기 때문에 변수를 계속 불러줘야 했다.

[LocalDateTime](https://docs.oracle.com/javase/8/docs/api/java/time/LocalDateTime.html) 클래스는 timezone 정보가 없는 날짜와 시간을 표현할 수 있는 클래스이고, [LocalDate](https://docs.oracle.com/javase/8/docs/api/java/time/LocalDate.html)와 [LocalTime](https://docs.oracle.com/javase/8/docs/api/java/time/LocalTime.html)은 각각 날짜만 표현하거나 시간만 표현하는 클래스이다. timezone 정보를 함께 표현하고 싶다면, [ZonedDateTime](https://docs.oracle.com/javase/8/docs/api/java/time/ZonedDateTime.html)을 사용하면 된다.

{% include google-ad-content %}

Date를 LocalDateTime으로 변경하려면 아래처럼 하면 된다. Date를 Java8에서 추가된 시점(특정 시각) 클래스인 [Instant](https://docs.oracle.com/javase/8/docs/api/java/time/Instant.html)로 변경하고 timezone을 설정하면 변경할 수 있다. [ZoneId](https://docs.oracle.com/javase/8/docs/api/java/time/ZoneId.html) 역시 timezone을 나타내기 위해 새로 추가된 클래스이다.

```java
LocalDateTime now = new Date()
    .toInstant()
    .atZone(ZoneId.systemDefault())
    .toLocalDateTime() // .toLocalDate(), .toLocalTime() 도 있다.
```

# 참고

- [Java – How to add days to current date](https://www.mkyong.com/java/java-how-to-add-days-to-current-date/)
- [LocalDateTime](https://docs.oracle.com/javase/8/docs/api/java/time/LocalDateTime.html)
- [LocalDate](https://docs.oracle.com/javase/8/docs/api/java/time/LocalDate.html)
- [LocalTime](https://docs.oracle.com/javase/8/docs/api/java/time/LocalTime.html)
- [ZonedDateTime](https://docs.oracle.com/javase/8/docs/api/java/time/ZonedDateTime.html)
- [Instant](https://docs.oracle.com/javase/8/docs/api/java/time/Instant.html)
- [ZoneId](https://docs.oracle.com/javase/8/docs/api/java/time/ZoneId.html)
