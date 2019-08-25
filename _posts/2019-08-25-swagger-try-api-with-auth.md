---
layout: post
title:  "[Swagger] 인증과 함께 API호출 테스트 (Basic auth 등)"
date:   2019-08-25 22:18:00 +0900
published: true
categories: [ swagger ]
tags: [ swagger, try, call, http, api, auth, authentication, basic authentication, basic auth, bearer authentication, bearer auth ]
---

# HTTP 호출할 때 header에 인증 정보 넣기

HTTP 요청에 인증을 사용하는 경우 `Authentication` 헤더로 인증 타입과 인증 값을 함께 주면 된다.

```
Authentication: <type> <credentials>

e.g.
Authentication: Basic aGVsbG86d29ybGQ=
Authentication: Bearer aGPeFO23FoF09xM8fN75DjqNz1
```


# Swagger로 API 호출 테스트 시 인증 정보

swagger로 API 호출 테스트를 할 때도 이 인증 정보를 함께 보내야 한다. swagger 문서에 따라 내 API에서 사용하는 인증을 설정한다. [Basic Auth 설정](https://swagger.io/docs/specification/authentication/basic-authentication/), [Bearer Auth 설정](https://swagger.io/docs/specification/authentication/bearer-authentication/)

아래 이미지처럼 자물쇠를 누르고 인증을 설정한다. (여기서는 Basic auth를 사용한다. 다른 인증을 사용한다면 화면이 달라질 수 있다. Bearer 인증 같으면 username/password가 아닌 token 정보를 입력하게 될 것이다.)

{% include image.html file='/assets/img/2019-08-25-swagger-try-api-with-auth1.png' alt='Add Basic Auth' %}

위와 같이 Basic auth의 로그인 정보를 넣고 `Authorize`버튼을 누르면 아래처럼 설정된 정보를 확인할 수 있다.

{% include image.html file='/assets/img/2019-08-25-swagger-try-api-with-auth2.png' alt='Add Basic Auth' %}

이제 API를 테스트로 호출해 보면 `curl` 부분에 아래처럼 `-H`로 헤더가 설정된 것을 볼 수 있다.

```bash
curl -X GET "http://127.0.0.1:8080/v1/my/name"
  -H "accept: application/json;charset=UTF-8"
  -H "authorization: Basic aGVsbG86d29ybGQ="
```


# 참고

- [Add an authorization header to your swagger-ui with Swashbuckle (revisited)](https://mattfrear.com/2018/07/21/add-an-authorization-header-to-your-swagger-ui-with-swashbuckle-revisited/)
