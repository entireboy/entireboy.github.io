---
layout: post
title:  "[Kotlin] enum이나 sealed class 그리고 대수적 타입(algebraic data type)"
date:   2020-02-08 22:18:00 +0900
published: true
categories: [ kotlin ]
tags: [ kotlin, statement, expression, when, switch, enum, sealed class, sum type, product type ]
---

# statement 와 expression

코틀린의 when은 `문(statement)`이면서 동시에 `식(expression)`이다.

when의 결과를 받아서 사용하는 경우는 when이 `식`이 되지만, 결과를 사용하지 않는다면 if-else 처럼 `문`으로만 처리된다.

```kotlin
enum class Color {
  RED, ORANGE, YELLOW
}

fun whenWithStatement(color: Color) {
  when (color) {
    Color.RED -> foo.handleRed()
    Color.ORANGE -> foo.callMeTangerine()
    Color.YELLOW -> foo.doWithYellow()
  }
}

fun whenWithExpression(color: Color) = when (color) {
  Color.RED -> foo.handleRed()
  Color.ORANGE -> foo.callMeTangerine()
  Color.YELLOW -> foo.doWithYellow()
}
```

`whenWithStatement` 함수의 when은 `문`이다. `color`에 따라서 패턴매칭을 하고, 매칭되는게 있으면 해당 코드를 실행하게 된다. 예를 들어, `ORANGE` 색깔이 들어오면 `callMeTangerine`을 호출한다. 하지만 `whenWithExpression` 함수의 when은 `callMeTangerine`을 실행하고 그 결과를 리턴까지 하게 된다. 때문에 `식`이다. `ORANGE` 색깔이 들어오면 `callMeTangerine`을 호출하고, `callMeTangerine`은 결과를 리턴해야만 한다. 따라서 아래처럼 그 리턴을 받아서 사용할 수도 있다.

```kotlin
val tangerine = whenWithExpression(color)
```


# enum 값이 추가되면

그런데 여기에 enum 값이 추가되면 어떻게 될까?? `Color`에 `BLUE`를 더해 보자.

```kotlin
enum class Color {
  RED, ORANGE, YELLOW, BLUE
}
```

`whenWithStatement` 함수에서는 이런 warning 메시지를 볼 수 있다. `BLUE`에 대해 case를 생성하거나 `else`를 추가해 주라는 메시지이다. warning이기 때문에 추가해 주지 않더라도 실행하는데는 문제가 되지 않는다. if-else처럼 분기`문`의 한 종류일뿐이고, `else`가 없다고 문제가 되지 않는 것이다.

> 'when' expression on enum is recommended to be exhaustive, add 'BLUE' branch or 'else' branch instead

{% include image.html file='/assets/img/2020-02-08-kotlin-enum-and-algebraic-data-type1.png' width='500px' %}

그리고 `whenWithExpression` 함수에서는 warning이 아닌 error 메시지를 볼 수 있다. 컴파일 에러로 아예 실행도 되지 않는다. 이 when은 `식`이기 때문에 값을 리턴해야 한다. `BLUE`가 들어왔을 때는 진행할 분기도 없고 리턴할 값이 없기 때문에 문제가 된다. 따라서, 컴파일 오류가 발생하고 꼭 모든 enum의 값을 분기 태우거나 `else`를 만들어 줘야 한다.

> 'when' expression must be exhaustive, add necessary 'BLUE' branch or 'else' branch instead

{% include image.html file='/assets/img/2020-02-08-kotlin-enum-and-algebraic-data-type2.png' width='500px' %}

게다가 이 `whenWithExpression` 함수는 리턴이 `Unit`으로 리턴할 값을 주지 않아도 되는데(void)도 말이다.


# 식으로 판단되면 모든 값의 분기가 강제된다

여기서 포인트는 when이 `식`으로 사용된다면 컴파일도 되지 않도록, `BLUE`나 `else`의 패턴매칭이 강제된다는 것이다.

실제로 일을 하다 보면 enum에 값을 추가할 때 마다 모든 코드를 쫓아다니면서 case를 추가해야 하는데 종종 누락하는 경우가 생긴다. 이럴 때 `whenWithExpression` 함수와 같이 `식`으로 인식된다면 컴파일 에러가 발생하기 때문에 체크하기가 쉽다.


# sealed class

enum은 `합타입`이다. 대수적 데이터 타입(algebraic data type)은 `합타입(sum type)`과 `곱타입(product type)`으로 이루어진다. `합타입`은 가질 수 있는 모든 종류를 열거할 수 있다. 그렇기 때문에 when에서 처럼 누락된 케이스를 찾을 수 있고, 강제할 수도 있다.

그리고 코틀린에는 enum과 유사한 sealed class가 있다. 이 녀석 역시 enum과 마찬가지로 `합타입`이다.

```kotlin
sealed class Day {
  object Sunday : Day()
  object Weekday : Day()
  object Saturday : Day()
}

fun whenWithStatement(day: Day): Unit {
  // enum과 동일한 warning
  when (day) {
    Day.Sunday -> foo.handleRed()
    Day.Weekday -> foo.doWithYellow()
  }
}

fun whenWithExpression(day: Day): Unit = when (day) {
  Day.Sunday -> foo.handleRed()
  Day.Weekday -> foo.doWithYellow()

// else나 'Saturday'가 없으면 컴파일 오류
//else -> throw IllegalArgumentException("으앜!!")
}
```


# 결론

코틀린은 같은 코드도 `식`과 `문`으로 사용될 수 있다.

enum과 sealed class는 `합타입`이다.

가능하다면 내 실수를 컴파일 오류로 확인할 수 있는 행태로 사용하자. 예를 들어, when을 사용한다면 `문` 보다는 `식`으로 사용하자.

`식`으로 사용되는 코드들이 있다면 섣불리 `문`으로 변경하지 말자. 이런 컴파일 오류를 만나기 위한 의도로 작성된 코드가 워닝으로 끝나고 버그를 안을 수 있다.


# 참고

- [대수적 데이터 타입이(algebraic data type)이란? With Kotlin](https://medium.com/@lazysoul/%EB%8C%80%EC%88%98%EC%A0%81-%EB%8D%B0%EC%9D%B4%ED%84%B0-%ED%83%80%EC%9E%85%EC%9D%B4-algebraic-data-type-%EC%9D%B4%EB%9E%80-26d9e73d96b6)
- [Algebraic data type(대수적 타입) - Wikipedia](https://en.wikipedia.org/wiki/Algebraic_data_type)
- [Sum type(합타입) - Wikipedia](https://en.wikipedia.org/wiki/Sum_type)
