---
layout: post
title:  "[RegEx] Greedy 와 lazy 매칭"
date:   2022-04-16 22:18:00 +0900
published: true
categories: [ regex ]
tags: [ regex, greedy, lazy, quantifier ]
---

# TL;DR

정규식은 greedy가 기본 설정이다. 매칭이 되는한 끝까지 greedy라는 단어 그대로 탐욕적으로 매칭한다.

(게으르게 lazy로) 짧게 매칭하고 싶다면 quantifier 뒤에 `?`를 사용한다.: `*?`, `+?`, `??`


# Greedy와 Lazy

## Default greedy mode

정규식을 쓰면 `*`과 `+`는 몇 개 까지 매칭되는지 모를 때가 있다. 길게 매칭되는지 짧게 매칭되는지..

```
ad-service.ad-server.deploy.pipeline
ad-service.ad-server.rollback.pipeline
ad-service.ad-tracking.deploy.pipeline
ad-service.ad-tracking.rollback.pipeline
```

위와 같은 목록들이 있을 때, `ad-server`와 `ad-tracking`만 꺼내고 싶다. 일단 아래처럼 시도해 보자.

```
ad-service\.(.+)\..+
```

샘플: [https://regexr.com/6jp7a](https://regexr.com/6jp7a)

어라?? `$1`의 그룹으로 `ad-server`와 `ad-tracking`가 아닌, `ad-server.deploy`와 `ad-server.rollback`, `ad-tracking.deploy`, `ad-tracking.rollback`가 잡힌다. 정규식은 기본적으로 greedy 방식이다. 때문에 `.+`로 잡을 수 있는 가장 긴 내용을 매칭하게 된다.


## Lazy mode

`ad-server`와 `ad-tracking`을 매칭하고 싶다면 `.+` 다음 처음으로 `\.`이 나올 때 까지인데 인데, 이럴 때는 lazy mode로 동작하게 `*`나 `+`뒤에 `?`를 추가한다. `*?`, `+?`, `??`와 같이 사용할 수 있다.

```
ad-service\.(.+?)\..+
```

이제 `$1` 그룹으로 `ad-server`와 `ad-tracking`가 잡히는 것을 확인할 수 있다.

샘플: [https://regexr.com/6jp77](https://regexr.com/6jp77)


# 참고
- [Greedy and lazy quantifiers](https://javascript.info/regexp-greedy-and-lazy)
