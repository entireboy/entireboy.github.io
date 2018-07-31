---
layout: post
title:  "[RabbitMQ] 간단 delayed queue 설정"
date:   2018-07-31 23:18:00 +0900
published: true
categories: [ rabbitmq ]
tags: [ rabbitmq, delayed queue, delay, ttl, dead-letter, routing, route, message, expire ]
---

간혹 큐로 메시지를 주고 받을 때, 메시지를 보낸 즉시가 아닌 일정 시간(delay) 이후에 받고 싶은 경우가 있다. RabbitMQ에서는 몇 가지 방법으로 구현할 수 있다.


# Using RabbitMQ Delayed Message Plugin

RabbitMQ [community-plugin](http://www.rabbitmq.com/community-plugins.html) 중에 `rabbitmq_delayed_message_exchange`라는 플러그인이 있다. 플러그인을 enable하고 github 사이트에 안내하는 방식으로 `x-delayed-type` 설정을 주고 exchange를 생성하면 간편하게 사용할 수 있다.


# Using Dead-letter Exchange config

플러그인을 설정할 수 없는 상황이거나, 손으로 직접 컨트롤 하고 싶은 경우는 별도의 큐를 생성해서 만들 수 있다. 아래 설정을 가진 queue를 생성한다.

- `x-dead-letter-exchange`: delay 이후 메시지가 전송될 exchange 이름
- `x-dead-letter-routing-key`: exchange에서 사용될 라우팅 키 (fanout exchange에 전송하는 경우 필요 없을 수도 있다.)
- `x-message-ttl`: delay 시간(ms)

아래 코드는 메시지를 `q2`에 전송하면 5초 후에 `x2`라는 exchange로 메시지를 보내게 되는 큐를 생성한 것이다. `x2`가 fanout exchange라서 라우팅 키 설정은 하지 않았다. ([샘플 코드](https://github.com/entireboy/blog-sample/tree/master/rabbitmq/src/main/java/kr/leocat/test/sample/rabbitmq/deadletter)에 RabbitMQ client와 spring-amqp를 사용할 때의 간단 예제를 만들어 뒀다.)

```java
@Bean
public Queue q2() {
    Map arguments = new HashMap();
    arguments.put("x-dead-letter-exchange", "x2");
    arguments.put("x-message-ttl", 5000L);
    return new Queue("q2", false, false, false, arguments);
}
```

전체적으로는 메시지를 보내고 5초 뒤에 `q1`에 메시지가 전송되게 하고 싶은 경우, `q2`에 전송하면 `x2`로 전송되고 `x2`에서 `q1`로 보내지는 형태이다.

```
q2 -(x-dead-letter-exchange)-> x2 -> q1
```


## Message TTL

직접 `x-dead-letter-exchange`와 `x-message-ttl` 설정으로 지연 큐를 생성했을 때, 큐에 설정된 TTL 보다 더 빠른 TTL로 메시지를 보내고 싶다면 메시지를 발송할 때 `expiration` 설정을 주면 된다.

```java
byte[] messageBodyBytes = "Hello, world!".getBytes();
AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                                   .expiration("1000")
                                   .build();
channel.basicPublish("my-exchange", "routing-key", properties, messageBodyBytes);
```

큐 TTL과 메시지 TTL이 모두 설정되어 있는 경우는, 더 작은 값이 적용된다.

> When both a per-queue and a per-message TTL are specified, the lower value between the two will be chosen.
>
> \- from http://www.rabbitmq.com/ttl.html


# 참고

- [Dead Letter Exchanges](https://www.rabbitmq.com/dlx.html)
- [Negative Acknowledgements](https://www.rabbitmq.com/nack.html)
- [Consumer Acknowledgements and Publisher Confirms](https://www.rabbitmq.com/confirms.html)
- [Spring AMQP](https://docs.spring.io/spring-amqp/reference/htmlsingle)
- [RabbitMQ TTL](http://www.rabbitmq.com/ttl.html)
