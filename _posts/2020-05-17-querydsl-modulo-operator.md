---
layout: post
title:  "[Querydsl] 나머지 구하기 (mod 연산, modulo)"
date:   2020-05-17 21:18:00 +0900
published: true
categories: [ querydsl ]
tags: [ querydsl, query, mod, modulo, operator, operation ]
---

Querydsl 에서 나머지(mod 연산, modulo)를 구하는 연산이 필요할 때는 NumberExpression#mod를 사용하면 된다.

```java
public List<Order> findOddOrderIds() {
    return from(order)
    .where(order.id.castToNum(Long.class).mod(2L).eq(1L)); // ID가 홀수인 것만 다 꺼내옴
}
```

`order.id`는 `NumberPath<Long>` 타입이기 때문에 `castToNum()`를 이용해서 `NumberExpression` 타입으로 변환해 준다.


로그로 실행되는 쿼리를 확인하면 아래처럼 `mod()`함수로 바뀐 것을 볼 수 있다. (이 샘플은 MySQL)

```bash
    ...
from
  order order0_
where
  mod(order0_.id, ?)=?
```


# 참고

- [NumberExpression - Querydsl API](http://www.querydsl.com/static/querydsl/4.0.3/apidocs/com/querydsl/core/types/dsl/NumberExpression.html)
