---
layout: post
title:  "[Kotlin] 코틀린으로 보는 문(statement)과 식(expression)"
date:   2019-12-29 22:18:00 +0900
published: true
categories: [ kotlin ]
tags: [ kotlin, java, statement, expression, if, else, when, switch, comparison ]
---

# statement 와 expression

자바의 if-else는 `문(statement)`이고, 코틀린의 if-else는 `식(expression)`이다.

`문`과 `식`은 무슨 차이가 있을까?? `식`은 값을 만들어낸다. 아주 간단히 값을 return을 한다고 생각해도 좋을 것 같다. 아래 코드는 코틀린의 if-else인데, `식`이기 때문에 if와 else 블럭의 값을 리턴해 주어서 바로 `maxValue`에 값으로 대입할 수 있다.

```kotlin
fun max(a: Int, b: Int) = if (a > b) a else b

fun main(args: Array<String>) {
  val maxValue = max(1, 2)
  println("Max is $maxValue")
}
```

자바였다면 아래처럼 if, else 블럭에 return을 명시해 줘야 한다. (위의 코틀린 코드와 아래 자바 코드는 똑같은 코드이다.)

```java
private static int max(int a, int b) {
  if (a > b) {
    return a;
  } else {
    return b;
  }
}

public static void main(String[] args) {
  int maxValue = max(1, 2);
  System.out.println("Max is " + maxValue);
}
```

맨처음 샘플에서 처럼 코틀린의 if-else는 자바의 3항 연산자처럼 사용할 수 있기 때문에, 코틀린은 별도로 3항 연산자를 제공하지 않는다. 그리고 저렇게 if-else를 사용하는 코드를 많이 볼 수 있다.


# when (switch)

if-else와 마찬가지로 자바의 switch와 같은 when도 `식`으로 값을 리턴한다. `getMnemonic`함수는 enum 값에 따라 문자열을 리턴한다.

```kotlin
enum class Color {
    RED, ORANGE, YELLOW, GREEN, BLUE, INDIGO, VIOLET
}

fun getMnemonic(color: Color) =
    when (color) {
      Color.RED -> "Richard"
      Color.ORANGE -> "Of"
      Color.YELLOW -> "York"
      Color.GREEN -> "Gave"
      Color.BLUE -> "Battle"
      Color.INDIGO -> "In"
      Color.VIOLET -> "Vain"
    }

println(getMnemonic(Color.RED))
// 결과 "Richard"
```

만일, 이 코드가 자바였다면.. 매번 return 넣어줄 생각만 해도;; 중간에 break나 return 하나 빼먹으면 그 결과는..

```java
private static String getMnemonic(Color color) {
  switch (color) {
    case RED:
      return "Richard";
    case ORANGE:
      return "Of";
    /* case .. 어휴 이쯤에서 포기 */
  }
}
```
