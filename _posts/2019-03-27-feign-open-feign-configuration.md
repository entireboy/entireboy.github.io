---
layout: post
title:  "[feign] open feign 설정 시 주의점 (@Configuration)"
date:   2019-03-27 22:18:00 +0900
published: true
categories: [ java ]
tags: [ java, spring, feign, open feign, rest client, http client, http, rest, client, configuration, netflix ]
---

> TL;DR `open feign`의 configuration은 `@Configuration` 을 달아주면 **안 된다**.

`spring-cloud`에 REST client [open feign client](https://cloud.spring.io/spring-cloud-netflix/multi/multi_spring-cloud-feign.html)가 있다. API endpoint 마다 사용하기 위해 여러 feign client를 만드는 경우 서로 다른 설정을 해줘야 해서 `configuration`을 만드는데, 이 때 `configuration`에 `@Configuration`을 달아주면 **안 된다**.

```java
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "foo-api", url = "${ext.foo-api.url}", configuration = FooApiRequestConfiguration.class)
public interface FooApi {
    @PostMapping("/v2/accounts/{accountId}/name")
    String name(
        @PathVariable("accountId") Long accountId,
        @RequestBody MyRequestDto myRequestDto);
}

@FeignClient(name = "bar-api", url = "${ext.bar-api.url}", configuration = BarApiRequestConfiguration.class)
public interface BarApi {
    @GetMapping("/v2/bars/{barId}/name")
    String name(@PathVariable("barId") Long barId);
}
```

위와 같이 `FooApi`와 `BarApi`가 있을 때 서로 다른 설정을 하기 위해 아래처럼 `configuration` 2개를 따로 만들어서 `@FeignClient`에 넣어줬다.

이 샘플은 `Bearer 인증`을 사용하는데, `FooApi`와 `BarApi`가 서로 다른 토큰 값을 가질 때 각자 설정해서 사용하는 샘플이다.

```java
public class FooApiRequestConfiguration {
    @Bean
    public RequestInterceptor fooApiRequestHeader(
        @Value("${ext.foo-api.bearerToken}") String token
    ) {
        return new BearerAuthRequestInterceptor(token);
    }
}

public class BarApiRequestConfiguration {
    @Bean
    public RequestInterceptor barApiRequestHeader(
        @Value("${ext.bar-api.bearerToken}") String token
    ) {
        return new BearerAuthRequestInterceptor(token);
    }
}

import com.google.common.base.Preconditions;
import feign.RequestInterceptor;
import feign.RequestTemplate;

public class BearerAuthRequestInterceptor implements RequestInterceptor {
    private String token;

    public BearerAuthRequestInterceptor(String token) {
        Preconditions.checkNotNull(token, "Token should not be null");
        this.token = token;
    }

    @Override
    public void apply(RequestTemplate template) {
        template.header("Authorization", "Bearer " + token);
    }
}
```

이 때 `configuration` 이니까 (Spring 설정이 아닌데도) 습관처럼 `@Configuration`을 달아주면 의도와는 다르게 동작한다. 여기서의 의도는 `FooApi`에는 `FooApiRequestConfiguration`만 설정되고 `BarApi`에는 `BarApiRequestConfiguration`만 설정되기를 원하는 것이다. 하지만 `@Configuration`을 달아 주게 되면 Spring bean으로 등록돼서 모든 feign client의 설정으로 동작하게 되고, `FooApi`를 사용할 때 `fooApiRequestHeader()`와 `barApiRequestHeader()` 모두 실행되게 된다. `BarApi`도 마찬가지이다.

오늘의 교훈. 습관은 무서운 것. netflix 고마워요.


# 참고

- [Declarative REST Client: Feign](https://cloud.spring.io/spring-cloud-netflix/multi/multi_spring-cloud-feign.html)
- [Feign makes writing java http clients easier](https://github.com/OpenFeign/feign)
