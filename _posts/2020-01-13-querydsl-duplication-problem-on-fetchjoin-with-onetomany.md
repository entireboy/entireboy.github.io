---
layout: post
title:  "[Querydsl] OneToMany 관계에서 fetchjoin 시 데이터 중복 문제"
date:   2020-01-13 22:18:00 +0900
published: true
categories: [ querydsl ]
tags: [ querydsl, duplicated, query, result, fetchjoin, onetomany, duplication, hibernate, initialize, distinct ]
---

`OneToMany` 관계의 entity를 Querydsl로 조회할 때 `fetchjoin`을 사용하면 데이터가 중복되어 조회될 수 있다.

> **TL;DR**: `OneToMany` 관계의 entity를 Querydsl로 조회할 때 `fetchjoin`을 사용하면 중복된 데이터가 조회될 수 있으니 조심해야 한다. 해결 방법은 `fetchjoin`을 사용하지 말고 `Hibernate.initialize`를 호출해서 child entity를 초기화를 해 주거나, `distinct()`를 사용해서 중복을 제거한다.


# 사건의 재구성

간단한 entity `Order`와 `OrderItem`을 만들어서 테스트 해보자. `Order`는 `OrderItem`을 여러개 가질 수 있는 구조이다.

```java
@ToString
@Entity
public class Order {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "user_name")
  private String userName;

  @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
  private List<OrderItem> orderItems = new ArrayList<>();

  public Order addOrderItem(OrderItem orderItem) {
    orderItem.order = this;
    orderItems.add(orderItem);
    return this;
  }
}

@ToString
@Entity
public class OrderItem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "item_name")
  private String itemName;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "order_id", nullable = false)
  @ToString.Exclude
  private Order order;
}
```

아래와 같이 Querydsl 코드를 만들면 `Order`가 2개 꺼내지길 기대할 것이다.

```java
public class OrderRepositoryImpl extends QuerydslRepositorySupport
                                 implements OrderRepositoryCustom {

  @Override
  public List<Order> findAllByUserName(String userName) {
    return from(order)
      .join(order.orderItems, orderItem).fetchJoin()
      .where(order.userName.eq(userName))
      .fetch();
  }
}
```

테스트 코드를 만들어서 확인해보자. (테스트는 spock 코드이다.)

```groovy
  def "test"() {
    given:
    Order order1 = new Order(id: 1L, userName: "펭수")
    order1.addOrderItem(new OrderItem(id: 11L, itemName: "펭하"))
          .addOrderItem(new OrderItem(id: 12L, itemName: "펭바"))
    Order order2 = new Order(id: 2L, userName: "펭수")
          .addOrderItem(new OrderItem(id: 21L, itemName: "이유는 없어 그냥해"))

    orderRepository.saveAll([order1, order2])

    when:
    List<Order> actual = orderRepository.findAllByUserName("펭수")

    then:
    actual.size() == 2
  }
```

결과는 2개(order1, order2)를 기대했지만 그 결과는 두둥!! 3개!! spock 테스트 결과를 보면 id가 1인 `Order`가 2개나 나왔다.

```
Condition not satisfied:

actual.size() == 2
|      |      |
|      3      false
[Order(id=1, userName="펭수"),Order(id=1, userName="펭수"),Order(id=2, userName="펭수")]
```

query도 확인해 보면 내가 원하는 형태인데 말이다.

```sql
select
    order0_.id,
    order0_.userName,
from
    order order0_
inner join
    order_item order_items1_
        on order0_.id=order_items1_.order_id
where
    order0_.userName=?
```

join을 하게 되면서 `OrderItem`이 여러개가 되고, 그 값을 그대로 entity로 mapping을 하면서 발생하는 문제 같다.

```
    order   --   order_item
+----+------++----+----------------+
| id | name || id | item_name      |
+----+------++----+----------------+
|  1 | 펭수  || 11 | 펭하            |
|  1 | 펭수  || 12 | 펭바            |
|  2 | 펭수  || 21 | 이유는 없어 그냥해 |
+----+------++----+----------------+
```


# 해결 방법 #1 - Hibernate initialize

hibernate를 사용한다면 `fetchjoin`을 사용하지 말고, `Hibernate.initialize()`를 호출해서 초기화를 해줘도 된다.

```java
public List<Order> findAllByUserName(String userName) {
  List<Order> result = from(order)
    .where(order.userName.eq(userName))
    .fetch();

  result.stream.map(Order::getOrderItems).forEach(Hibernate::initialize);

  return result;
}
```


# 해결 방법 #2 - distinct

`OneToMany`로 연결된 테이블의 데이터가 문제이니, `fetchjoin`을 사용하면 `distinct()`로 중복을 제거하는 방법이 있다.

```java
public List<Order> findAllByUserName(String userName) {
  return from(order)
    .join(order.orderItems, orderItem).fetchJoin()
    .where(order.userName.eq(userName))
    .distinct()
    .fetch();
}
```
