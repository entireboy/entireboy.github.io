---
layout: post
title:  "[Java] headless mode - 마우스/키보드/모니터 등이 없을 때 옵션 설정"
date:   2017-10-30 22:18:00 +0900
published: true
categories: [ java ]
tags: [ java, headless, mode, opt, option, property ]
---

Headless Mode는 마우스/키보드/모니터 등이 없는 서버에서 사용하면 좋은 옵션이다. 모니터나 키보드 등이 없다면 화면 디스플레이를 하거나 입력을 받을 필요가 없고, 폰트 같은 그래픽 관련된 클래스를 로딩하지 않아도 되고 예외처리를 할 필요도 없다.

```shell
-Djava.awt.headless=true
```

모니터나 키보드가 없다면 대부분 서버에서 동작시키는 형태일테니, `-server` 옵션도 같이 주면 좋을지도.. ([-server](http://www.oracle.com/technetwork/java/whitepaper-135217.html)는 그 상황에 따라 맞춰서..)


# 참고

- [Using Headless Mode in the Java SE Platform](http://www.oracle.com/technetwork/articles/javase/headless-136834.html)
- [https://blog.idrsolutions.com/2013/08/what-is-headless-mode-in-java/](https://blog.idrsolutions.com/2013/08/what-is-headless-mode-in-java/)
- [The Java HotSpot Performance Engine Architecture](http://www.oracle.com/technetwork/java/whitepaper-135217.html)
