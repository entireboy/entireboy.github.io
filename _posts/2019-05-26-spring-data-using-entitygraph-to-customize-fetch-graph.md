---
layout: post
title:  "[spring-data] @EntityGraph로 lazy 패치 같이 불러오기 (fetch-graph 커스터마이징)"
date:   2019-05-26 22:18:00 +0900
published: true
categories: [ spring-data ]
tags: [ spring-data, spring, repository, entity, fetch, fetch graph, EntityGraph, EntityGraphType, annotation, lazy, eager ]
---

[spring-data-jpa](https://spring.io/projects/spring-data-jpa)로 JPA를 쓰다가 보면 `LAZY` 패치타입으로 relation이 달려 있는 entity를 `n+1` 문제 없이 한번에 가져오고 싶을 때가 있다. 평소에는 `LAZY`로 쓰지만, 특정 시나리오에서는 한번에 패치하는게 필요하기도 하다.

이럴 때는 repository 메소드에 `@EntityGraph`만 달아주면 손쉽게 join 해서 한번에 패치해 올 수 있다.

```java
// getter/setter and annotations ..
@Entity
@Table(name = "user")
public class User {

    // other properties

    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private List<Address> addresses = new ArrayList<>();
}

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long userId);

    @EntityGraph(attributePaths = {"addresses"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<User> findWithAddressesById(Long userId);
}
```

`addresses`가 `LAZY`이기 때문에 `findById`로 패치한 `User` 엔티티에서 `addresses`에 접근하면 그 때 달려 있는 `addresses` 개수 만큼 select 쿼리를 날린다. 하지만 `findWithAddressesById`는 `@EntityGraph` 어노테이션으로 `addresses`도 함께 패치해 오도록 해두었기 때문에 1번의 fetch join 쿼리만 실행된다.

```java

select
    user0_.id as id1_10_0_,
    user0_.username as username2_10_0_
from
    user user0_
where
    user0_.id=?


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
    user0_.id=?


```

가끔 필요한 경우를 위해 fetch type을 바꿀 필요도 없고, `Querydsl`이나 `JPQL` 같이 쿼리를 별도로 만들지 않아도 되기 때문에 편하다.

`@EntityGraph`의 `type`은 `EntityGraph.EntityGraphType.FETCH`와 `EntityGraph.EntityGraphType.LOAD` 2가지가 있다.

- `FETCH`: entity graph에 명시한 attribute는 `EAGER`로 패치하고, 나머지 attribute는 `LAZY`로 패치
- `LOAD`: entity graph에 명시한 attribute는 `EAGER`로 패치하고, 나머지 attribute는 entity에 명시한 fetch type이나 디폴트 FetchType으로 패치 (e.g. `@OneToMany`는 `LAZY`, `@ManyToOne`은 `EAGER` 등이 디폴트다)


# 참고

- [EntityGraph - spring-data/jpa docs](https://docs.spring.io/spring-data/jpa/docs/current/api/org/springframework/data/jpa/repository/EntityGraph.html)
- [EntityGraph.EntityGraphType - spring-data/jpa docs](https://docs.spring.io/spring-data/jpa/docs/current/api/org/springframework/data/jpa/repository/EntityGraph.EntityGraphType.html)
