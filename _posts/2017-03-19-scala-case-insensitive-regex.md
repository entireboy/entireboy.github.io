---
layout: post
title:  "[Scala] Case insensitive RegEx"
date:   2017-03-19 22:53:00 +0900
published: true
categories: [ scala ]
tags: [ scala, regex, flag, case insensitive, java ]
---

대소문자를 가리지 않는 RegEx를 만들기. `i` flag를 준다. 참 쉽죠잉-

```scala
val prodEnvRegex = """(?i)PROD""".r
```

사실 이건 Scala가 아니라 [Java RegEx](http://docs.oracle.com/javase/6/docs/api/java/util/regex/Pattern.html#special) 문법이다. RegEx 시작에 (?idmsux) 형태로 플래그를 줄 수 있고, 각각은 이런 의미를 가지고 있다.:

- i [CASE_INSENSITIVE](http://docs.oracle.com/javase/6/docs/api/java/util/regex/Pattern.html#CASE_INSENSITIVE)

- d [UNIX_LINES](http://docs.oracle.com/javase/6/docs/api/java/util/regex/Pattern.html#UNIX_LINES)

- m [MULTILINE](http://docs.oracle.com/javase/6/docs/api/java/util/regex/Pattern.html#MULTILINE)

- s [DOTALL](http://docs.oracle.com/javase/6/docs/api/java/util/regex/Pattern.html#DOTALL)

- u [UNICODE_CASE](http://docs.oracle.com/javase/6/docs/api/java/util/regex/Pattern.html#UNICODE_CASE)

- x [COMMENTS](http://docs.oracle.com/javase/6/docs/api/java/util/regex/Pattern.html#COMMENTS)

각 플래그는 on-off 형태이고, `(?)`로 지정해 주면 on, 켜진다.

# 참고

- [Scala regex ignorecase - Stack Overflow](http://stackoverflow.com/questions/17930774/scala-regex-ignorecase)
- [Special constructs (non-capturing) of Class java.util.regex.Pattern](http://docs.oracle.com/javase/6/docs/api/java/util/regex/Pattern.html#special)
