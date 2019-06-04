---
layout: post
title:  "[Querydsl] fetch join으로 n+1 문제 피하기"
date:   2019-06-01 22:18:00 +0900
published: true
categories: [ querydsl ]
tags: [ querydsl, fetch, fetch join, join, query, select, lazy ]
---

Querydsl로 select를 할 때 `innerJoin()`이나 `leftJoin()`, `rightJoin()` 등을 사용하면 `from()`에 있는 entity만 꺼내진다. join된 테이블이 select의 조건으로만 쓰고 실제 데이터는 필요가 없다면, 이런 join들을 사용하면 된다.

하지만 함께 join된 테이블의 값을 같이 꺼내고 싶은 경우, 다시 select를 해야 하는 `n+1` 문제가 발생할 수 있다. 이럴 때 `fetchJoin`을 사용하면 한번에 꺼내온다.


# innerJoin

```java
  public List<User> findMe(String street) {
      return from(QUser.user)
          .leftJoin(QUser.user.addresses, QAddress.address)
          .where(QAddress.address.street.eq(street))
          .fetch();
  }
```

아래처럼 `address`가 where절에 조건이 들어가지만 select 대상에는 없는 것을 볼 수 있다.

```java
select
    user0_.id as id1_10_,
    user0_.username as username2_10_
from
    user user0_
left outer join
    address addresses1_
        on user0_.id=addresses1_.user_id
where
    addresses1_.street=?
```


# fetchJoin

```java
  public List<User> findMe(String street) {
      return from(QUser.user)
          .leftJoin(QUser.user.addresses, QAddress.address)
          .fetchJoin()
          .where(QAddress.address.street.eq(street))
          .fetch();
  }
```

`fetchJoin`을 사용하면 `address`가 select 대상에도 포함된다.

```java
select
    user0_.id as id1_10_0_,
    addresses1_.id as id1_5_1_,
    user0_.username as username2_10_0_,
    addresses1_.street as street2_5_1_,
    addresses1_.user_id as user_id3_5_0__,
    addresses1_.id as id1_5_0__
from
    user user0_
left outer join
    address addresses1_
        on user0_.id=addresses1_.user_id
where
    addresses1_.street=?
```
