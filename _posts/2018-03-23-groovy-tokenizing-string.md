---
layout: post
title:  "[Groovy] 문자열 자르기"
date:   2018-03-23 23:18:00 +0900
published: true
categories: [ groovy ]
tags: [ groovy, string, tokenize, split ]
---

Java 문자열을 자를 때 많이 쓰는 함수는 `split`이다. `split`은 정규식도 가능하다는 장점이 있다. Groovy는 당연히 `split`을 사용할 수도 있지만, `tokenize`함수도 제공된다. 둘의 차이를 살짝 비교해 보자.

`tokenize`는 문자열을 나눌 문자(`char`)를 나열하면 된다. 파라미터를 주지 않으면 whitespace(공백, 탭 등등)로 잘라진다.

```groovy
String  testString='hello world'
assert ['hello','world'] == testString.tokenize()
assert ['he', 'o wor', 'd'] == testString.tokenize('l')
assert ['he', ' w', 'r', 'd'] == testString.tokenize('lo')
```

`lo`처럼 문자열을 주면 각 문자로 나눠진다. `split`의 정규식 `[lo]`와 같은 효과(?)가 나타난다. 단, `split`과는 다르게 `tokenize`의 결과로 empty string은 나오지 않는다.

```groovy
String  testString='hello world'
assert ['he', ' w', 'r', 'd'] == testString.tokenize('lo')
assert ['he', '', '', ' w', 'r', 'd'] == testString.split('[lo]')
```

csv 파일 구분자를 열거하는 형태의 활용을 하면 좋다. Groovy의 [Multiple assignment](http://groovy-lang.org/semantics.html#_multiple_assignment)와 함께 사용하면 한줄로 tokenizing이 되는 멋진 효과를 볼 수 있다.

```groovy
new File('./hosts.csv').eachLine { line ->
  def (ip, host, type) = line.tokenize(', ')
  println "Host(${host)})'s IP is ${ip}'"
}
```


# 참고

- [Class StringGroovyMethods - Groovy docs](http://docs.groovy-lang.org/2.4.0/html/gapi/org/codehaus/groovy/runtime/StringGroovyMethods.html)
- [Groovy : tokenize() vs split()](http://www.tothenew.com/blog/groovy-tokenize-vs-split/)
- [Multiple assignment - Groovy Semantics](http://groovy-lang.org/semantics.html#_multiple_assignment)
