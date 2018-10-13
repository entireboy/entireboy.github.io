---
layout: post
title:  "CORS(Cross Origin Resource Sharing) 초간단 정리 및 링크"
date:   2018-01-27 22:18:00 +0900
published: true
categories: [ web ]
tags: [ web, cors, cross origin, origin, jsonp, http ]
---

# 관련 링크

- [Cross Origin Resource Sharing - CORS](https://homoefficio.github.io/2015/07/21/Cross-Origin-Resource-Sharing/)
- [javascript ajax 크로스도메인 요청 - CORS](https://brunch.co.kr/@adrenalinee31/1)


# Spring CORS sample

[Enabling Cross Origin Requests for a RESTful Web Service](https://spring.io/guides/gs/rest-service-cors/)

간단한 설정으로 API 마다 origin 설정을 할 수 있다.

```java
@CrossOrigin(origins = "http://localhost:9000")
@GetMapping("/greeting")
public Greeting greeting(@RequestParam(required=false, defaultValue="World") String name) {
    System.out.println("==== in greeting ====");
    return new Greeting(counter.incrementAndGet(), String.format(template, name));
}
```

# 결론

이미지나 css 같은 파일은 다른 도메인에서 가져올 수 있으나 script는 보안상의 문제로 single-origin policy로 인해 동일한 도메인이 아니면 실행되지 않는다. 하지만, 지금은 이 정책이 정해질 때의 상황과는 많이 바뀌었기 때문에, 어떤 면에서는 낚은 정책이 되었다. 그동안 꼼수로 쓰던 JSONP는 GET만 호출 가능했지만, 필요가 많아짐에 따라 CORS 추가됐다. (JSONP 이외에 클라이언트 브라우저 플러그인으로 동일한 도메인인듯이 속여서 호출하는 방법을 쓰기도 했다.)

# CORS 요약

- 클라이언트에서 `Access-Control-*` 헤더를 설정해서 호출한다.
- 서버에서는 `Access-Control-*` 헤더를 확인해서 처리하고 응답에 포함한다.
- 서버에서는 `Access-Control-Allow-Origin` 등의 값을 확인하고 보안에 대비(?)한다.
- 서버는 `OPTION` method를 지원하는 편이 좋다(?) CORS의 `simple request` 방식에서는 한 번만 호출해서 응답을 받지만, `preflight request` 방식에서는 `OPTION`을 먼저 보내서 CORS 호출이 가능한지 체크하기 때문이다.
