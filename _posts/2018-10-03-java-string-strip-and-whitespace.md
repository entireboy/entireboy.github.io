---
layout: post
title:  "[Java] String strip()과 whitespace"
date:   2018-10-03 23:18:00 +0900
published: true
categories: [ java ]
tags: [ java, jdk, 11, string, strip, trim, whitespace, unicode ]
---

> **TL;DR**
> 유니코드의 whitespace에는 `tab 문자`, `공백`, `CR`, `LF` 이외에도 훨씬 많은 것들이 있다.
> 이 모든 whitespace를 제거하려면, `trim()` 대신 JDK 11에 추가된 `strip()`을 사용해야 한다.


Java 11 String에는 새로 추가된 `strip()`, `stripLeading()`, `stripTrailing()`이 있다. `trim()`처럼 앞뒤의 whitespace를 지운 새로운 String 객체를 반환하는데, `strip()`과 `trim()`은 지우게 되는 whitespace 대상이 조금 다르다.

whitespace라고 하면 간단하게 `tab 문자(U+0009)`, `공백(U+0020)`, `CR(U+000D)`, `LF(U+000A)` 등의 문자만 생각했는데, 유니코드에서는 그 이외에도 [상당히 많은 whitespace](https://en.wikipedia.org/wiki/Whitespace_character)가 존재한다. 특히나 공백의 가로폭에 따라 아래 이미지와 같이 사이즈별로 존재한다.

{% include image.html file='/assets/img/2018-10-03-java-string-strip-and-whitespace.png' alt='whitespaces' %}

위와 같은 whitespace도 함께 제거하고 싶으면 `trim()`이 아닌 `strip()`을 사용해야 한다. 아래 코드에서 `U+205F`값을 가지는 `medium mathematical space`가 제거되는 것을 볼 수 있다.

```java
public class StripAndTrimTest {
  public static void main(String[] args) {
      String str = "my text\u205F";
      System.out.println("'" + str.strip() + "'");
      System.out.println("'" + str.trim() + "'");
  }
}
```

```
'my text'
'my text ' // trip()은 `U+205F`가 제거되지 않았다
```

`strip()`은 모든 유니코드 whitespace를 제거하고, `trim()`은 `U+0020` 이하의 값을 가지는 whitespace만을 제거한다. `U+0020` 이하는 `tab 문자`, `공백`, `CR`, `LF` 등을 포함한다. 그 목록은 [Whitespace character - Wikipedia](https://en.wikipedia.org/wiki/Whitespace_character)에서 확인할 수 있다.

이 때문인지 `trim()`의 javadoc 설명이 바뀌었다.

> **trim(): JDK 8**
> Returns a string whose value is this string, with any leading and trailing whitespace removed.
>
> **trim(): JDK 11**
> Returns a string whose value is this string, with all leading and trailing space removed, where space is defined as any character whose codepoint is less than or equal to 'U+0020' (the space character).
>
> **strip(): JDK 11**
> Returns a string whose value is this string, with all leading and trailing white space removed.


# 참고

- [Java 11 - String Changes](https://www.logicbig.com/tutorials/core-java-tutorial/java-11-changes/string-changes.html)
- [Whitespace character - Wikipedia](https://en.wikipedia.org/wiki/Whitespace_character)
- [Class String - JavaDoc JDK 11](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html)
- [Class String - JavaDoc JDK 8](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html)


본문 이미지 출처: [Wikipedia](https://en.wikipedia.org/wiki/Whitespace_character)
