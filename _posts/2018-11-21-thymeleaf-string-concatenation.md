---
layout: post
title:  "[Thymeleaf] 문자열 합치기"
date:   2018-11-21 22:18:00 +0900
published: true
categories: [ thymeleaf ]
tags: [ thymeleaf, string, concat, concatenation, interpolation, substitution, java, template ]
---

(까먹을까봐 기록차)

Thymeleaf에서 문자열 조합을 하려면..

```html
<div th:text="'Hello, ' + ${name} + '!!'"></div>
```

먼가 지저분하다. 이럴 때, `|`로 싸주면 문자열을 조합할 수 있다.

```html
<div th:text="|Hello, ${name}!!|"></div>
```


# 참고

- [Literal substitutions - Thymeleaf doc](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html#literal-substitutions)
- [Thymeleaf: Concatenation - StackOverflow](https://stackoverflow.com/questions/16119421/thymeleaf-concatenation-could-not-parse-as-expression/20589845#20589845)
- [Thymeleaf doc](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html)
- [Thymeleaf + Spring doc](https://www.thymeleaf.org/doc/tutorials/3.0/thymeleafspring.html)
