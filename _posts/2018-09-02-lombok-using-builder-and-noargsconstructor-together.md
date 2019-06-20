---
layout: post
title:  "[Lombok] @Builder와 @NoArgsConstructor 함께 사용하기"
date:   2018-09-02 23:18:00 +0900
published: true
categories: [ java ]
tags: [ java, lombok, annotation, Builder, constructor, NoArgsConstructor, AllArgsConstructor ]
---

> **TL;DR**: `@Builder`와 `@NoArgsConstructor`를 함께 사용하려면, `@AllArgsConstructor`도 함께 사용하거나 모든 필드를 가지는 생성자를 직접 만들어 줘야 한다. `@Builder`를 사용할 때 `@NoArgsConstructor`뿐만 아니라 손수 만든 다른 생성자가 있다면, 그 때도 모든 필드를 가지는 생성자가 필요하다.


# 문제

아래처럼 `@Builder`와 `@NoArgsConstructor`를 함께 사용하면, 컴파일 시 에러가 발생한다.

```java
// @Getter @Setter @EqualsAndHashCode 등등
@Builder
@NoArgsConstructor
public class MyName {
    private String first;
    private String last;
}
```

```
Error:(7, 1) java: constructor MyName in class kr.leocat.test.MyName cannot be applied to given types;
  required: no arguments
  found: java.lang.String,java.lang.String
  reason: actual and formal argument lists differ in length
```

그리고 `@NoArgsConstructor`가 아니더라도, 아래처럼 일부 필드만을 가지는 생성자를 가지는 경우에도 컴파일이 되지 않는다.

```java
// @Getter @Setter @EqualsAndHashCode 등등
@Builder
public class MyName {
    private String first;
    private String last;

    public MyName() { ... }
}

  .. 또는 ..

// @Getter @Setter @EqualsAndHashCode 등등
@Builder
public class MyName {
    private String first;
    private String last;

    public MyName(String last) { ... }
}
```


# 해결방법

`@AllArgsConstructor`도 함께 달아주거나 모든 필드를 가지는 생성자를 손수 만들어 주면 된다.

```java
// @Getter @Setter 등등
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyName {
    private String first;
    private String last;
}

  .. 또는 ..

// @Getter @Setter 등등
@Builder
@NoArgsConstructor
public class MyName {
    private String first;
    private String last;

    MyName(String first, String last) { ... }
}
```


# 참고

- [Lombok @Builder and JPA Default constructor - stackoverflow](https://stackoverflow.com/questions/34241718/lombok-builder-and-jpa-default-constructor/35602246#35602246) - 채택된 답변 보다 아래 답변이 더 나이쓰함!!
- [@Builder - lombok](https://projectlombok.org/features/Builder)
- [@NoArgsConstructor, @AllArgsConstructor - lombok](https://projectlombok.org/features/constructor)
