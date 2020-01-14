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

이 방법은 Hibernate의 `hibernate.default_batch_fetch_size` 설정으로 성능 향상 효과를 볼 수 있다.

```yaml
jpa:
  properties:
    hibernate.default_batch_fetch_size: 30
```

`Order`의 children으로 달린 `OrderItem`을 설정해 준 개수 만큼 `in 절`로 쿼리를 하게 되기 때문에 `N+1`문제를 해결할 수 있다. `hibernate.default_batch_fetch_size`는 기본 batch size를 설정하는데, `@BatchSize` annotation으로 특정 entity에서만 batch size를 조절할 수도 있다.

```java
public class Order {

  // .. (생략) ..

  @BatchSize(size = 10)
  @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
  private List<OrderItem> orderItems = new ArrayList<>();
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

`distinct()`를 사용하면 join 된 테이블의 데이터가 모두 전송된다. 그리고 메모리에서 중복되는 parent(`Order`)의 데이터를 모두 날려 버리기 때문에, 원하는 결과는 나오지만 불필요한 데이터 전송량이 증가하는 문제가 있을 수 있다.


# 해결 방법 #3 - 각 하나씩 찔러보기

이건 안 좋은 방법이라 안 쓰려 했지만, 팀에서 공유하다 옆에 계신 분이 안 좋아도 참고나 하라고 써보자 해서 추가

`fetchjoin`을 사용하지 않고 가져온 parent의 children을 하나하나 돌면서 `getId()` 등을 호출해서 lazy loading을 직접 처리해 준다. 그리고 children을 가져오는 `getOrderItems()` 까지만 호출하면 안 되고, children의 member 까지 호출해야 한다.

이 방법은 `N+1` fetch를 하게 되기 때문에 성능 문제가 클 수 있다.

```java
public List<Order> findAllByUserName(String userName) {
  List<Order> result = from(order)
    .where(order.userName.eq(userName))
    .fetch();

  result.stream.map(Order::getOrderItems).forEach(Hibernate::initialize);
  trans.stream()
    .map(Order::getOrderItems)
    .flatMap(Collection::stream)
    .forEach(orderItem::getId);

  return result;
}
```
