---
layout: post
title:  "[Jira] 정규식으로 Label 검색하기 (JQL)"
date:   2024-04-23 22:18:00 +0900
published: true
categories: [ jira ]
tags: [ jira, search, query, filter, label, regex, wildcard, jql ]
---

Jira의 일반적인 labels 검색은 label 키워드 전체가 매칭되어야 한다. 지원하는 연산자는 아래 스크린샷처럼 `=`, `!=`, `is not`, `is`, `not in`, `in`이다.

![operators for labels]({{ site.baseurl }}/assets/img/2024/2024-04-23-jira-how-to-search-for-labels-with-wildcard-regex1.png)

label의 일부만 일치하는 것을 찾기 위해 와일드카드(wildcard)나 정규식(RegEx)을 사용하려면 `issueFieldMatch`를 사용하면 된다.

text나 summary는 `~`연산자로 텍스트 검색이 가능한데 다른 필드는 labels처럼 지원하지 않는 경우가 많은데, 이럴 때 `issueFieldMatch`함수를 사용하면 정규식을 사용할 수 있다.

```
issueFunction in issueFieldMatch("project = MY", "labels", "스프린트중간#?[0-9]?")
```
![RegEx for searching for labels]({{ site.baseurl }}/assets/img/2024/2024-04-23-jira-how-to-search-for-labels-with-wildcard-regex2.png)


# 참고
- [JQL Trick: How to search for labels with a wildcard expression in Jira Server](https://mraddon.blog/2018/11/19/jql-trick-how-to-search-for-labels-with-a-wildcard-expression-in-jira-server/)
- [How to use wildcards for lables field ?](https://community.atlassian.com/t5/Jira-Software-questions/How-to-use-wildcards-for-lables-field/qaq-p/645139)
