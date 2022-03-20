---
layout: post
title:  "[Spring Cloud Stream] inMemorySwaggerResourcesProvider-out-0 오류"
date:   2022-03-21 22:18:00 +0900
published: true
categories: [ spring ]
tags: [ spring, spring cloud, spring cloud stream, spring cloud function, stream, kafka, kotlin ]
---

에러 메시지도 참 불친절하다. 이 한 줄이 전부이다.

```
[Producer clientId=producer-1] Error while fetching metadata with correlation id 5760 : {inMemorySwaggerResourcesProvider-out-0=UNKNOWN_TOPIC_OR_PARTITION}
```

사실 메시지에 swagger가 적혀 있지만, swagger와는 전혀 무관하고 Spring Cloud Stream의 문제이다.

결론부터 이야기 하면, [Spring Cloud Stream(SCSt)은 최소 한 개 이상의 `java.util.function.[Supplier/Function/Consumer]` 타입의 bean이 필요](https://gitter.im/spring-cloud/spring-cloud-stream?at=5fd0d5b63dd3b251a4f64766)하다.

{% include image.html file='/assets/img/2022/2022-03-21-spring-cloud-stream-no-binding-error.png' alt='It's the way the framework works - it asks Spring for all registered beans of type Function, Consumer or Supplier' %}

Spring Cloud Stream이 버전업 되면서 ([2020.0.0-M2 부터](https://spring.io/blog/2020/07/13/introducing-java-functions-for-spring-cloud-stream-applications-part-0)이긴 한데, 아마 3.0.x쯤 부터??) Spring Cloud Function을 사용해서 쉽고 편하게 functional 형태로 개발할 수 있게 되었다. `java.util.function.[Supplier/Function/Consumer]` 타입의 bean을 만들고 application.yml 설정으로 바인딩 시켜주면 된다. 앞으로 functional 형태로 가려는지 문서도 모두 Spring Cloud Function을 사용한 것으로 바뀌고 있다.

```yaml
spring:
  cloud:
    stream:
      kafka:
        binder:
          autoCreateTopics: false
          autoAddPartitions: false
          autoAlterTopics: false
      bindings:
        myProductChangeEvent-out-0: # producer는 out 으로 표시
          destination: 'my-product-changed'
          content-type: application/json
```

편하다. 이건 인정!!

하지만 yaml 파일과 생성된 bean이 일치하지 않으면?? bean은 있는데 설정이 없거나, bean은 있는데 설정이 없거나.. 참조가 없는 bean이라 지웠더니 yaml 파일에서 발견되면?!?! 오늘 만난 `inMemorySwaggerResourcesProvider-out-0` 문제는 SCSt가 마음대로 `inMemorySwaggerResourcesProvider` bean을 스트림 처리하는 bean으로 인식해 버렸고 yml 설정에 바인딩이 없다고 오류를 뱉은 것이다. 사실 swagger의 `InMemorySwaggerResourcesProvider` 클래스는 운 나쁘게도 `Supplier<List<SwaggerResource>>`를 상속 받고 있어서 잡힌 클래스일 뿐이다. swagger를 제거한다면 다른 `java.util.function.[Supplier/Function/Consumer]` 를 상속 받은 bean 중 무언가가 잡혔을 것이다.

```java
@Component
public class InMemorySwaggerResourcesProvider implements SwaggerResourcesProvider, ApplicationContextAware { ... }

public interface SwaggerResourcesProvider extends Supplier<List<SwaggerResource>> { }
```


# 임시해결

`StreamBridge` 등을 사용하고 있어서 functional 코드로 당장 바꾸지 못 한다면, 임시로 `java.util.function.[Supplier/Function/Consumer]`를 상속 받은 bean 하나를 만들어 주자.

```kotlin
@Configuration
class MyDummyConfig {

    /**
     * Spring Cloud Stream은 최소한 하나 이상의 `java.util.function.[Supplier/Function/Consumer]` 타입의 빈이 존재해야 한다.
     * StreamBridge만을 사용한 publisher만 존재하는 경우, 이런 타입의 빈이 필요 없기 때문에 더미로 하나 추가함.
     * ad-server-api 모듈에서 Spring Cloud Stream을 위해 하나 이상의 `java.util.function.[Supplier/Function/Consumer]` 타입의 빈이 생성되면, 이 빈은 삭제할 수 있다.
     *
     * @see https://gitter.im/spring-cloud/spring-cloud-stream?at=5fd0d5b63dd3b251a4f64766
     */
    @Bean
    fun dummySupplier(): () -> String = { "" }
}
```

다시 얘기하지만 어디까지나 **임시로 사용**하는 것이다. **SCSt는 앞으로 functional 방향으로 가는 것 같으니 얼른 functional 스타일로 바꾸기!!**


# 참고

- [Gitter - spring-cloud/spring-cloud-stream](https://gitter.im/spring-cloud/spring-cloud-stream?at=5fd0d5b63dd3b251a4f64766)
- [Introducing Java Functions for Spring Cloud Stream Applications - Part 0](https://spring.io/blog/2020/07/13/introducing-java-functions-for-spring-cloud-stream-applications-part-0)
