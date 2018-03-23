---
layout: post
title:  "[Groovy] 문자열 자르기"
date:   2018-03-23 23:18:00 +0900
published: true
categories: [ groovy ]
tags: [ groovy, string, tokenize, split ]
---

Groovy에서 문자열을 자를 때 `tokenize` 함수를 쓰거나, Java의 `split` 함수를 쓰면 된다.

```groovy

```




new File(hostFileName).eachLine { line ->
  def (ip, host, type) = line.tokenize(' ')
  hostnames[ip] = host
}





http://groovy-lang.org/semantics.html
