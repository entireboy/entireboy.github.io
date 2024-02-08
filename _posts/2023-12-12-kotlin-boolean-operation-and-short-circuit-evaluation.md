---
layout: post
title:  "[Kotlin] Boolean 연산자(and, or, &&, ||)와 short-circuit evaluation"
date:   2023-12-12 22:18:00 +0900
published: true
categories: [ kotlin ]
tags: [ kotlin, boolean, bool, short circuit, evaluation, operator, and, or ]
---

# and/or operator

Kotlin은 Boolean 연산자인 `&&`와 `||`를 `and`와 `or`로 바꿔서 사용할 수 있다. 코드가 눈에도 잘 들어오고 읽기 편안해 진다.

```kotlin
// 술을 사기 위해서는 만 18세 이상이어야 하고 본인 확인을 해야 한다.
fun canBuyAlcohol() = overAge() and identified()
```

# and/or의 단점

`and`와 `or`의 아주 큰 단점이 있는데, short-circuit evaluation을 지원하지 않는다는 점이다.

> 술을 사기 위해서는 만 18세 이상이어야 하고 본인 확인을 해야 한다.
> 바꿔 말하면, 만 18세가 되지 않았다면 본인 확인을 할 필요가 없다.

`&&`는 short-circuit evaluation을 지원하기 때문에 18세가 되지 않았을 때 본인 확인을 하지 않는데, `and`는 그렇지 않다. 

{% include image.html file='/assets/img/2023/2023-12-12-kotlin-boolean-operation-and-short-circuit-evaluation.png' alt='and and and' %}


# 참고
- [Boolean.and](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/and.html)
- [Boolean.or](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/or.html)
