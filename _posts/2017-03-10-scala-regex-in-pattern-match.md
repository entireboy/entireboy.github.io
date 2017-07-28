---
layout: post
title:  "[Scala] RegEx in pattern match"
date:   2017-03-10 22:53:00 +0900
published: true
categories: [ scala ]
tags: [ scala, regex, pattern match, match, captured group ]
---

Scala pattern match에서 사용할 패턴으로 정규식을 사용할 수 있다. String 클래스의 implicit function(method) `r`로 [RegEx](http://www.scala-lang.org/api/current/scala/util/matching/Regex.html)를 얻을 수 있다. 만들어진 RegEx를 pattern으로 사용하자. 파라미터(?)를 준다면 정규식에서 잡힌 녀석들(captured group)을 사용할 수 있다. `\1`, `\2`와 같은 그룹 번호를 일일이 변수에 넣어주지 않아도 돼서 편하다.

```scala
// text to test
val srv = "_https._tcp.myservice.domain.com"

val HttpType = """_(https?)._(tcp|udp)+\..+""".r
val service = srv match {
  case HttpType(service) => "Doesn't come due to capture count is different"
  case HttpType(service, proto) => service
case _ => {/* do something else */}
}

// result is 'https'
```

위의 예제에서는 두번째 `case HttpType(service, proto)`에 매치된다. 첫번째 case는 그룹 개수가 맞지 않기 때문에 건너뛰고, 두번째 case에 걸린다. 순서대로 `(https?)`에 잡히는 녀석은 `service`, `(tcp|udp)`에 잡히는 녀석은 `proto`로 사용할 수 있다. 예제는 처음 잡힌 `service`만 리턴했기 때문에 결과는 `https`가 된다.

{% include google-ad-content %}

# tip

Scala에서 정규식을 만들 때 `"\\."` 대신 `"""\."""`을 사용하면 `\`의 지옥에서 벗어날 수 있다.

```scala
val HttpType = "_(https?)\\._(tcp|udp)+\\\\.\\.+".r
```

이게 패턴의 `\`인지 String의 escape를 위한 `\`인지 도무지 알아 볼 수가 없다. 아래처럼 알아보기 쉽게 이쁘게 써주자.

```scala
val HttpType = """_(https?)._(tcp|udp)+\..+""".r
```

# 참고

- [RegEx - Scala Standard Library](http://www.scala-lang.org/api/current/scala/util/matching/Regex.html)
- [RegExr](http://regexr.com/) RegEx 패턴 테스트
