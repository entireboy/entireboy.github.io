---
layout: post
title:  "[RabbitMQ] Publisher Confirm - 메시지 전송도 안 잃어버리기"
date:   2018-06-23 22:18:00 +0900
published: true
categories: [ rabbitmq ]
tags: [ rabbitmq, publisher, confirm, message, ack, queue, message queue ]
---

RabbitMQ에서 메시지를 꺼내서 처리할 때만 ACK를 보내는게 아니라, 정상적으로 보냈는지 확인할 ACK도 받고 싶을 때 Publisher Confirm을 사용하면 된다. publisher confirm과 transaction은 함께 사용할 수 없다.

아래처럼 `confirm.select`를 사용하도록 설정하고, 메시지를 보내면 callback을 받을 수 있다. (전체 샘플은 [요기](https://github.com/entireboy/blog-sample/tree/master/rabbitmq/src/main/java/kr/leocat/test/sample/rabbitmq/publisherconfirm)에 추가해 두었다.)

```java
channel.confirmSelect();
channel.addConfirmListener(new ConfirmListener() {
  @Override
  public void handleAck(long deliveryTag, boolean multiple) throws IOException {
    System.out.println("ACK: " + deliveryTag);
  }

  @Override
  public void handleNack(long deliveryTag, boolean multiple) throws IOException {
    System.out.println("NACK: " + deliveryTag);
  }
});
```

spring-amqp를 사용할 때는 아래처럼 하면 된다.

```java
@Bean
public ConnectionFactory rabbitConnectionFactory() {
  CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost", 5672);
  // .. 이러저런 설정
  // publisher confirm ON
  connectionFactory.setPublisherConfirms(true);
  return connectionFactory;
}

@Bean
public RabbitTemplate rabbitTemplate(ConnectionFactory rabbitConnectionFactory) {
  RabbitTemplate template = new RabbitTemplate(rabbitConnectionFactory);
  template.setConfirmCallback((correlationData, ack, cause) -> {
    if(ack) {
      System.out.println("ACK");
    } else {
      System.out.println("NACK: " + cause);
    }
  });
  return template;
}
```


# 참조

- [Publisher Confirms](https://www.rabbitmq.com/confirms.html#publisher-confirms)
- [Introducing Publisher Confirms](https://www.rabbitmq.com/blog/2011/02/10/introducing-publisher-confirms/)
- [Publisher Confirms and Returns - Spring AMQP](https://docs.spring.io/spring-amqp/reference/htmlsingle/#template-confirms)
