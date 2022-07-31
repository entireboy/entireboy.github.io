---
layout: post
title:  "[Redis] null 값 저장하지 않기 - 의도치 않은 RedisCacheManager 설정 오류"
date:   2022-05-05 22:18:00 +0900
published: true
categories: [ redis ]
tags: [ redis, store, save, null, value, unexpected, exception, cache ]
---

`RedisCacheManager` 선언 시, value로 `null`을 허용하지 않기 위해 `disableCachingNullValues()` 사용하면 강력한 예외를 만나게 된다.

하지만, 캐시 값으로 `null`을 반환하면, 아래 메시지의 `IllegalArgumentException` 을 만난다.

```java
Avoid storing null via '@Cacheable(unless="#result == null")' or configure RedisCache to allow 'null' via RedisCacheConfiguration.
```

[RedisCacheConfiguration#disableCachingNullValues](https://docs.spring.io/spring-data/redis/docs/current/api/org/springframework/data/redis/cache/RedisCacheConfiguration.html#disableCachingNullValues--) 설정에 아래와 같은 주의 문구가 있다.

```
NOTE: any Cache.put(Object, Object) operation involving null value will error. Nothing will be written to Redis, nothing will be removed. An already existing key will still be there afterwards with the very same value as before.

주의: null value를 가지는 Cache.put(Object, Object) 작업은 오류가 발생한다. Redis에는 아무 것도 저장되지 않으며, 지워지는 것도 없다. 이미 존재하는 키는 이전과 아주 동일한 값으로 이후에도 계속 존재한다.
```


`null`을 value로 허용하지 않고 싶은 것은 맞지만, 강력하게 예외도 함께 만날 수 있다. 내 의도가 `@Cacheable` annotation이 달린 메소드가 `null을 반환할 수 있지만 저장하지 않는다` 라면 `disableCachingNullValues()` 를 사용하지 말고, 예외 메시지에 있는 방식으로 result를 확인하도록 처리해야 한다.

```java
@Cacheable(value="defaultCache", key="#pk", unless="#result == null")
public Person findPerson(int pk) {
   return getSession.getPerson(pk);
}
```


# 참고

- [RedisCacheConfiguration#disableCachingNullValues](https://docs.spring.io/spring-data/redis/docs/current/api/org/springframework/data/redis/cache/RedisCacheConfiguration.html#disableCachingNullValues--)
