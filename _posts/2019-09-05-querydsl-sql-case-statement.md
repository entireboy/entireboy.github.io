---
layout: post
title:  "[Querydsl] Querydsl에서 CASE statement"
date:   2019-09-05 22:18:00 +0900
published: false
categories: [ querydsl ]
tags: [ querydsl, case, statement, sql, CaseBuilder ]
---

Querydsl로 `CASE` statement(`case-when-then-else`)를 만들 때는 `CaseBuilder`를 사용하면 된다.

```java
from(shape)
.select(
    shape.name,
    shape.height,
    new CaseBuilder()
        .when(shape.color.gt(Color.RED))
        .then(shape.width)
        .otherwise(100))
.fetch();
```

아래처럼 `when-then` 케이스를 계속 붙일 수도 있다.

```java
from(person)
.select(
    person.name,
    new CaseBuilder()
    .when(person.age.lt(10)).then("어린이")
    .when(person.age.lt(20)).then("학생")
    .when(person.age.lt(30)).then("젊은..이")
    .otherwise("어른"))
.fetch();
```


# 참고

- [CaseBuilder - Javadoc](http://www.querydsl.com/static/querydsl/4.1.4/apidocs/com/querydsl/core/types/dsl/CaseBuilder.html)
