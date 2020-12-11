---
layout: post
title:  "[Spring Cloud] Gateway 샘플"
date:   2020-12-12 22:18:00 +0900
published: true
categories: [ spring ]
tags: [ spring, spring cloud, gateway, sample, actuator, endpoint, kotlin ]
---

Gateway 가 필요할 때 간단한 설정으로 사용할 수 있는 [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway)가 있다. Public 망과 private 망을 서로 연결해 주는 역할을 하거나, 일부 host의 요청만 거르거나, 일부 path의 호출만 허용하는 등의 필터링 역할도 할 수 있다. 그리고 서킷 브레이커와 통합도 쉽고, [트래픽 제한](https://github.com/spring-cloud/spring-cloud-gateway/blob/master/spring-cloud-gateway-sample/src/main/java/org/springframework/cloud/gateway/sample/ThrottleGatewayFilter.java) 등의 역할도 가능하다. Spring WebFlux 위에서 동작하기 때문에 성능도 상당히 좋다.


# 샘플

설정은 아래처럼 정말 간단하다. 이 샘플은 Kotlin으로 작성되어 있고, Kotlin을 사용한다면 DSL 스타일로 쓸 수 있기 때문에 눈에도 잘 들어오고 편하다.

```kotlin
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.cloud.gateway.route.builder.filters
import org.springframework.cloud.gateway.route.builder.routes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpMethod.GET

/**
 * MyApi routing 설정
 */
@Configuration
internal class MyApiRoutingConfiguration constructor(
    /**
     * 포워딩할 서버
     */
    @Value("\${my-api.url}")
    private val myApiUrl: String,

    /**
     * 포워딩할 서버의 인증 정보 `username`
     */
    @Value("\${my-api.username}")
    private val myApiUsername: String,

    /**
     * 포워딩할 서버의 인증 정보 `password`
     */
    @Value("\${my-api.password}")
    private val myApiPassword: String,
) {

    @Bean
    fun myApiRouteLocator(builder: RouteLocatorBuilder) = builder.routes {
        // /v1/users 로의 routing 설정
        route(id = "my-api-user") {
            method(GET) and // `and`가 꼭 필요하다.
//              host("") and // 호출 받을 `host` 설정도 가능하다.
                path("$MY_API_PREFIX/v1/users") // 정규식 패턴을 사용해도 된다.
            filters {
                // path에 gateway 에서 사용할 prefix가 있으면 잘라낼 단위
                stripPrefix(PREFIX_SIZE)
                // 포워딩할 서버에서 인증이 필요한 경우 인증을 추가할 수 있다.
                addRequestHeader(AUTHORIZATION, (myApiUsername to myApiPassword).toBasicAuthHeaderValue())
            }
            uri(adServerApiUrl)
        }
        route(id = "my-api-animal") { /* ... */ }
        route(id = "my-api-building") { /* ... */ }
    }

    companion object {
        private const val MY_API_PREFIX = "/api/my-api"
        private const val PREFIX_SIZE = 2
    }
}

// 아래 extension은 다른 파일에 선언해 두고 공통으로 사용하면 된다.
/**
 * [Pair]를 Basic auth 에 사용될 인증 정보 header 값으로 인코딩한다.
 * - `Pair#first`: username
 * - `Pair#second`: password
 */
internal fun Pair<String, String>.toBasicAuthHeaderValue(): String =
    """Basic ${String(Base64.getEncoder().encode("$first:$second".toByteArray(UTF_8)), UTF_8)}"""
```

위의 gateway 설정은 아래처럼 포워딩 하는 설정이다. `{my-api}` 는 설정에서 읽어온 `my-api.url` 값이다.

`method`, `host`, `path` 등으로 gateway 에서 호출 받을 url을 지정한다.

`filters` 로 포워딩해 줄 서버를 호출하는데 필요한 정보를 설정한다. `stripPrefix` 는 아래처럼 호출된 path 에서 몇 개의 prefix를 제거할 것인지 지정하는 것이다. 샘플에서는 `2`를 지정해서 앞부분의 2개(`/api/my-api`)부분을 제거했다. 헤더에 추가로 값을 설정하고 싶은 경우에는 `addRequestHeader` 를 사용하면 된다, 샘플에서는 Basic auth 인증을 추가했다.

```
GET http://localhost:8080/api/my-api/v1/users

->

GET http://{my-api}/v1/users
Authorization: Basic aaabbbcccddd
```

{% include google-ad-content %}


# 설정된 Route 확인하기

내가 원하는 형태로 route가 설정되었는지 확인이 필요하다. 설정이 잘못된 경우 오류 메시지도 없기 때문에 설정을 잘못한건지 테스트 호출을 잘못 한건지 정말 답답하다. 이럴 때 `gateway` actuator를 사용하면 확인이 수월하다.

```yaml
management:
  endpoints:
    web:
      exposure:
        include: gateway
  endpoint:
    gateway:
      enabled: true # default: true
```

`application.yml` 파일에 위와 같이 `gateway` 설정을 추가하면 actuator 에서 route 설정을 확인할 수 있다. 운영 환경에서는 노출되면 안 되기 때문에 actuator는 비운영 환경에서만 설정해 두도록 한다. `management.endpoint.gateway.enabled` 설정은 default 값이 `true` 이기 때문에 설정할 필요는 없지만, 운영환경 등 노출을 하고 싶지 않은 환경에서는 `false` 로 설정이 가능하다. Route를 마음대로 추가/삭제할 수 있기 때문에 테스트 환경에서만 쓰는 것을 강력하게 추천한다!!

```bash
http://localhost:8080/actuator/gateway/routes
```

위의 주소로 접속하면, 아래처럼 설정된 모든 route를 확인할 수 있다.

```bash
[
  {
    "predicate": "(Methods: [GET] && Paths: [/api/my-api/v1/users], match trailing slash: true)",
    "route_id": "my-api-user",
    "filters": [
      "[[StripPrefix parts = 2], order = 0]",
      "[[AddRequestHeader Authorization = 'Basic aaabbbcccddd'], order = 0]"
    ],
    "uri": "http://{my-api}",
    "order": 0
  },
  {
    "predicate": "(Hosts: [kotlin.abc.org] && Paths: [/image/png], match trailing slash: true)",
    "route_id": "test-kotlin",
    "filters": [
      "[[PrefixPath prefix = '/httpbin'], order = 0]",
      "[[AddResponseHeader X-TestHeader = 'foobar'], order = 0]"
    ],
    "uri": "http://localhost:8080",
    "order": 0
  }
]
```

Actuator를 통해서 route 를 추가하거나 변경할 수도 있다. (위험해 위험해..) 아래 목록은 `gateway` actuator 설정에서 오픈되는 endpoint이다.

- GET /actuator/gateway/globalfilters: global filter 리스트
- GET /actuator/gateway/routefilters: 특정 route에 설정된 gateway filter 리스트
- POST /actuator/gateway/refresh: route 캐시를 비운다
- GET /actuator/gateway/routes: 전체 route 설정 정보
- GET /actuator/gateway/routes/{id}: 특정 route 설정 정보
- POST /actuator/gateway/routes/{id}: Gateway에 새 route 추가
- DELETE /actuator/gateway/routes/{id}: Gateway에서 route 제거


# 참고

- [Actuator API - Spring Cloud Gateway Docs](https://cloud.spring.io/spring-cloud-gateway/reference/html/#actuator-api)
- [spring-gatewa-sample](https://github.com/spring-cloud/spring-cloud-gateway/tree/master/spring-cloud-gateway-sample)
- [Kotlin 샘플](https://github.com/spring-cloud/spring-cloud-gateway/blob/master/spring-cloud-gateway-sample/src/main/kotlin/org/springframework/cloud/gateway/sample/AdditionalRoutes.kt)
- [Java 샘플](https://github.com/spring-cloud/spring-cloud-gateway/blob/master/spring-cloud-gateway-sample/src/main/java/org/springframework/cloud/gateway/sample/GatewaySampleApplication.java)
