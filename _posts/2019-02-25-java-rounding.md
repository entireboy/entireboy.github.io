---
layout: post
title:  "[Java] 숫자 반올림/올림/내림"
date:   2019-02-25 22:18:00 +0900
published: true
categories: [ java ]
tags: [ java, jdk, jdk9, number, numeric, rounding, round, RoundingMode, BigDecimal, scale, precision, deprecated ]
---

항상 헷갈리는 반올림, 올림, 내림 `RoundingMode`를 정리해 보자.

```java
// 단순하게 소수점 제거하고 프린트 하는 함수 - 화면 사이즈상 줄바꿈이나 띄워쓰기 제거
public void print(BigDecimal num) {
    System.out.print(num.setScale(0, RoundingMode.UP));
    System.out.print(num.setScale(0, RoundingMode.DOWN));
    System.out.print(num.setScale(0, RoundingMode.CEILING));
    System.out.print(num.setScale(0, RoundingMode.FLOOR));
    System.out.print(num.setScale(0, RoundingMode.HALF_UP));
    System.out.print(num.setScale(0, RoundingMode.HALF_DOWN));
}

print(BigDecimal.valueOf(5.5D));
print(BigDecimal.valueOf(1.6D));
print(BigDecimal.valueOf(1.1D));
print(BigDecimal.valueOf(-1.1D));
print(BigDecimal.valueOf(-1.6D));
print(BigDecimal.valueOf(-5.5D));
```

결과는?? 보통 양수는 안 헷갈린다. 음수를 보면.. 읭?!?!

| Input | UP | DOWN | CEILING | FLOOR | HALF_UP | HALF_DOWN |
|:-----:|:--:|:----:|:-------:|:-----:|:-------:|:---------:|
|5.5|6|5|6|5|6|5|
|1.6|2|1|2|1|2|2|
|1.1|2|1|2|1|1|1|
|-1.1|-2|-1|-1|-2|-1|-1|
|-1.6|-2|-1|-1|-2|-2|-2|
|-5.5|-6|-5|-5|-6|-6|-5|

학교에서 배우는 일반적인 반올림은 단어 그대로 `HALF_UP`이니 알기 쉽다. 그런데, 올림과 내림은?? `FLOOR`와 `DOWN`은 무슨 차이지?? `CEILING`과 `UP`은?? 자세히 보면 음수에서 차이가 난다. `5.5`의 `FLOOR`와 `DOWN`은 모두 `5`로 같지만, `-5.5`는 `FLOOR`는 `-6`이고 `DOWN`은 `-5`이다. `CEILING`과 `UP`도 마찬가지이다.

간단히 그림으로 그려보면, 알기 쉽다. 가운데 `빨간줄`이 숫자 `0`이고, 오른쪽이 양수, 외쪽이 음수이다.

{% include image.html file='/assets/img/2019-02-25-java-rounding1.jpg' alt='Rounding behavior' %}

- `CEILING`은 양수 방향으로 올림하고, `FLOOR`는 음수 방향으로 내림한다.
- `UP`은 `0`에서 멀어지는 방향으로 올림하고, `DOWN`은 `0`에 가까워지는 방향으로 내림한다.
- `HALF_UP`과 `HALF_DOWN`은 이름에서 알 수 있듯이 `UP`과 `DOWN`과 같은 방향이다.


# deprecated RoundingMode

BigDecimal에서 `RoundingMode`를 줄 때 쓰던 BigDecimal.ROUND_XXX는 jdk9부터 deprecated되었다. RoundingMode.XXX를 사용하자. ([RoundingMode](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/math/RoundingMode.html))

{% include image.html file='/assets/img/2019-02-25-java-rounding2.png' alt='Deprecated rounding mode' width='400px' %}


# 참고

- [BigDecimal](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/math/BigDecimal.html)
- [RoundingMode](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/math/RoundingMode.html)
