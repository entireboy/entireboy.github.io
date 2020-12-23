---
layout: post
title:  "[Netty] URL 길이 제한 문제 (maxInitialLineLength)"
date:   2020-12-23 22:18:00 +0900
published: true
categories: [ netty ]
tags: [ netty, http, request, url, length, 413, status, Payload Too Large, Request Entity Too Large, too large, decode, reactor, play ]
---

엄청나게 길고 많은 request parameter를 가진 GET 요청을 보냈는데, [413 Payload Too Large](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/413) 응답을 받았다. 이 413 응답이 nginx에서 뱉은거라면 [[nginx] 413 Request Entity Too Large 오류]({{ site.baseurl }}{% post_url 2020-04-21-nginx-413-request-entity-too-large %})를 참고하면 되지만, nginx가 아닌 웹서버에서 내는거라면 문제는 다르다. (응답 화면으로 알아볼 수 있다. 에러 페이지를 바꾸지 않았다면, nginx는 nginx 특유의 에러 페이지가 뙇!!)

Reactor나 Play처럼 내부적으로 Netty를 사용하는 경우라면 HttpRequestDecoder를 사용할 것이고, 여기에 HTTP request 요청의 첫 라인의 길이 제한이 있다. `maxInitialLineLength` 값으로 설정되며, [기본값은 4k(4096)](https://netty.io/4.0/api/io/netty/handler/codec/http/HttpRequestDecoder.html#HttpRequestDecoder--)이다. 이 값은 `GET / HTTP/1.0`과 같이 HTTP request의 가장 첫 줄의 최대 길이이다.

> - maxInitialLineLength: The maximum length of the initial line (e.g. "GET / HTTP/1.0") If the length of the initial line exceeds this value, a TooLongFrameException will be raised.
> - maxHeaderSize: The maximum length of all headers. If the sum of the length of each header exceeds this value, a TooLongFrameException will be raised.
> - maxChunkSize: The maximum length of the content or each chunk. If the content length exceeds this value, the transfer encoding of the decoded request will be converted to 'chunked' and the content will be split into multiple HttpContents. If the transfer encoding of the HTTP request is 'chunked' already, each chunk will be split into smaller chunks if the length of the chunk exceeds this value. If you prefer not to handle HttpContents in your handler, insert HttpObjectAggregator after this decoder in the ChannelPipeline.

아래 샘플이 HTTP request 인데, 첫줄에 request method와 path, protocol version을 적어주게 된다. ([Requests](https://developer.mozilla.org/en-US/docs/Web/HTTP/Overview#Requests) - An overview of HTTP) `GET`으로 호출하다 보니 request parameter 때문에 이 길이가 4k를 넘어서 413 응답을 내려준 것이다.

```bash
# GET 요청
GET http://my.host/api/v1/users?name=스뎅 HTTP/1.1
Authorization: Basic ThisIsEncodedIdAndPassWord
Content-Type: application/json

# POST 요청
POST http://my.host/api/v1/users HTTP/1.1
Authorization: Basic ThisIsEncodedIdAndPassWord
Content-Type: application/json

{
    "name": "스뎅",
    "age": 17
}
```

설정을 살짝 바꿔주면 된다. (WebFlux를 사용하는 Spring 코드)

```java
@Component
public class WebConfig implements WebServerFactoryCustomizer<NettyReactiveWebServerFactory> {
    @Override
    public void customize(NettyReactiveWebServerFactory serverFactory) {
        serverFactory.addServerCustomizers(httpServer -> httpServer.httpRequestDecoder(
                // HTTP request 첫 줄이 4096 넘어가면 413 오류 발생
                // https://netty.io/4.0/api/io/netty/handler/codec/http/HttpRequestDecoder.html
                httpRequestDecoderSpec -> httpRequestDecoderSpec.maxInitialLineLength(40960)
        ));
    }
}
```


# 참고

- [Requests - MDN An overview of HTTP](https://developer.mozilla.org/en-US/docs/Web/HTTP/Overview#Requests)
- [Class HttpDecoderSpec - Reactor JavaDoc](https://projectreactor.io/docs/netty/release/api/reactor/netty/http/HttpDecoderSpec.html)
- [Class HttpRequestDecoder - Netty JavaDoc](https://netty.io/4.0/api/io/netty/handler/codec/http/HttpRequestDecoder.html)
- [413 Payload Too Large - MDN](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/413)
