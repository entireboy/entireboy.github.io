---
layout: post
title:  "[Spring] advice 순서 변경하기"
date:   2016-04-02 20:25:00 +0900
categories: spring advice
tags: spring aop aspect advice pointcut order sort
---

동일한 곳에 여러 advice가 들어온다면.. 그 중 내가 만든 advice가 가장 먼저/나중에 실행되어야 한다면..??

간단히.. [@Order](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/core/annotation/Order.html) annotation을 사용하자.

일부 코드만 샘플로.. 전체 코드는 [맨 아래 링크](https://github.com/entireboy/blog-sample/tree/master/testSpring)로..
아래에 있는 3개 클래스는 `MyClass.myMethod()`에 걸려 있다. 호출하면 누가 가장 먼저 호출되고 누가 가장 나중에 호출될까??

```java
@Aspect
@Order(1)
public class MyFirstAspect {
   @Around("..............")
   public Object log(ProceedingJoinPoint pjp) throws Throwable {
       log.info("my - Order(1) start");
       Object result = pjp.proceed();
       log.info("my - Order(1) end");
       return result;
   }
}
```

```java
@Aspect
@Order(2)
public class MyAspect {
   @Around("..............")
   public Object log(ProceedingJoinPoint pjp) throws Throwable {
       log.info("my - Order(2) start");
       Object result = pjp.proceed();
       log.info("my - Order(2) end");
       return result;
   }
}
```

```java
@Aspect
public class YourAspect {
   @Around("..............")
   public Object log(ProceedingJoinPoint pjp) throws Throwable {
       log.info("your - not ordered start");
       Object result = pjp.proceed();
       log.info("your - not ordered end");
       return result;
   }
}
```

```java
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = SpringTestContextConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class MyClassTest {
   @Autowired
   private MyClass dut;

   @Test
   public void test() {
       dut.myMethod();
   }
}
```

`@Aspect`에 `@Order`로 값을 지정해 주면 호출순서가 정렬이 된다. 프록시로 감쌀 때 가장 작은 값을 주면 `@Around`를 기준으로 가장 바깥을 감싸게 된다. 가장 먼저 호출되고, 가장 마지막에 호출되는 것이다. 테스트를 돌리면 아래와 같은 결과를 볼 수 있다. `@Order(1)`을 준 `MyFirstAspect`가 가장 바깥 자리를 차지하고,  `@Order`를 주지 않은 `YourAspect`는 가장 안쪽을 차지한다.

```bash
$ cd testSpring
$ ./gradlew --info test

... (생략)...

kr.leocat.test.spring.aop.order.MyClassTest > test STANDARD_ERROR
   [Test worker] INFO kr.leocat.test.spring.aop.order.myfirst.MyFirstAspect - my - Order(1) start
   [Test worker] INFO kr.leocat.test.spring.aop.order.my.MyAspect - my - Order(2) start
   [Test worker] INFO kr.leocat.test.spring.aop.order.your.YourAspect - your - not ordered start
   [Test worker] INFO kr.leocat.test.spring.aop.order.MyClass - 잇힝~
   [Test worker] INFO kr.leocat.test.spring.aop.order.your.YourAspect - your - not ordered end
   [Test worker] INFO kr.leocat.test.spring.aop.order.my.MyAspect - my - Order(2) end
   [Test worker] INFO kr.leocat.test.spring.aop.order.myfirst.MyFirstAspect - my - Order(1) end
```

전체 샘플 코드는 아래에..

[https://github.com/entireboy/blog-sample/tree/master/testSpring](https://github.com/entireboy/blog-sample/tree/master/testSpring)
