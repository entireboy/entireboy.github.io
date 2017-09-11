---
layout: post
title:  "[Groovy] Spaceship operator (<=>)"
date:   2017-09-11 22:18:00 +0900
published: true
categories: [ groovy ]
tags: [ groovy, operator, spaceship, spaceship operator, compare, compareTo, comparator, delegate, sort ]
---

# Spaceship operator

Groovy에는 우주선 연산자(spaceship operator)가 있다. `<=>` 생긴게 우주선 같아서 그런 이름을 붙였는지 모르겠지만, 이걸 처음 본 순간 무슨 일을 하는 녀석인지 도저히 감이 안 오는 처음 보는 녀석 T_T ruby, php 등에서는 많이 지원되는 연산자인 것 같다.

이 spaceship operator는 `compareTo`를 호출(delegate)해 준다.

```groovy
assert (1 <=> 1) == 0
assert (1 <=> 2) == -1
assert (2 <=> 1) == 1
assert ('a' <=> 'z') == -1
```

연산자 왼쪽에 있는 녀석의 compareTo를 delegate 해주는 녀석이기 때문에 좌항의 compareTo가 호출되는 형태이다. 따라서 좌항의 compareTo 파라미터로 우항의 클래스 타입이 없다면 예외가 발생한다.

```groovy
1 <=> "ABC" // java.lang.ClassCastException: java.lang.String cannot be cast to java.lang.Integer
1 <=> 1.2 // -1. 오호
```

(Integer) 1과 (String) "ABC"의 비교는 "ABC"를 Integer(Number)로 캐스팅할 수 없기 때문에 예외가 발생한다. 하지만 신기하게도 `Integer#compareTo(Double)` 메소드는 없는데도 예외가 발생하지 않고 잘 동작한다. 그건 groovy가 숫자 비교를 편하게 해주는 compareTo를 많이 지원해 주기 때문이다. (org.codehaus.groovy.runtime.DefaultGroovyMethods#compareTo(java.lang.Number, java.lang.Number))


# Compare with null

참 착하게도 null을 비교할 때는 NullPointerException이 발생하지 않게 해주어서 고맙다.

```groovy
assert ("ABCD" <=> null) == 1
assert (null <=> "ABCD") == -1
assert (null <=> null) == 0
```


# Sort

이 연산자를 어디에 많이 쓰는고 하면..

- NullPointerException 고민 없이 비교를 한다거나,
- 정렬할 때 comparator의 연산으로 쓸 수 있다.

```groovy
def arr = [1, 7, 5, 3, 8, 2, 9, 6]
Comparator ascComparator = { a, b -> a <=> b }
Collections.sort(arr, ascComparator)
assert arr == [1, 2, 3, 5, 6, 7, 8, 9]

// 귀찮은 null check가 없어도 문제 없다. (null first)
def arr = [1, 7, null, -2, 9]
Comparator ascComparator = { a, b -> a <=> b }
Collections.sort(arr, ascComparator)
assert arr == [null, -2, 1, 7, 9]
```


# 참고

- [Spaceship operator - Groovy Operators](http://groovy-lang.org/operators.html#_spaceship_operator)
- [The Groovy Spaceship Operator Explained](https://objectpartners.com/2010/02/08/the-groovy-spaceship-operator-explained/)
