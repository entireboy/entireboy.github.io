---
layout: post
title:  "[Spring Boot] x-forwarded-xxx 헤더 설정하기"
date:   2019-06-19 22:18:00 +0900
published: true
categories: [ spring ]
tags: [ spring, spring boot, tomcat, http, header, x-forwarded, x-forwarded-for, xff, proxy ]
---

> **TL;DR**: Tomcat을 사용하는 경우, 내부 proxy(nginx, L4, ELB 등)의 IP 대역이 `10/8`, `192.168/16`, `169.254/16`, `127/8`, `172.16/12`, `::1`가 아닌 경우 `internalProxies`를 설정해 줘야 한다. (아래 내용 참조)


# x-forwarded 헤더

웹서버 바로 앞의 request IP로 들어오기 때문에 웹서버 앞단에 nginx나 L4 등의 proxy를 거치는 경우, 웹서버로 들어오는 모든 요청 IP는 웹서버 바로 앞의 proxy인 nginx나 L4의 IP로 남는다. 이럴 때 요청자의 IP나 port를 header로 넘겨주면 실제 호출한 client를 알 수 있게 해준다. header에 아래처럼 거치는 proxy를 이어붙여서 어디로부터 요청이 들어왔는지 확인할 수 있다.

```
X-Forwarded-For: client, proxy1, proxy2
```

이렇게 헤더에 값을 넣기 위해 설정할 항목들은 아래와 같고, 원하는 내용만 설정하면 된다. 웹서비스의 경우 보통 client IP 확인을 위한 `x-forwarded-for`가 가장 중요할 것이다.

- X-Forwarded-For: client IP
- X-Forwarded-Port: request port
- X-Forwarded-Proto: request protocol (e.g. http, https)


# nginx

nginx 설정 파일(`nginx.conf`)에 아래 내용을 추가한다. [Using the Forwarded header - nginx](https://www.nginx.com/resources/wiki/start/topics/examples/forwarded/)

```
proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
proxy_set_header X-Forwarded-Proto $proxy_x_forwarded_proto;
```


# Tomcat

`server.xml` 파일에 다음 설정을 추가해 준다. [Tomcat RemoteIpValve](https://tomcat.apache.org/tomcat-8.0-doc/api/org/apache/catalina/valves/RemoteIpValve.html)

```xml
<Valve className="org.apache.catalina.valves.RemoteIpValve"
  internalProxies="192\.168\.0\.10|192\.168\.0\.11"
  remoteIpHeader="x-forwarded-for"
  proxiesHeader="x-forwarded-by"
  protocolHeader="x-forwarded-proto" />
```

## Tomcat with spring-boot

spring-boot embedded tomcat을 사용하고 있다면 `application.yml` 파일에 아래 내용을 추가하면 된다.

```yaml
server:
  tomcat:
    remote-ip-header: x-forwarded-for
    protocol-header: x-forwarded-proto
    internal-proxies: "10\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}|20\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}|192\\.168\\.\\d{1,3}\\.\\d{1,3}|169\\.254\\.\\d{1,3}\\.\\d{1,3}|127\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}|172\\.1[6-9]{1}\\.\\d{1,3}\\.\\d{1,3}|172\\.2[0-9]{1}\\.\\d{1,3}\\.\\d{1,3}|172\\.3[0-1]{1}\\.\\d{1,3}\\.\\d{1,3}|0:0:0:0:0:0:0:1|::1"
```

위 설정 중 `20\.\d{1,3}\.\d{1,3}\.\d{1,3}` 부분은 내부에서 사용하고 있는 proxy 주소이다. 이 내용은 아래 참고..


## Tomcat and internal proxies config

이 글을 쓰게 된 계기이기도 하고, 그동안 몰랐던 내용인 `internal proxy` 설정이 있다. [Tomcat RemoteIpValve](https://tomcat.apache.org/tomcat-8.0-doc/api/org/apache/catalina/valves/RemoteIpValve.html)의 tomcat 설정을 보면 `internalProxies`는 기본값으로 `10/8`와 `192.168/16`, `169.254/16`, `127/8`, `172.16/12`, `::1`가 설정되어 있다.

Tomcat 에서 `request.getRemoteAddr()`를 호출해서 client의 IP를 얻어올 때 이 범위에 있는 IP는 내부 proxy로 인식해서 `x-forwarded-for` 리스트에서 제거하고 실제 client의 IP를 찾게 된다. 정규식(RegEx)으로 설정해야 하기 때문에 yaml 파일 같은 경우 `\\`를 반복해서 써야 해서 조심(귀찮;;)해야 한다. spring-boot doc에는 모든 proxy를 믿도록 설정하려면 `server.tomcat.internal-proxies`를 빈 값으로 두면 되지만, 실서비스에서는 추천하지 않는다고 한다.

이번에 문제가 됐던 부분은, 지금까지는 내부 proxy의 IP 대역이 `10/8`여서 이 `internalProxies` 기본값에 포함되어 있어 별도 설정 없이 사용해도 `x-forwarded-for`에서 잘 제거되어 넘겨졌었다. 그런데 지금 사용하는 실서비스 서버는 `20/8` 대역이라 서버 IP가 남지 않고 그 다음에 위치한 ELB IP가 남고 있었다.

spring-boot를 사용하고 있다면 `server.tomcat.internal-proxies`를 설정하고, tomcat을 직접 사용한다면 `internalProxies`를 설정해 주자.


# AWS ELB

ELB는 자동으로 `x-forwarded` 헤더를 지원해 준다. [HTTP Headers and Classic Load Balancers](https://docs.aws.amazon.com/elasticloadbalancing/latest/classic/x-forwarded-headers.html)


# 주의

웹서버 까지 들어오는 모든 경로의 proxy에 `x-forwarded` 헤더들을 주가해 줘야 한다. 중간에 하나라도 빠지면 그 위치에서 client 정보를 잃어버리게 된다.

위에서 설명했듯이 tomcat을 사용하는 경우, 호출하는 내부 proxy IP 대역이 `10/8`, `192.168/16`, `169.254/16`, `127/8`, `172.16/12`, `::1`에 포함되지 않는 경우 `internalProxies`를 설정해 주어야 한다.


# 참고

- [Using the Forwarded header - nginx](https://www.nginx.com/resources/wiki/start/topics/examples/forwarded/)
- [nginx Module ngx_http_proxy_module - Embedded Variables](https://nginx.org/en/docs/http/ngx_http_proxy_module.html#variables)
- [Tomcat RemoteIpValve](https://tomcat.apache.org/tomcat-8.0-doc/api/org/apache/catalina/valves/RemoteIpValve.html)
- [HTTP Headers and Classic Load Balancers - AWS ELB](https://docs.aws.amazon.com/elasticloadbalancing/latest/classic/x-forwarded-headers.html)
- [78. Embedded Web Servers - spring-boot docs](https://docs.spring.io/spring-boot/docs/current/reference/html/howto-embedded-web-servers.html)
