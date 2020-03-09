---
layout: post
title:  "[Kotlin] reduce 와 fold"
date:   2020-03-09 22:18:00 +0900
published: true
categories: [ kotlin ]
tags: [ kotlin, collection, list, reduce, fold, accumulate, java, stream ]
---

Kotlin 컬렉션에는 컬랙션 내의 데이터를 모두 모으는(accumulate) 함수인 [reduce()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/reduce.html)와 [fold()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/fold.html)가 있다.

둘의 차이는 accumulation 작업을 할 때 `reduce()`는 초기값이 없이 첫번째 요소(element)로 시작하고, `fold()`는 지정해 준 초기값으로 시작한다.

```kotlin
val numbers = listOf(7, 4, 8, 1, 9)

val sum = numbers.reduce { total, num -> total + num }
println("reduced: $sum") // reduced: 21
val sumFromTen = numbers.fold(10) { total, num -> total + num }
println("folded: $sumFromTen") // folded: 31
```

Java의 stream 에서는 둘 다 `reduce()`이다.

```java
List<Integer> numbers = ImmutableList.of(7, 4, 8, 1, 9);

Optional<Integer> sum = numbers.stream()
    .reduce((total, num) -> total + num); // Integer::sum
System.out.println("reduced: " + sum.get());
Integer sumFromTen = numbers.stream()
    .reduce(10, (total, num) -> total + num);
System.out.println("folded: " + sumFromTen);
```


# 빈 컬렉션 (empty collection)

`reduce()`는 첫번째 element를 시작으로 accumulation 작업을 시작하는데, empty list 처럼 빈 컬렉션에서는 첫번째 element가 없다. 그러면 어떻게 될까?? 때문에, Java에서는 `Optional`을 리턴한다. Kotlin은??

```kotlin
val numbers = emptyList<Int>()

val sumFromTen = numbers.fold(10) { total, num -> total + num }
println("folded: $sumFromTen") // folded: 10
val sum = numbers.reduce { total, num -> total + num }
println("reduced: $sum")
```

`fold()`는 정상적으로 출력이 되고, `reduce()`는 `UnsupportedOperationException`이 발생한다.

```bash
folded: 10

Empty collection can't be reduced.
java.lang.UnsupportedOperationException: Empty collection can't be reduced.
	at kr.leocat.test.FoldTest.test(FoldTest.kt:35)
  ...
```

**컬렉션이 비어있을 가능성이 있다면 `fold()`를 사용하자.**


# 첫번째 요소 (first element)

그리고 accumulation에서 첫번째 element가 사용되기 때문에 아래처럼 코드를 사용하면 **안 된다.** 이는 Java stream의 `reduce()`도 마찬가지이다.

```kotlin
val numbers = listOf(5, 2, 10, 4)

val doubledSum = numbers.reduce { total, num -> total + num * 2 }
println("reduced: $doubledSum") // reduced: 37 -> 원래는 42가 나와야 한다!!
val doubledSumFromZero = numbers.fold(0) { total, num -> total + num * 2 }
println("folded: $doubledSumFromZero") // folded: 42
```

`reduce()`의 첫번째 iteration의 total은 첫번째 element로 사용되고, num은 두번째 element로 사용된다. 때문에, `total + num * 2` 에서 첫번째 element는 2가 곱해지지 않게 되고, 결과는 42가 나와야 하지만 (첫번째 element 5가 2배가 되지 못 해) 37이 나오게 되는 것이다.

`fold()`의 경우는 초기값이 첫번째 iteration의 total로 사용되기 때문에 의도대로 모든 element의 값이 2배가 된다.


# 참고

- [Collection Aggregate Operations](https://kotlinlang.org/docs/reference/collection-aggregate.html)
- [reduce()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/reduce.html)
- [fold()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/fold.html)
