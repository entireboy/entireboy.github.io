---
layout: post
title:  "[Querydsl] Querydsl에서 CASE statement"
date:   2019-09-05 22:18:00 +0900
published: true
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

`org.hibernate.SQS` 로그를 `DEBUG`로 맞추면, 아래와 같은 형태로 남는 로그를 볼 수 있다. when 절에 있는 조건은 별도로 바인딩 된다. `org.hibernate.type` 로그를 `TRACE` 레벨까지 낮추면 값을 확인할 수 있다.

```
2019-09-07 18:58:38.311 DEBUG 1927 --- [           main] org.hibernate.SQL                        :
select
    shape0_.name as col_0_0_,
    shape0_.height as col_1_0_,
    case
        when shape0_.color=? then shape0_.width
        else 100
    end) as col_2_0_
from
   shape shape0_
2019-09-07 18:58:38.311 TRACE 1927 --- [           main] org.hibernate.type.EnumType              : Binding [RED] to parameter: [1]
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
