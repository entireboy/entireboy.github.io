---
layout: post
title:  "[HTTP] X- prefix 헤더 사용은 지양한다"
date:   2023-11-30 22:18:00 +0900
published: true
categories: [ http ]
tags: [ http, header, prefix, rfc, x-forwarded-for, x-forwarded-host, x-forwarded-proto, Forwarded, x-forwarded ]
---

[RFC 6648](https://datatracker.ietf.org/doc/html/rfc6648) 문서에 따르면, HTTP 헤더 중 `X-`로 시작하는 헤더는 테스트나 확장을 뜻 하는 `eXperimental` 또는 `eXtension`의 의미라고 한다.

> Historically, designers and implementers of
> application protocols have often distinguished between standardized
> and unstandardized parameters by prefixing the names of
> unstandardized parameters with the string "X-" or similar constructs
> (e.g., "x."), where the "X" is commonly understood to stand for
> "eXperimental" or "eXtension".

그런데 단점이 커서 사용하지 말자고 2012년에 제안한 문서가 [RFC 6648](https://datatracker.ietf.org/doc/html/rfc6648)이다. `X-` prefix로 이미 잘 쓰고 있는 것을 다시 재정의 해서 바꾸는 비용이 너무 크기 때문에 잘 정의해서 쓰자고 이야기 하고 있고, 그 방법을 정리해 두었다.

대표적으로 [X-Forwarded-For](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/X-Forwarded-For) (XFF), [X-Forwarded-Host](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/X-Forwarded-Host) (XFH), [X-Forwarded-Proto](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/X-Forwarded-Proto) (XFP) 같은 헤더들이 있다. 프록시 같은 서버를 통할 때 원래 요청한 시작 위치(origin)의 IP 등을 확인하기 위해 정보를 포함해서 전달하는 헤더이다. 이 헤더들은 너무 긴 시간 사용했고 많은 곳에서 이것을 표준으로 사용하고 있기 때문에 이 헤더들은 사실상 표준(de-facto standard)이지만, [Forwarded](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Forwarded)헤더가 이 정보들을 담기 위해 정해진 진짜(?) 표준이다. 진짜 표준이 정해졌지만 이미 사용하는 곳이 많아서 쉽사리 제거하기도 어렵고, 널리 알리기도 어렵다. 알린다고 한들 잘 쓰고 있는 것을 바꿀 이유도 없다.

(하지만.. 개인적으로는) 표준으로 들어갈 헤더가 아니고 회사나 개인이 쓸 것이라면 상관 없을 것 같다. 다른 이름으로 헤더가 바뀌지 않을테니 처리 비용이 늘지도 않을 것이고..


# 참고

- [HTTP 헤더 - MDN](https://developer.mozilla.org/ko/docs/Web/HTTP/Headers)
- [RFC 6648](https://datatracker.ietf.org/doc/html/rfc6648)
- [Forwarded](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Forwarded)
- [X-Forwarded-For (XFF) 헤더 - MDN](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/X-Forwarded-For)
- [X-Forwarded-Host (XFH) 헤더 - MDN](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/X-Forwarded-Host)
- [X-Forwarded-Proto (XFP) 헤더 - MDN](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/X-Forwarded-Proto)
