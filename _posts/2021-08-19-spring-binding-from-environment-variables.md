---
layout: post
title:  "[Spring] 환경변수(Environment) 매직 - 자동 변형/맞춤"
date:   2021-08-19 22:18:00 +0900
published: true
categories: [ spring ]
tags: [ spring, framework, spring boot, environment, variable, relaxed binding, mapping, convert, system ]
---

스프링에서는 조금 더 표현하기 좋고 읽기 좋도록 환경변수를 점(`.`)을 통해 계층화를 할 수 있다.

```
my-service.db.username
```

특히 yaml 파일 등에서 계층화 하기 좋다. 아래의 두 설정은 같은 설정이다.

```
server:
  tomcat:
    threads:
      max: 10

server.tomcat.threads.max: 10
```

하지만 시스템의 환경변수(Environment)의 컨벤션은 보통 대문자를 사용하고, 알파벳과 숫자, 언더스코어(`_`)로 구성된다. 스프링에서 사용하는 표현과 차이가 있기 때문에 Spring Boot에서는 느슨한 바인딩(relaxed binding)을 지원한다. 다음 룰을 통해서 변형(convert)된다.

- 점(`.`)을 언더스코어(`_`)로 변경
- 대시(`-`) 제거
- 대문자로 변경

```
// 이렇게 바뀐다.
my-service.db.username -> MYSERVICE_DB_USERNAME
```

아래와 같이 `my-service.db.username`를 사용하는 코드가 있을 때,

```
@Component
class AdViewController @Autowired constructor(
    @Value("\${my-service.db.username}")
    private val username: String,
) {
  fun printUsername() {
    println(username)
  }
}
```

환경변수로 아래와 같이 설정하고 실행하면, 정상적으로 `username`에 바인딩 되고 출력되는 것을 볼 수 있다.

```
$ MYSERVICE_DB_USERNAME=th.deng
$ java -DMYSERVICE_DB_USERNAME=th.deng -jar my.jar
```

yaml 파일에서도 동일하게 접근할 수 있다.


# 결론

사실.. 이 기능은 편해 보이는데, 써야 하는지는 잘 모르겠다.

오늘 팀에서 `MYSERVICE_DB_USERNAME` 환경변수를 찾았는데, 이게 쓰이는 것인지 아닌 것인지 모르겠어서 한참을 뒤적였다. 결국 꽤나 시간을 보낸 뒤에야 `my-service.db.username` 형태로 사용되는 곳을 찾았다. 동일한 형태의 상수라면 검색이라도 용이한데, 반대로 찾아가려니 죽을 맛이네..


# 참고

- [Relaxed Binding - Spring Boot docs](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config.typesafe-configuration-properties.relaxed-binding)
- [Binding from Environment Variables - Spring Boot docs](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config.typesafe-configuration-properties.relaxed-binding.environment-variables)
