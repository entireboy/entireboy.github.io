---
layout: post
title:  "[Querydsl] 존재여부 확인하기 (exists)"
date:   2021-01-09 22:18:00 +0900
published: true
categories: [ querydsl ]
tags: [ querydsl, exists, spring data, jpa, method query, improve ]
---

Querydsl을 사용하면서 데이터가 있는지 체크(exists)하기가 어려웠다.


# Before Fix

팀에서 처음에 사용하던 쿼리는 아래 코드처럼 개수를 확인했는데,

```java
public boolean existsById(Long orderId) {
    return from(order)
        .where(order.id.eq(orderId))
        .select(order.id)
        .fetchCount() > 0;
}
```

실제로 실행되는 쿼리는 아래처럼 생겼다. `count` 때문에 전체 데이터를 조회해야 하니 문제가 많았다. 데이터가 많으면 많을수록 성능은..

```bash
select
    count(order0_.id) as col_0_0_
from
    order order0_
where
    and order0_.id=?
```


# After Fix

아래처럼 `fetchFirst()`를 사용하도록 수정했다.

```java
public boolean existsById(Long orderId) {
    return from(order)
        .where(order.id.eq(orderId))
        .select(order.id)
        .fetchFirst() != null;
}
```

그러면 실제로 실행되는 쿼리는 아래처럼 `limit`을 사용하도록 바뀐다.

```bash
select
    order0_.id as col_0_0_
from
    order order0_
where
    and order0_.id=? limit ?
```


# Notes

아래처럼 Spring Data JPA의 메소드 쿼리 `exists`를 사용하면,

```java
boolean existsById(Long orderId);
```

`count` 쿼리를 사용하게 된다.

```bash
select
    count(*) as col_0_0_
from
    order order0_
where
    and order0_.id=?
```
