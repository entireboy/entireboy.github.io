---
layout: post
title:  "[Querydsl] Querydsl 조회 결과를 별도 DTO 클래스로 받기 - projection"
date:   2019-11-14 22:18:00 +0900
published: true
categories: [ querydsl ]
tags: [ querydsl, query, result, handle, handling, projection ]
---

Querydsl로 조회를 할 때 entity 전체가 아닌 일부 column만 꺼내오면, 아래처럼 `Tuple`을 사용해야 하고 하나하나 필드를 알고 있어야 하는 불편함이 있다.

```java
List<Tuple> userContacts = query.from(user)
.select(user.name, user.mobile, user.address)
.fetch();

for (Tuple contact : userContacts) {
     System.out.println("user: " + contact.get(user.name));
     System.out.println("mobile: " + contact.get(user.mobile));
}}
```

이럴 때 조금 더 명시적으로 DTO 클래스를 사용하고 싶다면 `com.querydsl.core.types.Projections`를 쓰면 된다.

```java
List<UserContactDto> userContacts = query.from(user)
.select(Projections.constructor(UserContactDto.class,
  user.name,
  user.mobile,
  user.address))
.fetch();
```

```java
public class UserContactDto {
  private String name;
  private String mobile;
  private String address;

  public UserContactDto(String name, String mobile, String address) {
    ...
  }

  // getter
}
```

`Projections.constructor()`로 projection할 클래스와 column을 지정해 주면 된다. 메소드명에서 보이듯이 생성자를 통해서 DTO 객체를 생성한다. 위처럼 DTO에는 생성자가 필수이다. 생성자로 만드는 방법 이외에도 `Projections.bean()`, `Projections.fields()` 처럼 entity bean을 생성해서 채우거나 필드로 채워주는 메소드도 지원한다.


# 참고

- [3.2. Result handling - Querydsl Reference Guide](http://www.querydsl.com/static/querydsl/4.2.1/reference/html/ch03s02.html)
