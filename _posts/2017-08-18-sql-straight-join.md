---
layout: post
title:  "[SQL] STRAIGHT_JOIN on MySQL"
date:   2017-08-18 21:18:00 +0900
published: true
categories: [ sql ]
tags: [ sql, db, database, query, optimizer, join, optimize ]
---

MySQL에 `STRAIGHT_JOIN`이라는 희안한 [join](https://dev.mysql.com/doc/refman/5.7/en/join.html)이 있다.

> `STRAIGHT_JOIN` is similar to `JOIN`, except that the left table is always read before the right table. This can be used for those (few) cases for which the join optimizer processes the tables in a suboptimal order.

왼쪽 테이블을 먼저 읽는 `join`이라고 보면 될 것 같다. (ANSI SQL은 아니니 꼭 필요한 경우가 아니면 피하고 싶다.)

이런 경우에 효과가 있었다. 왼쪽 드라이빙 테이블에 비해 오른쪽 테이블의 데이터가 너무 많아 필터링 해야 하는 데이터가 많은데 정렬까지 해야 하는 경우가 있었다. 아래 테이블처럼 오른쪽 테이블(`BB`)이 상당히 큰데, 사실 왼쪽 테이블(`AA`)와 `join`할 때는 크게 필요 없는 데이터가 많다. 그리고 AA 테이블을 기준으로 정렬이 필요했다.

```sql
sql> SELECT * FROM
  ->   (SELECT count(1) AA FROM AA) AA,
  ->   (SELECT count(1) BB FROM BB) BB,
  ->   (SELECT count(1) 'AA and BB' FROM AA FROM BB ON AA.id = BB.aid) CC;
+---------+----------+-----------+
|      AA |       BB | AA and BB |
+---------+----------+-----------+
|  550000 |  3800000 |    550000 |
+---------+----------+-----------+
```

`join`일 때는 25초 이상 걸리는 쿼리가 `straight_join`은 0.01초만에 끝;;

```sql
sql> SELECT *
  -> FROM AA
  -> JOIN BB ON AA.id = BB.aid
  -> ORDER BY AA.id
  -> LIMIT 10; -- 25초 이상 걸림

sql> SELECT *
  -> FROM AA
  -> STRAIGHT_JOIN BB ON AA.id = BB.aid
  -> ORDER BY AA.id
  -> LIMIT 10; -- 0.01초면 끝
```

`order`과 `limit` 때문에 거대한 왼쪽 테이블(`AA`)이 10줄로 줄어든다. 나머지를 `join`하느라 고생할 필요가 없어지니 상당히 빠른 속도를 낼 것이다. 물론, 여기에 여러 다른 테이블도 함께 많이 붙어있는 상황이다.

아래처럼 `STRAIGHT_JOIN`을 적어주면 모든 `join`이 `STRAIGHT_JOIN`으로 동작한다.

```sql
sql> SELECT STRAIGHT_JOIN *
  -> FROM AA
  -> JOIN BB ON AA.id = BB.aid
  -> ORDER BY AA.id
  -> LIMIT 10;
```


# 참고

- [](https://dev.mysql.com/doc/refman/5.7/en/join.html)
- [MySQL STRAIGHT_JOIN - w3resource](http://www.w3resource.com/mysql/advance-query-in-mysql/mysql-straight-join.php)
