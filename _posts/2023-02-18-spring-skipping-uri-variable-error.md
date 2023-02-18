---
layout: post
title:  "[Spring] Skipping URI variable 오류 메시지"
date:   2023-02-18 22:18:00 +0900
published: true
categories: [ spring ]
tags: [ spring, request, uri, variable, ignore, skip, binding ]
---

# 에러 메시지

```
WARN  00:29:44.682 [http-nio-7070-exec-1] o.s.validation.DataBinder - Skipping URI variable 'userId' because request contains bind value with same name.]
```


# 원인

원인은 path variable에 설정한 이름과 request param으로 받은 이름이 동일할 때이다. 별도의 클래스로 받게 된다면 파일 위치가 달라서 눈에 잘 안 띈다.

코드를 살펴보면..

```java
public class UserController {
    @GetMapping("/user/{userId}")
    public String user(
        User user
    ) {
        return "UserId: " + user.userId;
    }
}

class User {
    Long userId;
}
```

보통 User 클래스 같은 request, response 내용은 Controller와는 다른 파일에 생성하게 된다. 따라서 path variable의 userId가 User 클래스의 변수 userId와 겹치는지 쉽게 눈에 띄지 않는다.


# 테스트

이렇게 되면 어떻게 되는가? 아래처럼 호출하면 `user.userId`값은 222가 된다. 로그 내용 그대로 URI variable 'userId'는 무시되고 request param의 값인 222가 사용된다.

```bash
$ curl "http://127.0.0.1/user/111?userId=222"
UserId: 222
```

# 해결

1. 같은 이름을 사용하지 않도록 변경한다.
2. `@PathVariable` 등으로 명시적으로 설정한다.

```java
@GetMapping("/user/{userId}")
public String user(
    @PathVariable("userId") Long userId,
    User user
) {
    return "UserId: " + userId + " - " + user.userId;
}

class User {
    Long userId;
}
```

# 번외

이렇게 사용할 생각은 없지만, URI 쪽이 무시된다면 변수에 저장해 주려고 했다는건가? 이렇게 호출해 보았다. request param 빼고 path variable만 주고..

```bash
$ curl "http://127.0.0.1/user/111"
UserId: 111
```

아? 진짜 이게 되네?
