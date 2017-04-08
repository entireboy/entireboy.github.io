---
layout: post
title:  "[Java] @Deprecated and @deprecated"
date:   2017-04-08 12:18:00 +0900
published: true
categories: [ java ]
tags: [ java, javadoc, annotation, deprecated, deprecation ]
---

`@Deprecated`는 annotation으로 타입, 필드, 메소드 등에 붙일 수 있고, `@Deprecated` 표시 되어 있는 메소드나 필드를 사용하면 빌드할 때 워닝 메시지를 보여준다. 컴파일러에게 이 메소드는 없어질거라는걸 알려주고 쓰지 말도록 경고하는 것이다.

`@deprecated`는 [Javadoc](http://docs.oracle.com/javase/1.5.0/docs/tooldocs/windows/javadoc.html#@deprecated)으로 이 메소드는 어떤 이유로 사라지며 대신 어떻게 사용하라는 내용을 담아줄 수 있다. Javadoc을 보는 사용자에게 알려주는 것이다.

```java
/*
 * @deprecated Replaced by {@link #newOne()}, deprecated for slow performance.
 */
@Deprecated
public void oldOne() {
  ..
}
```

# 참고

- [How and When To Deprecate APIs](http://docs.oracle.com/javase/1.5.0/docs/guide/javadoc/deprecation/deprecation.html)
- [Javadoc - Wikipedia](https://en.wikipedia.org/wiki/Javadoc)
