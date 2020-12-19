---
layout: post
title:  "[AWS] SNS 구독 필터링(Subscription Filtering)"
date:   2020-12-18 22:18:00 +0900
published: true
categories: [ aws ]
tags: [ aws, sns, sqs, subscription, subscribe, filtering, filter, message, concern, interest, spring, spring cloud, spring cloud aws, message queue, message exchange ]
---

[AWS SNS](https://aws.amazon.com/ko/sns/)를 사용하면 한 곳으로 이벤트(메시지)를 발행하고, 여러 곳에서 동일한 이벤트를 받아볼 수 있다. 특정 서비스의 변경 이벤트를 한 곳(SNS)으로 발행하고, 이 이벤트가 관심 있는 서비스는 언제든 SQS 등을 연결해서 구독(subscription)을 추가하거나 중단할 수 있다. SNS에 연결된 SQS, Lambda, email, HTTP, HTTPS 등에 동일한 이벤트를 보내준다. RabbitMQ의 [exchange](https://www.rabbitmq.com/tutorials/tutorial-three-python.html)와 같은 역할을 한다.


# 모두 하나의 이벤트

이벤트를 SNS로 발행하다 보면 점점 모든 이벤트가 하나의 SNS에 몰리는 경우가 생긴다. (새로 추가한 이벤트가 기존과) 큰 차이가 없는데 기존 이벤트에 넣어버리지 하는 마음도 생기고 귀찮으니 이미 만들어져 있는거 쓰자 하는 마음도 생기나 보다.

SNS는 말 그대로 주제(topic)별로 이벤트를 보내는 것이기 때문에, 관심이 다르면 따로 만들어야 한다는게 개인적인 생각이다. 그래야 구독하는 입장에서도 내가 원하는 이벤트만 골라서 구독할 수 있고 불필요한 이벤트로 인한 자원 낭비도 없기 때문이다. 하지만 회사나 팀의 결정에 따라 어쩔 수 없는 경우도 생기기 마련이다.

이런 경우를 생각해 보자. 가게 시스템이 있고, 가게의 변경 이벤트를 모두 하나의 SNS로 보낸다. 가게의 `영업시작/종료 이벤트`나 `메뉴 변경 이벤트`, 각 메뉴의 `재고소진 이벤트` 등이 모두 한가지 형태의 이벤트로 보고 있다고 가정하자. 주문 시스템 입장에서는 모든 이벤트가 필요하다. 가게가 영업을 시작해야 주문을 할 수 있고, 지금 팔고 있는 메뉴인지 확인을 해야 하기 때문이다. 하지만 가게 검색 시스템 입장에서는 재고는 관심이 없고 가게가 영업중인지 여부(`영업시작/종료 이벤트`)만 관심 있을 수 있다. 물론 재고가 다 떨어진 가게를 검색에서 제외한다면 `재고소진 이벤트`를 받아야 할 수도 있지만 아직 그런 비즈니스는 없다. 이렇게 비즈니스에 맞춰서 원하는 이벤트를 골라 구독하면 되는 것이다.

{% include image.html file='/assets/img/2020-12-18-aws-sns-subscription-filtering1.png' alt='Subscribe all events' %}

그런데 하나의 SNS에 많은 종류의 이벤트를 보내게 되면 검색 시스템은 관심도 없는 `메뉴변경 이벤트`나 `재고소진 이벤트`를 함께 받아야 한다. 불필요한 이벤트를 받아야 하는 문제도 있고, 내 관심에 맞는 이벤트인지 확인하느라 정작 중요한 이벤트 처리가 늦어질 수도 있다.


# 구독 필터링

모든 이벤트를 받아야 하는 상황을 피해 보자. 2가지 작업을 해야 한다. 하나는 SNS에 메시지를 발행할 때 이 메시지는 어떤 내용을 담고 있는건지 관심사(`Message attribute`)를 표시해 주는 것이고, 다른 하나는 메시지를 수신할 때 내가 관심 있는 메시지만 고르는 것이다.

{% include image.html file='/assets/img/2020-12-18-aws-sns-subscription-filtering2.png' alt='Subscribe events which are interested' %}


## 메시지에 관심사 표시

SQS를 통해서 SNS 메시지를 받는 상황을 가정하자. 우선, 메시지를 보내는 쪽에서 메시지에 맞는 관심사를 `Message attribute`로 설정해서 보낸다. 가게 시스템은 `영업시작/종료 이벤트` 와 `메뉴 변경 이벤트`, 메뉴의 `재고소진 이벤트` 를 아래와 같은 `Message attribute` 를 추가해서 보낸다.

```json
{
    "shop_event_type": "LIVE_ON_OFF", // 영업시작/종료
    "shop_no": 123,
    "live": "ON",                     // 영업 시작
    "xxx": "yyy"
}

{
    "shop_event_type": "MENU_CHANGED", // 메뉴 변경
    "shop_no": 123,
    "xxx": "yyy"
}

{
    "shop_event_type": "IN_STOCK",     // 재고 변경
    "shop_no": 123,
    "stock": "IN_STOCK",               // 재고 생김
    "xxx": "yyy"
}
```

[Spring Cloud for AWS](https://spring.io/projects/spring-cloud-aws)를 사용한다면 아래처럼 attribute를 추가해서 전송하면 된다.

```java
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate;

NotificationMessagingTemplate notificationMessagingTemplate;
String topicName = "shop-changed";
ShopChangedEvent payload = new ShopChangedEvent(...);
Map<String, Object> headers = Map.of("shop_event_type", "LIVE_ON_OFF");

notificationMessagingTemplate.convertAndSend(topicName, payload, headers);

```


## 구독 관심사 필터링

메시지를 보내는 설정은 끝났으니, 구독할 때 원하는 것만 받을 수 있도록 필터링을 한다.

{% include image.html file='/assets/img/2020-12-18-aws-sns-subscription-filtering3.png' alt='Edit SNS subscription' %}

SNS subscription 설정 수정 화면을 보면 위처럼 `Subscription filter policy` 라는 부분이 있다. 여기에 내가 구독하고 싶은 `Message attribute`를 적어주면 된다. 아래처럼 주문 시스템은 모든 이벤트를 받을 수 있도록 필터링에 추가하고, 검색 시스템은 `영업시작/종료 이벤트(LIVE_ON_OFF)`만 추가해 줬다. (SNS Subscription을 바꿀 때 간혹 반영이 바로바로 되지 않고 조금 기다려야 하는 경우도 있으니 여유를 갖고 설정하자.)

```json
// 가게 변경이벤트 SNS 토픽 -> 주문 시스템 SQS
{
  "shop_event_type": [
    "LIVE_ON_OFF",
    "MENU_CHANGED",
    "IN_STOCK"
  ]
}

// 가게 변경이벤트 SNS 토픽 -> 검색 시스템 SQS
{
  "shop_event_type": [
    "LIVE_ON_OFF"
  ]
}
```


## 메시지 발송

준비는 끝났다. 메시지를 실제로 전송해서 확인해 보면 된다. Spring Cloud for AWS를 사용하면 위에 있는 코드처럼 header를 추가해서 전송하면 된다. 여기서는 간단하게 AWS Management Console에서 SNS에 직접 메시지를 전송해 본다.

{% include image.html file='/assets/img/2020-12-18-aws-sns-subscription-filtering4.png' alt='Publish message on SNS' %}

SNS 토픽 상단에 `Publish message` 버튼을 클릭하면 아래처럼 메시지 payload를 직접 채워서 전송할 수 있다. 이때, 아래에 있는 `Message attributes` 부분을 설정하면 된다.

{% include image.html file='/assets/img/2020-12-18-aws-sns-subscription-filtering5.png' alt='Publish message with Message attributes' %}

메시지 하나는 `shop_event_type`을 `LIVE_ON_OFF`로 설정해서 전송하고 또 다른 메시지는 `MENU_CHANGED`로 설정해서 전송하면, 아래처럼 주문 시스템 SQS에는 메시지가 2개 받아졌지만 검색 시스템 SQS에는 메시지가 1개만 들어온 것을 확인할 수 있다.

{% include image.html file='/assets/img/2020-12-18-aws-sns-subscription-filtering6.png' alt='Received messages from order SQS' %}

{% include image.html file='/assets/img/2020-12-18-aws-sns-subscription-filtering7.png' alt='Received messages from search SQS' %}

{% include google-ad-content %}


# 필터링 조건


## Prefix 매칭

위의 예제는 문자열 전체가 매칭되어야 하지만, `prefix`를 사용하면 **앞부분만 매칭**되는지 체크할 수도 있다. 때문에, prefix 형태로 메시지의 타입을 만들면 좋다. (e.g. `MENU_ADDED`, `MENU_CHANGED`, `MENU_DELETED`)

```json
{
  "shop_event_type": [
    {"prefix": "LIVE_"}, // 영업 시작/종료 관련 모든 이벤트
    {"prefix": "MENU_"}  // 메뉴 추가/변경/삭제 등 모든 메뉴 이벤트
  ]
}
```


## Exclude 매칭

`anything-but`를 사용하면 제외할 것들을 설정할 수 있다.

```json
{
  "shop_event_type": [
    // 메뉴와 재고 변경을 제외한 모든 이벤트
    {"anything-but": ["MENU_CHANGED", "IN_STOCK"]}
  ]
}
```


## And/Or 조건

Attribute 이름을 따로 주면 AND 조건이 되고, 배열(`[ .. ]`)로 나열하면 OR 조건이 된다. 아래 샘플은 `shop_event_type`이 `LIVE_ON_OFF`이면서, `shop_type`이 `delivery`인 경우(AND 조건)만 메시지를 받게 된다.

```json
{
  "shop_event_type": [
    "LIVE_ON_OFF"
  ],
  "shop_type": [
    "delivery"
  ]
}
```


## 숫자 비교 매칭

메시지 필터링은 [문자열 매칭뿐만 아니라 숫자 비교도 가능](https://aws.amazon.com/ko/about-aws/whats-new/2018/03/amazon-sns-introduces-new-message-filtering-operators/?sc_ichannel=ha&sc_icampaign=aware_sns-numeric-filtering&sc_isegment=en&sc_iplace=1up&sc_icontent=whats-new)하다고 한다. 특정 수치 이상/이하일 때만 받을 수 있는 필터링도 가능할 것 같다. 예를 들어, 1 부터 5 사이의 에러레벨이 있고, 레벨 4 이상만 필터링하면 에러레벨 4와 5만 받을 수 있다. ([Message Filtering Operators for Numeric Matching, Prefix Matching, and Blacklisting in Amazon SNS](https://aws.amazon.com/ko/blogs/compute/message-filtering-operators-for-numeric-matching-prefix-matching-and-blacklisting-in-amazon-sns/))


# 참고

- [주제에 게시된 메시지 필터링](https://aws.amazon.com/ko/getting-started/hands-on/filter-messages-published-to-topics/)
- [Amazon SNS subscription filter policies](https://docs.aws.amazon.com/sns/latest/dg/sns-subscription-filter-policies.html)
- [Spring Cloud for Amazon Web Services](https://spring.io/projects/spring-cloud-aws)
- [Amazon SNS Introduces New Message Filtering Operators](https://aws.amazon.com/ko/about-aws/whats-new/2018/03/amazon-sns-introduces-new-message-filtering-operators/?sc_ichannel=ha&sc_icampaign=aware_sns-numeric-filtering&sc_isegment=en&sc_iplace=1up&sc_icontent=whats-new)
- [Message Filtering Operators for Numeric Matching, Prefix Matching, and Blacklisting in Amazon SNS](https://aws.amazon.com/ko/blogs/compute/message-filtering-operators-for-numeric-matching-prefix-matching-and-blacklisting-in-amazon-sns/)
