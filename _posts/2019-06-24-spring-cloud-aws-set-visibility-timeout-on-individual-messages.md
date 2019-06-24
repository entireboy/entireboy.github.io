---
layout: post
title:  "[spring-cloud-aws] 메시지 단위로 VisibilityTimeout 변경하기"
date:   2019-06-24 22:18:00 +0900
published: true
categories: [ spring ]
tags: [ spring, spring-cloud, spring-cloud-aws, aws, sqs, set, visibility timeout, visibility, timeout, individual, message ]
---

# Visibility timeout

AWS SQS에는 메시지를 꺼내오고 명시적으로 지우지 않으면 (ack를 보내지 않으면) 일정 시간(`VisibilityTimeout`) 뒤에 메시지가 다시 나온다. Queue 설정에서 `Default Visibility Timeout`을 설정해 주면 된다.

이걸 이용해서 메시지를 정상적으로 처리하지 못 한 경우 일정 시간 후에 재처리를 하도록 설정할 수도 있다. 하지만 간혹 특정 예외의 경우는 해당 메시지만 큐에서 늦게 꺼내고 싶을 때가 있다. 예를 들어, SQS에서 메시지를 꺼내 외부 연동도 하고 다른 일들을 하는 모듈이 있는데, 외부 연동이 실패하는 경우에만 SQS에서 조금 더 나중에 꺼내서 재시도 하고 싶을 수 있다. 특성상 한 번 실패하면 바로 재시도 하면 또 실패할 확률이 높은 외부 연동이라면 한참 뒤에 재시도를 하고 싶을 수 있다.

이럴 때는 메시지 마다 `VisibilityTimeout`을 바꿔주면 된다.


# Setting Visibility Timeout with spring-cloud-aws

메시지를 처리하다 실패했을 때, 해당 메시지의 `VisibilityTimeout`만 바꾸고 싶으면 `Visibility`를 사용하면 된다. (`org.springframework.cloud.aws.messaging.listener.Visibility#extend(int seconds)`)

```java
@SqsListener(value = "leocat-queue", deletionPolicy = SqsMessageDeletionPolicy.NEVER)
public void handleBizmoneyBillingEvent(@Payload OrderEvent event,
                                       MessageHeaders headers,
                                       Acknowledgment ack,
                                       Visibility visibility) {

    try {
        doSomething();
        ack.acknowledge();
    } catch (NeedLongDelayException e) {
        // 120초 message visibility timeout 설정
        visibility.extend(120);
    } catch (Exception e) {
        // 어이쿠.. 실패했네..
        // SQS queue의 Default Visibility Timeout 이후에 메시지 다시 나옴
    }
}
```


# 참고

- [5. Messaging - Spring Cloud AWS doc](https://cloud.spring.io/spring-cloud-static/spring-cloud-aws/2.0.3.RELEASE/multi/multi__messaging.html)
- [SetQueueAttributes - Amazon SQS](https://docs.aws.amazon.com/ko_kr/AWSSimpleQueueService/latest/APIReference/API_SetQueueAttributes.html)
- [Amazon SQS Message Timers](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-message-timers.html)
- [Amazon SQS Delay Queues](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-delay-queues.html)
- [Amazon SQS Visibility Timeout](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-visibility-timeout.html)
