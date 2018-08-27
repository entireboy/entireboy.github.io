---
layout: post
title:  "[Lombok] @Builder와 @NoArgsConstructor 함께 쓰기"
date:   2018-06-26 23:18:00 +0900
published: true
categories: [ java ]
tags: [ java, logging, gc, log ]
---

아래처름 `@Builder`와 `@NoArgsConstructor`를 함께 사용하면, 컴파일 시 에러가 발생한다.
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





// @Getter @Setter 등등
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyName {
    private String first;
    private String last;
}
1

# 참고

- [Understanding the Java Garbage Collection Log](https://dzone.com/articles/understanding-garbage-collection-log)
- [How to Enable Garbage Collection (GC) Logging](https://confluence.atlassian.com/confkb/how-to-enable-garbage-collection-gc-logging-300813751.html)
- [GCeasy](http://gceasy.io/)
