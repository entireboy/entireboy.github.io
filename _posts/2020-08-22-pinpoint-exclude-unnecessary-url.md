---
layout: post
title:  "[Pinpoint] 불필요한 URL 모니터링 제외"
date:   2020-08-22 21:18:00 +0900
published: true
categories: [ pinpoint ]
tags: [ pinpoint, apm, monitoring, exclude, url, ant, path, plugin ]
---

Pinpoint로 불필요하게 모니터링이 되는 URL이 있다. 예를 들어, health-check URL 이라거나, favicon, static 파일들, 모니터링 데이터 수집을 위한 prometheus 호출 같은 경우가 있다.

모니터링 중인 웹서버의 `excludeurl` 을 설정하면 된다.

```bash
# Tomcat 인 경우
profiler.tomcat.excludeurl=/elb-health,/favicon.ico,/actuator/**,/static/**

# 요건 jetty
profiler.jetty.excludeurl=/favicon.ico,/static/**

# 요건 reactor netty
profiler.reactor-netty.server.excludeurl=/favicon.ico,/static/**
```

URL은 Ant style 패턴을 사용하면 된다. 하위의 모든 경로를 포함하려면 `**`, 하위의 한번의 경로만 포함하려면 `*`


# 설정 찾는 팁

Pinpoint 2.x 버전 부터는 웹서버가 플러그인 형태로 변경되면서, 문서도 정리된 내용이 없고 설정값 찾기가 힘들다. 플러그인 마다 만든 사람이 다른지 구성도 README도 너무 다르다.

그러면?? 코드를 열어본다.

지원하는 플러그인은 여기([plugins](https://github.com/naver/pinpoint/tree/master/plugins))서 확인할 수 있고, 각 플러그인의 xxxConfig.java 파일에 String으로 선언된 설정값을 찾아 보면 알 수 있다. (아옼!!!!!!) Tomcat의 경우는 [TomcatConfig.java](https://github.com/naver/pinpoint/blob/master/plugins/tomcat/src/main/java/com/navercorp/pinpoint/plugin/tomcat/TomcatConfig.java) 파일, Reactor Netty는 ([ReactorNettyPluginConfig.java](https://github.com/naver/pinpoint/blob/master/plugins/reactor-netty/src/main/java/com/navercorp/pinpoint/plugin/reactor/netty/ReactorNettyPluginConfig.java))에서 확인할 수 있다.
