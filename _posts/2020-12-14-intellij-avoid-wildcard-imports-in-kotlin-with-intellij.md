---
layout: post
title:  "[IntelliJ] Kotlin 코드에서 wildcard('*') import 사용하지 않기"
date:   2020-12-14 22:18:00 +0900
published: true
categories: [ intellij ]
tags: [ intellij, kotlin, avoid, wildcard, import, ktlint, lint ]
---

ktlint를 사용하면 `import java.util.Locale`와 같은 [wildcard import를 사용하지 못 하게](https://ktlint.github.io/#rule-import) 한다.

{% include image.html file='/assets/img/2020-12-14-intellij-avoid-wildcard-imports-in-kotlin-with-intellij1.png' alt='Wildcard import ktlint warn' %}

이유는
- 명확하게 선언할 수 있기 때문에 실수를 줄일 수도 있고,
- 다른 패키지에 동일한 클래스가 존재할 수도 있고,
- Kotlin 같은 경우는 패키지 레벨의 함수도 선언할 수 있기 때문에 혼란이 있을 수 있다.

여러 실수가 많이 있을 수 있고, 이런 실수로 일하다가 1시간을 헤맨 적도 있다. 심지어 페어 프로그래밍을 하고 있어서 보는 눈도 많았는데 아무도 못 봤다.. @_@

그런데, 문제는 IntelliJ가 동일한 패키지에서 있는 클래스/함수를 5개 이상의 import를 사용하면 wildcard import로 바꿔 버린다. 자동으로 바꾸지 못 하도록 설정을 바꿔주자. IntelliJ 설정에서 `Editor` > `Code Style` > `Kotlin` 설정에서 아래와 같이 바꿔준다. Kotlin 클래스에서만 적용되는 내용이기 때문에, Java 같은 다른 언어는 적용되지 않는다.

{% include image.html file='/assets/img/2020-12-14-intellij-avoid-wildcard-imports-in-kotlin-with-intellij2.png' alt='Config wildcard import in IntelliJ' %}

Wildcard import를 사용하지 않으려면 `Use single level import`를 체크하면 된다. 그리고 마지막으로 맨 아래에 있는 `Packages to Use Import with '*'` 설정을 바꿔준다. 특정 패키지를 import 하는 경우 위의 설정과는 별도로 무조건 wildcard import로 바꿔주는 클래스이다. `java.util.*`는 자주 사용하는 클래스들이 있어서 제거해 주는 편이 정신건강에 좋았다.

ktlint.. 다 좋은데, 너무 빡빡하게 체크해서 피곤할 때가 있다. 줄바꿈이 하고 싶다고!!


# 참고
- [No wildcard / unused imports. - ktlint](https://ktlint.github.io/#rule-import)
- [Avoiding Wildcard imports in Java/Kotlin with IntelliJ](https://jiga.dev/avoiding-wildcard-imports-in-java-kotlin-with-intellij/)
