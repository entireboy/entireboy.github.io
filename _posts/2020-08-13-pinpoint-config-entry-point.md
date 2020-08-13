---
layout: post
title:  "[Pinpoint] MQ listener 모니터링을 위해 entrypoint 추가하기"
date:   2020-08-13 21:18:00 +0900
published: true
categories: [ pinpoint ]
tags: [ pinpoint, apm, monitoring, sqs, mq, message, message queue, message processing, listener, consumer, entry point ]
---

API 호출처럼 URL이 있는 호출은 [Pinpoint]([http://naver.github.io/pinpoint/](http://naver.github.io/pinpoint/))로 모니터링하기 좋다. 그런데, SQS나 MQ 메시지를 처리하는 프로세스의 경우 이런 모니터링이 어렵다.

이럴 때는 `pinpoint.config` 파일에 `profiler.entrypoint` 를 설정해 주면 모니터링이 가능하다.

```
# Needs to be a comma separated list of fully qualified method names. Wild card not supported.
profiler.entrypoint=kr.leocat.test.OneEventHandler.handleEvent,kr.leocat.test.AnotherEventHandler.handleEvent
```

메소드 이름을 콤마(`,`)로 구분해서 직접 적어줘야 하고 와일드카드(`*` 등)는 쓸 수 없다. (으아 이건 최악이야.. message listener 추가될 때 마다 `까먹지 않고` 넣어줘야 하다니..)

{% include image.html file='/assets/img/2020-08-13-pinpoint-config-entry-point.png' alt='Pinpoint method entry point' %}

Pinpoint 2.x 버전에서는 `Path` 가 클래스명과 메소드명이 합쳐진 이름으로 보인다. 그런데 Pinpoint 1.x 버전에서는 `Path` 가 `null` 로 보이지만, 잘 모니터링은 잘 되는걸 볼 수 있다.


# 참고

- [My custom jar application is not being traced. Help! - Pinpoint FAQ](http://naver.github.io/pinpoint/2.0.4/faq.html#my-custom-jar-application-is-not-being-traced-help)
