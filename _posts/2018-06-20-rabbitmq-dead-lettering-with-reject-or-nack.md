---
layout: post
title:  "[RabbitMQ] nack/reject로 다른 큐로 보내기 (dead-letter)"
date:   2018-06-20 23:18:00 +0900
published: true
categories: [ rabbitmq ]
tags: [ rabbitmq, dead-letter, reject, nack, ack, routing, route, message, confirm ]
---

RabbitMQ로 메시지를 처리하다 문제가 발생하면, 다음 메시지를 처리하기 위해 이 메시지를 건너뛰어야 하는 경우가 있다. 이 메시지를 건너뛰어야 꾸준히 들어오는 다음 메시지를 처리할 수 있기 때문이다. 일시적으로 다른 API 호출이나 연결 문제가 있는 메시지는 나중에 재시도 하면 정상처리 될 수 있기 때문에 문제가 되는 메시지는 잠시 다른 곳에 보관한다.

queue에 dead letter 설정을 해두고, nack나 reject를 보내면 된다. 이 때, requeue는 false로 설정해서 보내야 한다. (requeue를 true로 하면 dead letter로 가지 않고 바로 메시지가 있던 원래 queue로 돌아간다.)

참고로 RabbitMQ에서 dead letter로 처리되는 경우는 다음과 같은 조건이고, 여기서 소개하는 방법은 nack/reject를 사용 하는 것이다.:

- `requeue=false`로 `basic.reject`나 `basic.nack` 처리되는 경우
- queue에서 메시지 TTL이 다 된 경우 (`expire`)
- queue가 가득차서 넘치는 경우 (`x-max-length`)

샘플로 보자. 전체 샘플은 [샘플로 만들어둔 코드](https://github.com/entireboy/blog-sample/tree/master/rabbitmq/src/main/java/kr/leocat/test/sample/rabbitmq/deadletter)에서 확인할 수 있다. 전체 샘플에 spring-amqp를 사용한 샘플도 있다. (큰 차이가 없어서 생략)

```java
Map props1 = new HashMap();
props1.put("x-dead-letter-exchange", "x1");
channel.queueDeclare("q1", false, false, false, props1);
```

위처럼 queue에 dead letter 설정을 해준다. 그리고 consumer에서 `nack`나 `reject`를 보내면 `x-dead-letter-exchange`에 설정된 exchange로 보내지는 것을 확인할 수 있다.

```java
DefaultConsumer consumer = new DefaultConsumer(channel) {
  @Override
  public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
    String message = new String(body, "UTF-8");
    System.out.println(" [x] Received '" + message + "'");

    // requeue=false
    channel.basicNack(envelope.getDeliveryTag(), true, false);
    // channel.basicReject(envelope.getDeliveryTag(), false);
    System.out.println(" Message is rejected: " + message);
  }
};
```


# 주의점


## auto ack는 false로 설정

`auto ack`가 `true`로 설정되어 있으면, 메시지를 가져오면 자동으로 `ack`를 보내서 RabbitMQ 서버에서 메시지가 사라지게 된다. 메시지를 가져와서 처리가 끝날 때 수동으로 `ack`를 보낼 수 있도록 `auto ack`를 `false`로 설정하자. 메시지를 가져와서 `ack`를 보내기 전 까지는 이 메시지를 다른 컨슈머에게 주지도 않고, 큐에서 제거하지도 않는다. 정상적으로 처리가 되지 않을 때, `ack` 대신 `nack`를 보내면 된다.

여기서 `auto ack`는 spring-amqp의 `acknowledgeMode`와는 다른 개념이다. (<https://docs.spring.io/spring-amqp/reference/htmlsingle/#containerAttributes>)


## requeue=false로 설정

`nack`나 `reject`를 보낼 때 `requeue=false`로 설정하지 않으면, 이 메시지는 큐의 원래 위치로 돌아가게 된다. 그러면 이 컨슈머나 혹은 다른 컨슈머가 그걸 다시 가져와서 처리하게 된다. 이렇게 다른 컨슈머가 바로 처리해도 되는 비즈니스가 맞는 곳이라면 dead letter를 설정하지 말고 그대로 requeue 하면 된다.

다른 컨슈머에게 패스하기([Negative Acknowledgement and Requeuing of Deliveries](https://www.rabbitmq.com/confirms.html#consumer-nacks-requeue)) 정도의 처리가 될 것 같다. :D

RabbitMQ에서 requeue는 가능한 메시지의 원래 위치에 넣게 된다. 만일 이미 많은 메시지가 빠져 나가서 원래 위치를 찾기 어려운 경우는 queue의 head에 가장 가까운 위치에 넣는다. ([nack](https://www.rabbitmq.com/nack.html))


# 참고

- [Dead Letter Exchanges](https://www.rabbitmq.com/dlx.html)
- [Negative Acknowledgements](https://www.rabbitmq.com/nack.html)
- [Consumer Acknowledgements and Publisher Confirms](https://www.rabbitmq.com/confirms.html)
- [Spring AMQP](https://docs.spring.io/spring-amqp/reference/htmlsingle)
