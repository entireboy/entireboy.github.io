---
layout: post
title:  "[Querydsl] fetch join으로 n+1 문제 피하기"
date:   2019-06-01 22:18:00 +0900
published: true
categories: [ querydsl ]
tags: [ querydsl, fetch, fetch join, join, query, select, lazy ]
---

Querydsl로 select를 할 때 `innerJoin()`이나 `leftJoin()`, `rightJoin()` 등을 사용하면 `from()`에 있는 entity만 꺼내진다. join된 테이블이 select의 조건으로만 쓰고 실제 데이터는 필요가 없다면, 이런 join들을 사용하면 된다.

하지만 함께 join된 테이블의 값을 사용해야 하는 경우, 다시 select를 해야 하는 n+1 문제가 발생할 수 있다. 이럴 때 `fetchJoin`을 사용하면 한번에 꺼내온다.


# innerJoin

```java
QUser ..

```

아래처럼 where절에 조건이 들어가지만 select 대상에는 없는 것을 볼 수 있다.

```java
```


# fetchJoin

```java
```

`fetchJoin`을 사용하면 select 대상에도 포함된다.

```java
```
