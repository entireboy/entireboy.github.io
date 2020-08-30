---
layout: post
title:  "[Hibernate] Java Map 으로 json 타입 매핑하기"
date:   2020-09-01 21:18:00 +0900
published: true
categories: [ hibernate ]
tags: [ hibernate, json, type, entity, mapping, java, map ]
---

간혹 entity에 json 필드를 넣어야 할 때가 있다. child가 없는 1차원의 데이터라면 Java Map으로 사용하면 편하기 때문에 Map으로 변환하고 싶을 때는 이렇게 하면 된다.

테이블은 아래처럼 `JSON` 타입으로 column 을 생성했다. (MySQL)

```sql
CREATE TABLE orders
(
    id              BIGINT   NOT NULL PRIMARY KEY AUTO_INCREMENT,
    order_groups    JSON     NOT NULL COMMENT '그룹 개수',
    ...
    UNIQUE KEY UNIQUE_xxx (xxx)
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COMMENT = '주문 정보';
```

`hibernate-types-52` 디펜던시를 추가한다. `hibernate-types-52` 는 hibernate 5.2, 5.3, 5.4 버전의 추가 타입을 지원한다.

```java
dependencies {
    compile('com.vladmihalcea:hibernate-types-52:2.9.13')
}
```

Entity에 `@TypeDef` annotation으로 추가 타입 사용을 지정한다. 그리고 필요한 column 에서 그 타입을 사용한다.

```java
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Getter
@Setter
@NoArgsConstructor
@TypeDef(name = "jsonb", typeClass = JsonStringType.class)
@Table(name = "orders", indexes = { ... })
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    ...

    @Type(type = "jsonb")
    @Column(name = "order_groups", columnDefinition = "jsonb", nullable = false)
    private Map<String, Integer> orderGroups = new HashMap<>();
}
```


# 참고

- [https://vladmihalcea.com/java-map-json-jpa-hibernate/](Java Map to JSON mapping with JPA and Hibernate)
