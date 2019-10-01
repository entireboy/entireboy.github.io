---
layout: post
title:  "[Spock] (Set 같은) Collection 전달인자 모킹(mocking)하기"
date:   2019-10-09 22:18:00 +0900
published: false
categories: [ java ]
tags: [ spock, test, mock, collection, set, parameter ]
---

**문제:** 다음 테스트는 통과할까??

```groovy
def "으음 무슨 테스트지"() {
    given:
    Order order = new Order(id: 123L, orderItems: [
            new OrderItem(id: 1L, productId: 11L),
            new OrderItem(id: 2L, productId: 22L),
    ])

    orderRepository.findById(order.id) >> order
    productService.findProductNames(order.orderItems*.productId as Set) >> ["이거", "저거"]

    when:
    List<String> actual = orderService.fetchOrderItemNames(order.id)

    then:
    actual == ["이거", "저거"]
}
```

테스트하는 클래스는 아래처럼 생겼다. 다들 알만한 필드나 구현은 생략하고 테스트할 코드만 남겼다.

```java
class OrderService {

    /* fields */

    public List<String> fetchOrderItemNames(Long orderId) {
        Order order = orderRepository.findById(orderId);
        Set<Long> productIds = order.getOrderItems().stream()
                .map({it.productId})
                .map(OrderItem::getProductId)
                .collect(Collectors.toSet());
        return productService.findProductNames(productIds);
    }
}

class ProductService {
    public List<Long> findProductNames(Collection<Long> productIds) {
        /* fetch and return names */
    }
}
```

테스트가 통과하는지 물어보는거 보니 테스트는 실패할거라는걸 짐작할 수 있다. `Set<Long> productIds = order.getOrderItems().stream()` 부분의 `Set`을 `List`로 바꾸기만 해도 테스트는 통과한다. 그런데 왜 `Set`만 안 되는걸까??

원인은 아래처럼 모킹을 하면 `productService.findProductNames`이 모킹되지 않는다는 것이다. 이런저런 형태로 캐스팅을 해주고 이리저리 바꿔줘도 계속 모킹이 되지 않고 null만 반환한다.

```groovy
// 이렇게 ID를 직접 주고 Set으로 캐스팅을 해도 모킹이 원하는 대로 되지 않고, null이 반환된다. 피곤쓰
productService.findProductNames(order.orderItems*.productId as Set) >> ["이거", "저거"]
productService.findProductNames([11L, 22L] as Set) >> ["이거", "저거"]
productService.findProductNames([11L, 22L] as Collection) >> ["이거", "저거"]
productService.findProductNames((Set) [11L, 22L]) >> ["이거", "저거"]
```

좀 설명하기 묘한데, groovy는 `as` 키워드로 캐스팅을 하게 된다. 그런데 위처럼 캐스팅 해서 모킹을 하는 경우, 실제로 모킹되는 값은 원래 형태인 `[11L, 22L]`과 같은 `List`인 것이다. 때문에 아래처럼 실제 그 타입 객체를 만들어 내서 테스트를 하면 통과하는걸 볼 수 있다. (헣허 )

```groovy
Collection<Long> productIds = order.orderItems*.productId as Set
productService.findProductNames(productIds) >> ["이거", "저거"]

// 또는

productService.findProductNames(order.orderItems*.productId.toSet()) >> ["이거", "저거"]

// 또는

productService.findProductNames([11L, 22L].toSet()) >> ["이거", "저거"]
```

`Set`으로 캐스팅하고 변수로 받아서 넣어주거나, `.toSet()`을 불러서 `Set`으로 바꿔서 모킹을 하면 된다. 아래 테스트는 잘 동작한다.

```groovy
def "으음 무슨 테스트지"() {
    given:
    Order order = new Order(id: 123L, orderItems: [
            new OrderItem(id: 1L, productId: 11L),
            new OrderItem(id: 2L, productId: 22L),
    ])

    orderRepository.findById(order.id) >> order
    productService.findProductNames(order.orderItems*.productId.toSet()) >> ["이거", "저거"]

    when:
    List<String> actual = orderService.fetchOrderItemNames(order.id)

    then:
    actual == ["이거", "저거"]
}
```
