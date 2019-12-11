---
layout: post
title:  "[MySQL] DATETIME/TIME/TIMESTAMP 초단위 오버플로우 실수를 조심하자"
date:   2019-12-11 22:18:00 +0900
published: true
categories: [ mysql ]
tags: [ mysql, datetime, type, field, overflow, precision, fractional seconds, fractional, second ]
---

> **TL;DR**: 시간을 표시하는 `DATETIME`, `TIME`, `TIMESTAMP`과 같은 타입은 지정된 정확도(precision)를 넘어가면 반올림 되고, 00시를 넘어서 날짜 자체가 다음 날로 넘어갈 수 있으니 정확도를 잘 체크하자.

MySQL의 `DATETIME`, `TIME`, `TIMESTAMP` 타입은 초단위를 최대 6자리 [microseconds(ms) 정확도 까지 저장](https://dev.mysql.com/doc/refman/8.0/en/fractional-seconds.html)할 수 있다. `DATETIME(6)`, `TIME(2)`과 같이 필요한 자리수를 지정하면 된다.

```sql
mysql> CREATE TABLE mydate (
  id         INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  created_at DATETIME(6)               DEFAULT NULL,
  PRIMARY KEY (id)
);
```

그런데 이렇게 생긴 데이터를 조회하는데 경계가 이상한 것을 발견했다. 아래 같이 조회해서 날짜가 `2019-12-11` 인 데이터만 조회하려 했는데, `2019-12-12` 데이터도 함께 나온다.

```sql
SELECT COUNT(1)
FROM mydate
WHERE created_at BETWEEN '2019-12-11T00:00:00.000000' AND '2019-12-11T23:59:59.9999999';
```

끄응.. 왜지?? [expr BETWEEN min AND max](https://dev.mysql.com/doc/refman/8.0/en/comparison-operators.html#operator_between) 는 `min`과 `max` 모두 inclusive라 딱 `2019-12-11` 까지의 데이터만 나와야 하는데 이상하다.

그래도 눈을 부릅뜨고 자세히 살펴보면 오타가 하나 있다. `999999`가 아니라 `9999999`이다. (말해줘도 잘 안 보여;;) 보이지도 않으니 실수하기 딱 좋다. 그러면 줄 맞춰서 데이터를 넣으면서 확인해 보자.

```sql
mysql> INSERT INTO mydate
VALUES (1, '2019-12-11T00:00:00.000000'),
       (2, '2019-12-11T23:59:59.99999'),
       (3, '2019-12-11T23:59:59.999999'),
       (4, '2019-12-11T23:59:59.9999991'),
       (5, '2019-12-11T23:59:59.9999994'),
       (6, '2019-12-11T23:59:59.9999995'),
       (7, '2019-12-11T23:59:59.9999999'),
       (8, '2019-12-12');

mysql> SELECT * FROM mydate;
+----+----------------------------+
| id | created_at                 |
+----+----------------------------+
|  1 | 2019-12-11 00:00:00.000000 |
|  2 | 2019-12-11 23:59:59.999990 |
|  3 | 2019-12-11 23:59:59.999999 |
|  4 | 2019-12-11 23:59:59.999999 |
|  5 | 2019-12-11 23:59:59.999999 |
|  6 | 2019-12-12 00:00:00.000000 |
|  7 | 2019-12-12 00:00:00.000000 |
|  8 | 2019-12-12 00:00:00.000000 |
+----+----------------------------+
```

지정한 자리수(`DATETIME(6)`)를 넘치면 반올림하게 된다. 그래서 `id`가 6, 7인 데이터는 `2019-12-12 00:00:00.000000`로 저장된 것이다. `SELECT`할 때도 동일하게 넘치는 부분은 반올림을 한다.

```sql
-- 결과: 5
SELECT COUNT(1)
FROM mydate
WHERE created_at BETWEEN '2019-12-11T00:00:00.000000'
                     AND '2019-12-11T23:59:59.999999';

-- 결과: 7
SELECT COUNT(1)
FROM mydate
WHERE created_at BETWEEN '2019-12-11T00:00:00.000000'
                     AND '2019-12-11T23:59:59.9999999';
```


# Range의 inclusive와 exclusive

개인적으로는 range는 시작은 inclusive(포함)로 사용하고 끝은 exclusive(미포함)로 사용하는걸 선호한다. 그렇게 되면 여러개의 range를 합쳐도 경계의 값이 끊어지지 않고 이어질 수 있기 때문이다.

예를 들어, MySQL이 `DATETIME`이 지원하는 저장 자리수를 6자리(ms)에서 9자리(ns)로 늘어나게 된다면, 아래의 두 쿼리 사이에는 경계에 빠지는 녀석들이 생길 수 밖에 없다. 테이블에 `2019-12-11 23:59:59.999999123`과 같은 값이 저장되어 있을 때는 다음 두 쿼리 어디에서도 조회되지 않을 것이다.

```sql
SELECT COUNT(1)
FROM mydate
WHERE created_at BETWEEN '2019-12-11T00:00:00.000000'
                     AND '2019-12-11T23:59:59.999999';
SELECT COUNT(1)
FROM mydate
WHERE created_at BETWEEN '2019-12-12T00:00:00.000000'
                     AND '2019-12-12T23:59:59.999999';
```

개인적으로, 위 쿼리는 `BETWEEN .. AND ..` 대신 아래처럼 바꾸고 싶다. `AND` 조건이 하나 더 들어가서 사용하기 귀찮더라도.. `DATETIME` 컬럼 조회에 `DATE`만 주면 `DATETIME`으로 [자동으로 컨버팅](https://dev.mysql.com/doc/refman/8.0/en/date-and-time-type-conversion.html)도 해주니, 시간(`TIME`)을 명시해 주지 않아도 좋아서 실수도 줄어들고 range도 이어지니 이런저런 문제가 많이 없어지지 않을까??

```sql
SELECT COUNT(1)
FROM mydate
WHERE '2019-12-11' <= created_at
  AND created_at < '2019-12-12';
SELECT COUNT(1)
FROM mydate
WHERE '2019-12-12' <= created_at
  AND created_at < '2019-12-13';
```

간혹 `java.util.stream.IntStream#range(int startInclusive, int endExclusive)`와 같이 범위를 나타내는 녀석들은 start는 inclusive이고 end는 exclusive인걸 종종 볼 수 있다. (IntStream 외에 지금 당장 떠오르는게 없네;;) `IntStream#rangeClosed`라고 end가 inclusive인 메소드가 따로 있다.


# 참고

- [Fractional Seconds in Time Values - MySQL doc](https://dev.mysql.com/doc/refman/8.0/en/fractional-seconds.html)
- [Conversion Between Date and Time Types - MySQL doc](https://dev.mysql.com/doc/refman/8.0/en/date-and-time-type-conversion.html)
- [Comparison Functions and Operators - MySQL doc](https://dev.mysql.com/doc/refman/8.0/en/comparison-operators.html#operator_between)
