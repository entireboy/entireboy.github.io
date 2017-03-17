---
layout: post
title:  "[Typesafe Config] include syntax: substitution (이미 설정된 config 재사용하기)"
date:   2016-12-20 21:11:00 +0900
published: true
categories: [ typesafe, config ]
tags: [ typesafe config, typesafe, config, include, syntax, reuse, substitution ]
---

Typesafe config 에서는 다른 곳에서 설정해둔 값을 쉽게 재사용할 수 있다.

```javascript
contact {
  me {
    name = ${my.name} # the value is evaluated to "ME"
  }
  you {
    name ="YOU!!"
  }
}
my {
  name = "ME"
  age = "secret"
}
```

`contact.me.name`의 `${my.name}`은 다른 곳에서 별도로 선언된 `my.name`의 값인 `ME`로 설정된다. 알고 나면 참 쉬운데, 모르면 알기 어렵다. @_@

# 참고

- [https://github.com/typesafehub/config/blob/master/HOCON.md#include-syntax](https://github.com/typesafehub/config/blob/master/HOCON.md#include-syntax)
- [http://blog.leocat.kr/post/153820414982/typesafes-config-config%EC%9D%98-default%EA%B0%92-%EC%84%A4%EC%A0%95](http://blog.leocat.kr/post/153820414982/typesafes-config-config%EC%9D%98-default%EA%B0%92-%EC%84%A4%EC%A0%95)
